//
//  AJPlugin.swift
//  AJ
//
//  Created by Bruno Fortunato on 30/08/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import ApplicaFramework

public typealias AJPluginCallback = (_ error: Bool, _ result: AJObject?) -> Void

@objc
public class AJPluginCallData : NSObject {
    init(argument: AJObject, callback: @escaping AJPluginCallback) {
        self.argument = argument
        self.callback = callback
    }
    
    public let argument: AJObject
    public let callback: AJPluginCallback
}

@objc
open class AJPlugin : NSObject {
    
    open let name: String
    
    public init(name: String) {
        self.name = name
        super.init()
    }
    
    open func exec(_ fn: String, data: AJObject, callback: @escaping AJPluginCallback) {
        if self.responds(to: Selector(fn + ":")) {
            _ = self.perform(Selector(fn + ":"), with: AJPluginCallData(argument: data, callback: callback))
        } else {
            fatalError("Cannot execute plugin method " + name + "." + fn)
        }
    }
    
}
