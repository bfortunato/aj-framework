//
//  AJStorageManager.swift
//  AJ
//
//  Created by Bruno Fortunato on 02/05/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import ApplicaFramework
import JavaScriptCore

@objc
public protocol AJStorageManagerProtocol: JSExport {
    func read(_ path: String, _ cb: JSValue)
    func readText(_ path: String, _ cb: JSValue)
    func write(_ path: String, _ buffer: Int, _ cb: JSValue)
    func writeText(_ path: String, _ content: String, _ cb: JSValue)
    func delete(_ path: String, _ cb: JSValue)
    func exists(_ path: String, _ cb: JSValue)
}

@objc
open class AJStorageManager: NSObject, AJStorageManagerProtocol {
    let runtime: AJRuntime
    let handleQueue = DispatchQueue(label: "AJStorageManager", attributes: DispatchQueue.Attributes.concurrent)
    
    init(runtime: AJRuntime) {
        self.runtime = runtime
        
        super.init()
    }
    
    open func readText(_ path: String, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            let content = try? String(contentsOfFile: fullPath)
            if let content = content {
                cb.call(withArguments: [false, content])
            } else {
                cb.call(withArguments: [true, "Not found"])
            }
        }
    }
    
    open func read(_ path: String, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            let data = try? Data(contentsOf: URL(fileURLWithPath: fullPath))
            if let data = data {
                cb.call(withArguments: [false, AJBuffer.create(with: data)])
            } else {
                cb.call(withArguments: [true, "Not found"])
            }
        }
    }
    
    open func writeText(_ path: String, _ contents: String, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            do {
                try contents.write(toFile: fullPath, atomically: true, encoding: String.Encoding.utf8)
                cb.call(withArguments: [false, "OK"])
            } catch _ {
                cb.call(withArguments: [true, "Error writing text at \(fullPath)"])
            }
        }
    }
    
    open func write(_ path: String, _ buffer: Int, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            let data = AJBuffer.get(buffer)
            if let data = data {
                do {
                    try data.write(to: URL(fileURLWithPath: fullPath), options: NSData.WritingOptions.atomicWrite)
                    cb.call(withArguments: [false, "OK"])
                } catch _ {
                    cb.call(withArguments: [true, "Error writing bytes at \(fullPath)"])
                }
            } else {
                cb.call(withArguments: [true, "Error decoding data"])
            }
        }
    }
    
    open func delete(_ path: String, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            if FileManager.default.isDeletableFile(atPath: fullPath) {
                do {
                    try FileManager.default.removeItem(atPath: fullPath)
                } catch _ {
                    cb.call(withArguments: [true, "Error deleting file at path \(fullPath)"])
                }
            } else {
                cb.call(withArguments: [true, "File at path \(fullPath) is not deletable"])
            }
            
            cb.call(withArguments: [false, FileManager.default.fileExists(atPath: fullPath)])
        }
    }
    
    open func exists(_ path: String, _ cb: JSValue) {
        handleQueue.async {
            let base = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] 
            let fullPath = AFPathUtils.normalizePath(AFPathUtils.concat(base, path))
            
            cb.call(withArguments: [false, FileManager.default.fileExists(atPath: fullPath)])
        }
    }
}
