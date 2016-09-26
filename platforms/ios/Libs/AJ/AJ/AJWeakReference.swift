//
//  AJWeakReference.swift
//  AJ
//
//  Created by Bruno Fortunato on 09/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

open class AJWeakReference<T: AnyObject> {
    
    open weak var ref: T?
    
    init(ref: T) {
        self.ref = ref
    }

}
