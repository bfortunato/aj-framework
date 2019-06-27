//
//  AJThread.swift
//  AJ
//
//  Created by Bruno Fortunato on 27/06/2019.
//  Copyright © 2019 Bruno Fortunato. All rights reserved.
//

import Foundation

public class AJThread {
    
    static let asyncQueue = DispatchQueue(label: "AJ.asyncQueue")
    
    public class func runui(_ action:@escaping (() -> Void)) {
        DispatchQueue.main.async(execute: { () -> Void in
            action()
        })
    }
    
    public class func async(_ action:@escaping (() -> Void)) {
        asyncQueue.async { () -> Void in
            action()
        }
    }
    
    public class func delay(_ delay: Double, closure:@escaping () -> Void) {
        DispatchQueue.main.asyncAfter(
            deadline: DispatchTime.now() + Double(Int64(delay * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC), execute: closure)
    }
    
}
