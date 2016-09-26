//
//  AJBuffersManager.swift
//  AJ
//
//  Created by bruno fortunato on 01/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import JavaScriptCore

@objc
protocol AJBuffersManagerExports: JSExport {
    func create(_ data: String, _ cb: JSValue)
    func load(_ id: Int, _ cb: JSValue)
    func destroy(_ id: Int, _ cb: JSValue)
}

@objc
class AJBuffersManager: NSObject, AJBuffersManagerExports {
    
    let handleQueue = DispatchQueue(label: "AJStorageManager", attributes: DispatchQueue.Attributes.concurrent)
    
    func create(_ data: String, _ cb: JSValue) {
        handleQueue.async { 
            if let nsdata = Data(base64Encoded: data, options: NSData.Base64DecodingOptions(rawValue: 0)) {
                cb.call(withArguments: [false, AJBuffer.create(with: nsdata)])
            } else {
                cb.call(withArguments: [true, "Invalid base64 string"])
            }
        }
        
    }
    
    func load(_ id: Int, _ cb: JSValue) {
        handleQueue.async {
            if let data = AJBuffer.get(id)?.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0)) {
                cb.call(withArguments: [false, data])
            } else {
                cb.call(withArguments: [true, "Buffer not found"])
            }
        }
        
    }
    
    func destroy(_ id: Int, _ cb: JSValue) {
        handleQueue.async {
            AJBuffer.destroy(id)
            cb.call(withArguments: [false])
        }
    }
}
