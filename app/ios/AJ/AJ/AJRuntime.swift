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
    
    open func getStore(_ type: String) -> AJStore {
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
        getStore(store).subscribe(owner: owner, subscription: subscription)
        
        NSLog("Subscribed from store: \(store)")
    }
    
    open func unsubscribe(from store: String, owner: AnyObject) {
        getStore(store).unsubscribe(owner: owner)
        
        NSLog("Unsubscribed from store: \(store)")
    }
    
    open func tigger(store: String, data: AJObject) {
        getStore(store).trigger(data)
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
    
    open func exec(plugin: String, fn: String, data: AJObject) -> AJObject {
        return get(plugin: plugin).exec(fn, data: data)
    }
    
}

