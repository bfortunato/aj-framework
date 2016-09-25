//
//  AJRequire.swift
//  AJ
//
//  Created by Bruno Fortunato on 10/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import JavaScriptCore
import UIKit
import ApplicaFramework

@objc
open class AJRequire: NSObject {
    
    let context: JSContext
    var cache = [String: JSValue]()
    
    fileprivate var _currentRequireQueue = [String]()
    
    init(context: JSContext) {
        self.context = context
    }
    
    open func require(_ path: String) -> JSValue {
        let relativePath = _currentRequireQueue.last
        let moduleExt = "js"
        let moduleBase = AFPathUtils.removeExtension(AFPathUtils.normalizePath(AFPathUtils.concat(relativePath ?? "", path)))
        let moduleName = AFPathUtils.getName(moduleBase)
        var module = path
        var source: String? = nil
        
        let possibilities = [
            moduleBase + ".js",
            AFPathUtils.concat(moduleBase, "index.js"),
            AFPathUtils.concat(moduleBase, "\(moduleName).js"),
        ]
        
        for possibility in possibilities {
            if let cached = cache[possibility] {
                NSLog("Loading cached module \(possibility)")
                
                return cached
            }
            
            let appDir = "/assets/js/"
            let moduleDir = AFPathUtils.getFullPath(AFPathUtils.concat(appDir, possibility))
            
            let name = AFPathUtils.getName(possibility, includingExtension: false)
            if let url = Bundle.main.path(forResource: name, ofType: moduleExt, inDirectory: moduleDir) {
                source = try? String(contentsOfFile: url)
            }
            
            if source != nil {
                module = possibility;
                break;
            }
        }
        
        guard let code = source else {
            fatalError("Cannot load module \(path)")
        }
        
        print("Loading module \(module)")
        
        _currentRequireQueue.append(AFPathUtils.getBaseName(module))
        
        var fullSource =    "var __exports = __exports || {};\n"
        fullSource +=       "__exports['\(module)'] = (function() {\n"
        fullSource +=       "var module = { exports: {} };\n"
        fullSource +=       "var exports = module.exports;\n\n"
        fullSource +=       code
        fullSource +=       "\n\nreturn module;\n"
        fullSource +=       "})();\n"
        context.evaluateScript(fullSource)
        let exports = context.objectForKeyedSubscript("__exports").objectForKeyedSubscript(module).objectForKeyedSubscript("exports")
        
        cache[module] = exports
        
        if exports?.toDictionary() != nil {
            //NSLog(exports.toDictionary().description);
        }
        
        _currentRequireQueue.removeLast()
        
        return exports!
    }
}
