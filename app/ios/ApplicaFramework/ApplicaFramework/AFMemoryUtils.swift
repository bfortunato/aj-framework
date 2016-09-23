//
//  AFMemoryUtils.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 13/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

open class Weak<T: AnyObject> {
    weak open var value: T?
    
    init(value: T) {
        self.value = value
    }
}
