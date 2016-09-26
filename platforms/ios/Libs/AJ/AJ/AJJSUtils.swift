//
//  AJJSUtils.swift
//  AJ
//
//  Created by Bruno Fortunato on 02/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import JavaScriptCore

func makeResponse(withValue value: AnyObject, error: Bool = false, message: String = "") -> [String: AnyObject] {
    return [
        "value": value,
        "error": error as AnyObject,
        "message": message as AnyObject
    ]
}
