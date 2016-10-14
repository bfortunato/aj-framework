//
//  AFLoader.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 12/05/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import UIKit

open class AFLoader {
    
    var hud: MBProgressHUD?
    
    public init() {
        
    }
    
    open class func show(title: String? = nil, message: String? = nil) -> AFLoader {
        let loader = AFLoader()
        loader.show(title: title, message: message)
        return loader
    }
    
    open func show(title: String? = nil, message: String? = nil) {
        let window = UIApplication.shared.windows.last!
        if (hud == nil) {
            hud = MBProgressHUD.showAdded(to: window, animated: true)
        }
        hud?.dimBackground = true
        hud?.label.text = message ?? ""
    }
    
    open func hide() {
        self.hud?.hide(animated: true)
    }
}
