//
//  AJPlugin.swift
//  AJ
//
//  Created by Bruno Fortunato on 30/08/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

@objc
open class AJPlugin : NSObject {
    
    open let name: String
    
    public init(name: String) {
        self.name = name
        super.init()
    }
    
    open func exec(_ fn: String, data: AJObject) -> AJObject {
        if self.responds(to: Selector(fn + ":")) {
            let result = self.perform(Selector(fn + ":"), with: data)
            let ret = result?.takeUnretainedValue() as? AJObject
            return ret ?? AJObject.empty()
        } else {
            fatalError("Cannot execute plugin method " + name + "." + fn)
        }
    }
    
}
