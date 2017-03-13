//
//  AJRuntime.swift
//  AJ
//
//  Created by Bruno Fortunato on 17/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore


open class AJRuntime {
    
    fileprivate var stores = [String: AJStore]()
    fileprivate var plugins = [String: AJPlugin]()
    
    open func get(store type: String) -> AJStore {
        let store = stores[type]
        
        if store != nil {
            return store!
        } else {
            let newStore = AJStore(runtime: self, type: type)
            stores[type] = newStore
            return newStore
        }
    }
    
    open func subscribe(to store: String, owner: AnyObject, subscription: @escaping Subscription) {
        get(store: store).subscribe(owner: owner, subscription: subscription)
        
        NSLog("Subscribed to store: \(store)")
    }
    
    open func unsubscribe(from store: String, owner: AnyObject) {
        get(store: store).unsubscribe(owner: owner)
        
        NSLog("Unsubscribed from store: \(store)")
    }
    
    open func tigger(store: String, data: AJObject) {
        get(store: store).trigger(data)
    }
    
    open func destroy() {
        for store in stores.values {
            store.unsubscribeAll()
        }
        
        stores.removeAll()
    }
    
    open func run(action: String, data: AJObject = AJObject.empty()) -> AJSemaphore {
        fatalError("Not implemented")
    }
    
    open func register(plugin: AJPlugin) {
        NSLog("Plugin registered: \(plugin.name)")
        
        plugins[plugin.name] = plugin
    }
    
    open func get(plugin: String) -> AJPlugin {
        guard let instance = plugins[plugin] else {
            fatalError("Plugin not registered: \(plugin)")
        }
        
        return instance
    }
    
    open func exec(plugin: String, fn: String, data: AJObject, callback: @escaping AJPluginCallback) {
        get(plugin: plugin).exec(fn, data: data, callback: callback)
    }
    
}

