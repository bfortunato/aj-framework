//
//  AFAsyncUtils.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

let asyncQueue = DispatchQueue(label: "ApplicaFramework.asyncQueue", attributes: DispatchQueue.Attributes.concurrent)

public func runui(_ action:@escaping (() -> Void)) {
    DispatchQueue.main.async(execute: { () -> Void in
        action()
    })
}

public func async(_ action:@escaping (() -> Void)) {
    asyncQueue.async { () -> Void in
        action()
    }
}

public func async_group(_ action:@escaping (() -> Void)) -> DispatchGroup {
    let group = DispatchGroup()
    asyncQueue.async(group: group) { () -> Void in
        action()
    }
    return group
}

public func delay(_ delay: Double, closure:@escaping () -> Void) {
    DispatchQueue.main.asyncAfter(
        deadline: DispatchTime.now() + Double(Int64(delay * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC), execute: closure)
}
