//
// Created by Bruno Fortunato on 08/03/16.
// Copyright (c) 2016 Bruno Fortunato. All rights reserved.
//

import Foundation

@objc
open class AFTimeLimitedCall: NSObject {

    public typealias Action = () -> Void

    let action: Action
    let delay: TimeInterval

    fileprivate var timer: Timer?

    public init(delay: TimeInterval, action: @escaping Action) {
        self.delay = delay
        self.action = action
    }

    open func execute() {
        self.cancel()
        self.timer = Timer.scheduledTimer(timeInterval: delay, target: self, selector: #selector(AFTimeLimitedCall._run), userInfo: nil, repeats: false)
    }

    func _run() {
        action()
    }
    
    open func cancel() {
        if let actual = self.timer {
            actual.invalidate()
        }
    }

}
