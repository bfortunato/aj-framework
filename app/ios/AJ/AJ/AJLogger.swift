//
//  AJLogger.swift
//  AJ
//
//  Created by Bruno Fortunato on 04/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore

@objc
public protocol AJLoggerExport: JSExport {
    func __i(_ message: String)
    func __w(_ message: String)
    func __e(_ message: String)
}

@objc
open class AJLogger: NSObject, AJLoggerExport {
    open func __i(_ message: String) {
        NSLog("JI: \(message)")
    }
    
    open func __w(_ message: String) {
        NSLog("JW: \(message)")
    }
    
    open func __e(_ message: String) {
        NSLog("JE: \(message)")
    }
}

