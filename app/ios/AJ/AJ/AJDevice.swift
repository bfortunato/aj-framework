//
//  AJDevice.swift
//  AJ
//
//  Created by Bruno Fortunato on 26/08/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore

@objc
protocol AJDeviceExports: JSExport {
    func getScale() -> Float
    func getHeight() -> Int
    func getWidth() -> Int
}

@objc
class AJDevice: NSObject, AJDeviceExports {
   
    func getScale() -> Float {
        return Float(UIScreen.main.scale)
    }
    
    func getWidth() -> Int {
        return Int(UIScreen.main.bounds.width)
    }
    
    func getHeight() -> Int {
        return Int(UIScreen.main.bounds.height)
    }
}
