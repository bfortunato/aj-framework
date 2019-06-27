//
//  AJTimers.swift
//  AJ
//
//  Created by bruno fortunato on 17/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore
import ApplicaFramework

let asyncQueue = DispatchQueue(label: "ApplicaFramework.timersAsyncQueue")

@objc
protocol AJTimersProtocol {
    func setTimeout(_ action: JSValue, _ delay: Int) -> Int
    func setInterval(_ action: JSValue, _ delay: Int) -> Int
    func clearTimeout(_ timerId: Int)
    func clearInterval(_ timerId: Int)
}

var AJTimerActionCounter = 0

class AJTimerAction {
    let id: Int
    var timer: Timer?
    let action: JSValue
    let delayTime: Int
    var loop = false
    var complete = false
    var canceled = false
    
    init(action: JSValue, delay: Int) {
        AJTimerActionCounter += 1
        id = AJTimerActionCounter
        self.action = action
        self.delayTime = delay
    }
    
    deinit {
    }
    
    func execute() {
        if delayTime > 0 {
            AJThread.delay(Double(self.delayTime) / 1000.0) { [weak self] () -> Void in
                if let me = self {
                    if (!me.canceled) {
                        me._run()
                    }
                    
                    me.complete = true;
                }
            }
        } else {
            self._run()
            self.complete = true;
        }
        
    }
    
    @objc func _run() {
        self.action.call(withArguments: [])
        if (!self.loop) {
            self.complete = true;
        }
    }
    
    func cancel() {
        self.canceled = true;
    }
}

@objc
class AJTimers: NSObject, AJTimersProtocol {
    
    var actions = [AJTimerAction]()
    
    func setTimeout(_ action: JSValue, _ delay: Int) -> Int {
        let timerAction = AJTimerAction(action: action, delay: delay)
        timerAction.loop = false
        
        append(action: timerAction)
        
        timerAction.execute()
        
        return timerAction.id
    }
    
    func setInterval(_ action: JSValue, _ delay: Int) -> Int {
        let timerAction = AJTimerAction(action: action, delay: delay)
        timerAction.loop = true
        
        actions.append(timerAction)
        
        timerAction.execute()
        
        return timerAction.id
    }
    
    func append(action: AJTimerAction) {
        //first of all, clear completed timer actions
        actions = actions.filter({!$0.complete})
        actions.append(action)
        
    }
    
    func clearTimeout(_ timerId: Int) {
        if let timerAction = actions.filter({ $0.id == timerId }).first {
            timerAction.cancel()
        }
    }
    
    func clearInterval(_ timerId: Int) {
        clearTimeout(timerId)
    }
    
}
