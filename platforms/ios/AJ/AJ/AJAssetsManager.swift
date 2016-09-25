//
//  AJAssetsManager.swift
//  AJ
//
//  Created by Bruno Fortunato on 09/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore
import ApplicaFramework

@objc
public protocol AJAssetsManagerProtocol: JSExport {
    func load(_ path: String, _ cb: JSValue)
}

@objc
open class AJAssetsManager: NSObject, AJAssetsManagerProtocol {

    let runtime: AJRuntime
    let handleQueue = DispatchQueue(label: "AJ.AJAssetsManager.handleQueue", attributes: DispatchQueue.Attributes.concurrent)
    
    init(runtime: AJRuntime) {
        self.runtime = runtime
        
        super.init()
    }
    
    open func load(_ path: String, _ cb: JSValue) {
        handleQueue.async {
            let base = "/assets/"
            let normalizedPath = AFPathUtils.normalizePath(path)
            let dir = AFPathUtils.getBaseName(normalizedPath)
            let fullDir = AFPathUtils.concat(base, dir)
            let name = AFPathUtils.getName(normalizedPath, includingExtension: false)
            let type = AFPathUtils.getExtension(normalizedPath)
            
            guard let url = Bundle.main.path(forResource: name, ofType: type, inDirectory: fullDir) else {
                cb.call(withArguments: [true, "Asset not found: \(path)"])
                return
            }
            
            guard let data = try? Data(contentsOf: URL(fileURLWithPath: url)) else {
                cb.call(withArguments: [true, "Error loading asset: \(path)"])
                return
            }
            
            cb.call(withArguments: [false, AJBuffer.create(with: data)])
        }
    }
    
    open func exists(_ path: String, _ cb: JSValue) {
        cb.call(withArguments: [false, true])
    }
    
}
