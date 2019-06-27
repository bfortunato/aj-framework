//
//  AFStringUtils.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit

public extension String {
    func beginsWith (_ str: String) -> Bool {
        if let range = self.range(of: str) {
            return range.lowerBound == self.startIndex
        }
        return false
    }
    
    func endsWith (_ str: String) -> Bool {
        if let range = self.range(of: str, options:NSString.CompareOptions.backwards) {
            return range.upperBound == self.endIndex
        }
        return false
    }
}
