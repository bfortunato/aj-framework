//
//  AJBuffer.swift
//  AJ
//
//  Created by bruno fortunato on 19/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit

var __lastBufferId = 1
var __buffers = [AJBuffer]()

open class AJBuffer {
    
    public let data: Data
    fileprivate let id: Int
    
    fileprivate var index: Int = 0
    
    fileprivate init(data: Data) {
        self.data = data
        __lastBufferId += 1
        self.id = __lastBufferId
        
        __buffers.append(self)
    }
    
    open class func create(with data: Data) -> Int {
        return AJBuffer(data: data).id
    }
    
    open class func get(_ id: Int, remove: Bool = true) -> Data? {
        for buffer in __buffers {
            if buffer.id == id {
                self.destroy(id)
                return buffer.data
            }
        }
        
        return nil
    }
    
    open class func destroy(_ id: Int) {
        if let index = __buffers.firstIndex(where: { $0.id == id }) {
            __buffers.remove(at: index)
        }        
    }
    
    open class func null() -> Int {
        return 0
    }
    
}
