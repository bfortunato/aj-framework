//
//  AJApplication.swift
//  AJ
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import AJ
import ApplicaFramework

var __AJApp_instance: AJApp?

open class AJApp {

    open var _runtime: AJRuntime?
    open var debug = false
    open var socketUrl = "ws://localhost:3000"
    
    open class func current() -> AJApp {
        return __AJApp_instance!
    }
    
    open class func runtime() -> AJRuntime {
        return current()._runtime!
    }
    
    open class func plugin(_ name: String) -> AJPlugin {
        return current()._runtime!.get(plugin: name)
    }

    public init() {
        if (__AJApp_instance != nil) {
            fatalError("AJApp already instantiated. It's possible to have only one instance of AJApp")
        }
        
        __AJApp_instance = self
    }

    func createWebSocketRuntime() -> AJRuntime {
        return AJWebSocketRuntime(url: URL(string: socketUrl)!)
    }

    func createNativeRuntime() -> AJRuntime {
        return AJJavaScriptCoreRuntime()
    }

    open func initialize() {
        if (_runtime == nil) {
            if debug {
                _runtime = createWebSocketRuntime()
            } else {
                _runtime = createNativeRuntime()
            }
        }

    }

    open func createRoot() -> UIViewController {
        return UIViewController()
    }
    
    open func reload() {
        if let runtime = self._runtime {
            runtime.destroy()
            self._runtime = nil
        }

        initialize()
    }
    
}
