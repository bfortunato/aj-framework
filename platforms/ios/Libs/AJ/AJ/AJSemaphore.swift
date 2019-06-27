//
//  AJFuture.swift
//  AJ
//
//  Created by Bruno Fortunato on 05/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import ApplicaFramework

public typealias AJSemaphoreListener = () -> Void
public typealias AJSemaphoreAction = () -> Void

var AJSemaphoreCounter = 0

open class AJSemaphore {
    
    open var complete = false
    open var id: Int
    
    var _listeners = [AJSemaphoreListener]()
    var _dispatchGroup: DispatchGroup? = nil
    
    init(action: AJSemaphoreAction?) {
        id = AJSemaphoreCounter
        AJSemaphoreCounter += 1
        if action != nil {
            run(action: action!)
        }
    }
    
    open func run(action: @escaping AJSemaphoreAction) {
        _dispatchGroup = ___async_group {
            action()
            self.free()
        }
    }
    
    fileprivate func free() {
        for listener in _listeners {
            listener()
        }
        
        complete = true
    }
    
    open func then(_ listener: @escaping AJSemaphoreListener) -> AJSemaphore {
        _listeners.append(listener)
        
        if complete {
            listener()
        }
        
        return self
    }
    
    open func await(_ timeout: Double? = nil) -> AJSemaphore {
        var time: DispatchTime = DispatchTime.distantFuture
        if let timeout = timeout {
            time = DispatchTime.now() + Double(Int64(timeout * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC);
        }
        
        _ = _dispatchGroup!.wait(timeout: time)
        
        return self
    }

}

open class AJManualSemaphore: AJSemaphore {
    
    var isGreen = false

    init() {
        super.init(action: nil)
        
        run(action: wait)
    }
    
    func wait() {
        let timeInterval = 0.01
        var elapsedTime = 0
        while !self.isGreen {
            Thread.sleep(forTimeInterval: timeInterval)
            elapsedTime += Int(timeInterval * 1000)
            /*
            if elapsedTime > 20000 {
                print("timeout")
                self.green()
            }
*/
        }
    }
    
    open func green() {
        self.isGreen = true;
    }
    
}

open class AJFakeSemaphore: AJSemaphore {
    override open func run(action: @escaping AJSemaphoreAction) {
        action()
        self.free()
    }
}
