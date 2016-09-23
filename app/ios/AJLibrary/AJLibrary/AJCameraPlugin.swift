//
//  AJCameraPlugin.swift
//  AJLibrary
//
//  Created by Bruno Fortunato on 06/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import AJ
import ApplicaFramework

open class AJQRCodeScannerPlugin: AJPlugin {
    
    var _camera: AFQRCodeScanner?
    
    var store: String?
    var qrCodeAction: String?
    var initialized = false
    
    public init() {
        super.init(name: "QRCodeScanner")
    }
    
    open func configure(in view: UIView, owner: UIViewController) {
        if _camera != nil {
            _camera?.close()
            _camera = nil
        }
        
        initialized = false
        _camera = AFQRCodeScanner(view: view, owner: owner)
    }
    
    
    open func open(_ data: AJObject) -> AJObject {
        if (_camera == nil) {
            fatalError("Camera not configured. Call method AJCameraPlugin.configureInView to make camera available")
        }
        
        if !initialized {
            self.qrCodeAction = data.get("onQrCodeAction")?.string
            if self.qrCodeAction == nil {
                fatalError("Please specify QRCode action")
            }

            _camera?.onQRCode += { (s, p) -> Void in
                if let result = p?["result"] as? String {
                    if let store = self.store, let action = self.qrCodeAction {
                        AJApp.runtime().run(action: action, data: AJObject.create().set("result", result as AnyObject?))
                    }
                }
            }
            
            initialized = true
        }
        
        _camera?.open()
        
        return AJObject.empty()
    }
    
    open func close(_ data: AJObject) -> AJObject {
        if (_camera == nil) {
            fatalError("Camera not configured. Call method AJCameraPlugin.configureInView to make camera available")
        }
        
        _camera?.close()
        
        return AJObject.empty()
    }
}
