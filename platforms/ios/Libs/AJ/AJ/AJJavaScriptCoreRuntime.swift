//
//  AJJavaScriptCoreRuntime.swift
//  AJ
//
//  Created by Bruno Fortunato on 18/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore
import ApplicaFramework

open class AJJavaScriptCoreRuntime: AJRuntime {

    let jsContext: JSContext
    let require: AJRequire
    let timers: AJTimers
    
    var jsRuntime: JSValue?
    
    public override init() {
        jsContext = JSContext()
        require = AJRequire(context: jsContext)
        timers = AJTimers()
        super.init()
        
        jsContext.exceptionHandler = { context, exception in
            print("JS Error: \(exception)")
        }
        
        let aj_require: @convention(block) (String) -> JSValue = { path in
            return self.require.require(path)
        }
        
        let aj_async: @convention(block) (JSValue) -> Void = { action in
            async({
                action.call(withArguments: [])
            })
        }
        
        let aj_setTimeout: @convention(block) (JSValue, Int) -> Int = { action, time in
            return self.timers.setTimeout(action, time)
        }
        
        let aj_setInterval: @convention(block) (JSValue, Int) -> Int = { action, time in
            return self.timers.setInterval(action, time)
        }
        
        let aj_clearTimeout: @convention(block) (Int) -> Void = { timerId in
            return self.timers.clearTimeout(timerId)
        }
        
        let aj_clearInterval: @convention(block) (Int) -> Void = { timerId in
            return self.timers.clearInterval(timerId)
        }
        
        let aj_trigger: @convention(block) (String, JSValue) -> Void = { (store, data) in
            let dict = data.toDictionary() as? [String: AnyObject]
            let state: AJObject = dict != nil ? AJObject(dict: dict!) : AJObject.empty()

            self.tigger(store: store, data: state)
        }
        
        let aj_exec: @convention(block) (String, String, JSValue, JSValue) -> Void = { (plugin, fn, data, callback) in
            let dict = data.toDictionary() as? [String: Any]
            let arguments: AJObject = dict != nil ? AJObject(dict: dict!) : AJObject.empty()
            
            self.exec(plugin: plugin, fn: fn, data: arguments) { (error, result) in
                callback.call(withArguments: [error, result?.toDict() ?? [String: AnyObject]()])
            }
        }
        
        jsContext.globalObject.setObject(jsContext.globalObject, forKeyedSubscript: "global" as (NSCopying & NSObjectProtocol)!)
        
        let platform = ["engine": "native", "device": "iOS"]
        jsContext.globalObject.setObject(platform, forKeyedSubscript: "platform" as (NSCopying & NSObjectProtocol)!)
        
        //used by js components to notify the native parts
        jsContext.globalObject.setObject(unsafeBitCast(aj_trigger, to: AnyObject.self), forKeyedSubscript: "__trigger" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_exec, to: AnyObject.self), forKeyedSubscript: "__exec" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_require, to: AnyObject.self), forKeyedSubscript: "require" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_async, to: AnyObject.self), forKeyedSubscript: "async" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_setTimeout, to: AnyObject.self), forKeyedSubscript: "setTimeout" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_setInterval, to: AnyObject.self), forKeyedSubscript: "setInterval" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_clearTimeout, to: AnyObject.self), forKeyedSubscript: "clearTimeout" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(unsafeBitCast(aj_clearInterval, to: AnyObject.self), forKeyedSubscript: "clearInterval" as (NSCopying & NSObjectProtocol)!)

        jsContext.globalObject.setObject(AJLogger(), forKeyedSubscript: "logger" as (NSCopying & NSObjectProtocol)!)
        jsContext.evaluateScript("logger.i = function() { logger.__i(Array.prototype.join.call(arguments, ' ')); }")
        jsContext.evaluateScript("logger.w = function() { logger.__w(Array.prototype.join.call(arguments, ' ')); }")
        jsContext.evaluateScript("logger.e = function() { logger.__e(Array.prototype.join.call(arguments, ' ')); }")
        
        jsContext.globalObject.setObject(AJHttpClient(runtime: self), forKeyedSubscript: "__httpClient" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(AJAssetsManager(runtime: self), forKeyedSubscript: "__assetsManager" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(AJStorageManager(runtime: self), forKeyedSubscript: "__storageManager" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(AJBuffersManager(), forKeyedSubscript: "__buffersManager" as (NSCopying & NSObjectProtocol)!)
        jsContext.globalObject.setObject(AJDevice(), forKeyedSubscript: "device" as (NSCopying & NSObjectProtocol)!)
        
        let aj_createRuntime = require.require("./aj").objectForKeyedSubscript("createRuntime")
        let main = require.require("./main").objectForKeyedSubscript("main")
        self.jsRuntime = aj_createRuntime?.call(withArguments: [])
        _ = main?.call(withArguments: [])
    }
    
    open override func run(action: String, data: AJObject = AJObject.empty()) -> AJSemaphore {
        if let jr = self.jsRuntime {
            let semaphore = AJSemaphore(action: { () -> Void in
                let dict = data.toDict()
                jr.invokeMethod("run", withArguments: [action, dict])
            })
            return semaphore
        }
        
        fatalError("jsRuntime not initialized")
    }
    
}
