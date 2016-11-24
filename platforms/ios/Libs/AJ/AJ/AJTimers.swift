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
    let action: JSValue
    let delayTime: Int
    var canceled: Bool = false
    var destroyed: Bool = false
    var loop = false

    weak var timers: AJTimers?

    init(action: JSValue, delay: Int) {
        AJTimerActionCounter += 1
        id = AJTimerActionCounter
        self.action = action
        self.delayTime = delay
    }

    func execute() {
        async {
            while true {
                Thread.sleep(forTimeInterval: Double(self.delayTime) / 1000.0)
                if (!self.canceled) {
                    self.action.call(withArguments: [])

                    if (!self.loop) {
                        break
                    }
                } else {
                    break
                }
            }

        }
    }

    func cancel() {
        canceled = true
    }

    func destroy() {
        destroyed = true
    }
}

@objc
class AJTimers: NSObject, AJTimersProtocol {

    var actions = [AJTimerAction]()

    func setTimeout(_ action: JSValue, _ delay: Int) -> Int {
        let timerAction = AJTimerAction(action: action, delay: delay)
        timerAction.loop = false
        timerAction.timers = self

        append(action: timerAction)

        timerAction.execute()

        return timerAction.id
    }

    func setInterval(_ action: JSValue, _ delay: Int) -> Int {
        let timerAction = AJTimerAction(action: action, delay: delay)
        timerAction.loop = true
        timerAction.timers = self

        actions.append(timerAction)

        timerAction.execute()

        return timerAction.id
    }

    func append(action: AJTimerAction) {
        //first of all, clear destroyed times
        actions = actions.filter({$0.id == action.id})
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