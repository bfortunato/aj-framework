//
//  AFPathUtils.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit

open class AFPathUtils {
    
    public static let separator: String = "/"
    
    open class func normalizePath(_ path: String, withEndingSeparator:Bool = false) -> String {
        var path = path.replacingOccurrences(of: "\\", with: separator)
        
        let paths = path.split{$0 == Character(separator)}.map(String.init)
        var normalized = [String]()
        for p in paths {
            if p == ".." {
                normalized.removeLast()
            } else if p != "." {
                normalized.append(p)
            }
        }
        
        if path.starts(with: separator) {
            path = "/"
        } else {
            path = ""
        }
        path = path + normalized.joined(separator: separator)
        
        if withEndingSeparator {
            if !path.endsWith(separator) {
                path += separator
            }
        }
        
        return path
    }
    
    open class func getName(_ path: String, includingExtension:Bool = true) -> String {
        let path: String = normalizePath(path, withEndingSeparator: false)
        let r = path.range(of: separator, options: .backwards)
        if let r = r {
            let name: String = String(path[r.upperBound...])
            if !includingExtension {
                let xr = name.range(of: ".", options: .backwards)
                if let xr = xr {
                    return String(name[..<xr.lowerBound])
                }
            }
            
            return name
        }
        
        if !includingExtension {
            let xr = path.range(of: ".", options: .backwards)
            if let xr = xr {
                return String(path[..<xr.lowerBound])
            }
        }
        
        return path
        
    }
    
    open class func getExtension(_ path: String) -> String {
        let path = getName(path)
        let xr = path.range(of: ".", options: .backwards)
        if let xr = xr {
            return String(path[xr.upperBound...])
        }
        
        return ""
    }
    
    open class func removeExtension(_ path: String) -> String {
        let xr = path.range(of: ".", options: .backwards)
        if let xr = xr {
            return String(path[..<xr.lowerBound])
        }
        
        return path
    }
    
    open class func getFullPath(_ path: String) -> String {
        var path = normalizePath(path, withEndingSeparator: false)
        if !path.endsWith(separator) {
            let sr = path.range(of: separator, options: .backwards)
            if let sr = sr {
                path = String(path[..<sr.lowerBound])
            }
        }
        
        return path
    }
    
    open class func getBaseName(_ path: String) -> String {
        let path = normalizePath(path, withEndingSeparator: false)
        
        let sr = path.range(of: separator, options: .backwards)
        if sr == nil {
            return ""
        }
        
        return String(path[..<sr!.lowerBound])
        
    }
    
    open class func concat(_ path1: String, _ path2: String) -> String {
        return normalizePath(path1 + separator + path2)
    }
    
}
