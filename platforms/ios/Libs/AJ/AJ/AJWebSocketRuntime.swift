 //
 //  AJWebSocketRuntime.swift
 //  AJ
 //
 //  Created by Bruno Fortunato on 17/02/16.
 //  Copyright © 2016 Bruno Fortunato. All rights reserved.
 //
 
 import UIKit
 import SocketIO
 
 open class AJWebSocketRuntime: AJRuntime {
    
    let io: SocketIOClient
    
    private var _connecting = false
    
    var semaphores = [AJManualSemaphore]()
    
    public init(url: URL) {
        let handleQueue = DispatchQueue(label: "AJ.AJWebSocketRuntime.handleQueue", attributes: DispatchQueue.Attributes.concurrent)
        io = SocketIOClient(socketURL: url, config: [.log(false), .handleQueue(handleQueue), .forceWebsockets(true), .reconnects(false)])
        super.init()
        
        io.on("trigger") { (data, ack) -> Void in
            let store = data[0] as! String
            let dict = data[1] as? [String: AnyObject]
            let argument: AJObject = dict != nil ? AJObject(dict: dict!) : AJObject.empty()
            
            self.tigger(store: store, data: argument)
            
            ack.with()
        }
        
        io.on("exec") { (data, ack) -> Void in
            let plugin = data[0] as! String
            let fn = data[1] as! String
            let dict = data[2] as? [String: AnyObject]
            let argument: AJObject = dict != nil ? AJObject(dict: dict!) : AJObject.empty()
            
            _ = self.exec(plugin: plugin, fn: fn, data: argument) { (error, result) in
                ack.with(error, result?.toJson() ?? "{}")
            }
        }
        
        io.on("device") { (data, ack) -> Void in
            let device = AJObject.create()
                .set("scale", Float(UIScreen.main.scale) as AnyObject?)
                .set("height", Int(UIScreen.main.bounds.height) as AnyObject?)
                .set("width", Int(UIScreen.main.bounds.width) as AnyObject?)
            
            
            ack.with(device.toJson())
        }
        
        io.on("freeSemaphore") { data, ack in
            let id = data[0] as! Int
            self.freeSemaphore(id)
        }
        
        io.on("createBuffer") { data, ack in
            guard let base64 = data[0] as? String else {
                ack.with(true, "Bad data")
                return
            }
            
            guard let bytes = Data(base64Encoded: base64, options: NSData.Base64DecodingOptions(rawValue: 0)) else {
                ack.with(true, "Bad data")
                return
            }
            
            let id = AJBuffer.create(with: bytes)
            ack.with(false, id)
        }
        
        io.on("readBuffer") { data, ack in
            guard let id = data[0] as? Int else {
                ack.with(true, "Bad data")
                return
            }
            
            guard let base64 = AJBuffer.get(id)?.base64EncodedData(options: Data.Base64EncodingOptions(rawValue: 0)) else {
                ack.with(true, "Bad data")
                return
            }
            
            ack.with(false, base64)
        }
        
        io.on("connect") { data, ack in
            print("Connected to web socket server")
            self._connecting = false
        }
        
        _connecting = true
        io.connect()
    }
    
    open override func run(action: String, data: AJObject = AJObject.empty()) -> AJSemaphore {
        let json = data.toJson();
        
        while _connecting {
            NSLog("Waiting for connection with web socket server...")
            Thread.sleep(forTimeInterval: 0.25)
        }
        
        NSLog("Calling \(action) with data: \(json)")
        
        let semaphore = AJManualSemaphore()
        semaphores.append(semaphore)

        self.io.emitWithAck("run", action, json).timingOut(after: 0) { (data) in
            self.freeSemaphore(semaphore.id)
        }
        
        return semaphore
    }
    
    func freeSemaphore(_ id: Int) {
        var index = -1
        var found = false
        for semaphore in semaphores {
            index += 1
            
            if semaphore.id == id {
                found = true
                semaphore.green()
                break;
            }
        }
        
        if found {
            semaphores.remove(at: index)
        }
    }
    
    open override func destroy() {
        super.destroy()
        
        semaphores = []
        io.disconnect()
    }
    
    
 }
