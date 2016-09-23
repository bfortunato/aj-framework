//
//  AFEvent.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit

open class AFEvent {
    public typealias Callback = ((_ sender: AnyObject, _ params:[String: Any]?) -> Void)

    public init() {
        
    }

    var callbacks:[Callback] = []

    open func add(callback:@escaping Callback) {
        callbacks.append(callback)
    }

    open func invoke(sender: AnyObject, params:[String: Any]? = nil) {
        for c in callbacks {
            c(sender, params)
        }
    }

    open func clear() {
        callbacks = []
    }
}

public func += (left: inout AFEvent, right: @escaping AFEvent.Callback) {
    left.add(callback: right)
}
