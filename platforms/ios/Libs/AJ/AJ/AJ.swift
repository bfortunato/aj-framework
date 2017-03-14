//
//  AJ.swift
//  AJ
//
//  Created by Bruno Fortunato on 10/10/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

open class AJ {
    
    open class func get(store type: String) -> AJStore {
        return AJApp.runtime().get(store: type)
    }
    
    open class func subscribe(to store: String, owner: AnyObject, subscription: @escaping Subscription) {
        AJApp.runtime().subscribe(to: store, owner: owner, subscription: subscription)
    }
    
    open class func unsubscribe(from store: String, owner: AnyObject) {
        AJApp.runtime().unsubscribe(from: store, owner: owner)
    }
    
    open class func run(action: String, data: AJObject = AJObject.empty()) -> AJSemaphore {
        return AJApp.runtime().run(action: action, data: data)
    }
    
    open class func register(plugin: AJPlugin) {
        AJApp.runtime().register(plugin: plugin)
    }
    
    open class func get(plugin: String) -> AJPlugin {
        return AJApp.runtime().get(plugin: plugin)
    }
    
    open class func exec(plugin: String, fn: String, data: AJObject, callback: AJPluginCallback? = nil) {
        return AJApp.runtime().exec(plugin: plugin, fn: fn, data: data, callback: callback ?? { (s, r) in })
    }
    
}
