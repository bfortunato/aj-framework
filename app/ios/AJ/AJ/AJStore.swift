//
//  AJComponent.swift
//  AJ
//
//  Created by Bruno Fortunato on 17/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore
import ApplicaFramework
import Foundation

public typealias Subscription = (_ state: AJObject) -> Void

struct SubscriptionInfo {
    weak var owner: AnyObject?
    var subscription: Subscription
}

@objc
open class AJStore: NSObject {

    open let runtime: AJRuntime
    open let type: String
    
    open var state:AJObject?
    
    fileprivate var subscriptions = [SubscriptionInfo]()
    fileprivate let lock = NSLock()
    
    init(runtime: AJRuntime, type: String) {
        self.runtime = runtime;
        self.type = type;
        
        super.init()
    }
    
    open func subscribe(owner: AnyObject, subscription: @escaping Subscription) {
        lock.lock()
        subscriptions += [SubscriptionInfo(owner: owner, subscription: subscription)]
        lock.unlock();
    }
    
    open func unsubscribe(owner: AnyObject) {
        lock.lock()
        
        subscriptions = subscriptions.filter({$0.owner != nil})
        
        var index = -1
        var found = false
        for subscription in subscriptions {
            index += 1
            
            if let o = subscription.owner {
                if o === owner {
                    found = true
                    break;
                }
            }
        }
        
        if found {
            subscriptions.remove(at: index)
        }

        lock.unlock()
    }
    
    open func unsubscribeAll() {
        lock.lock()
        subscriptions.removeAll()
        lock.unlock()    
    }
    
    open func trigger(_ data: AJObject) {
        self.state = data
        
        var safeSubscriptions = [SubscriptionInfo]()
        safeSubscriptions += self.subscriptions.filter({$0.owner != nil})
        runui {
            for s in self.subscriptions {
                s.subscription(data)
            }
        }
    }
    
}
