//
//  AJObject.swift
//  AJ
//
//  Created by Bruno Fortunato on 17/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import ApplicaFramework

@objc
open class AJValueBase: NSObject {
    open var isObject: Bool {
        get {
            return self is AJObject
        }
    }
    
    open var isArray: Bool {
        get {
            return self is AJArray
        }
    }
    
    open var isValue: Bool {
        get {
            return self is AJValue
        }
    }

}

open class AJValue: AJValueBase {
    open let key: String
    open var value: Any?
    
    public init(key: String, value: Any?) {
        self.key = key
        self.value = value
    }
    
    open var string: String? {
        return value as? String
    }
    
    open var int: Int? {
        return value as? Int
    }
    

    open var bool: Bool? {
        return value as? Bool
    }
    
    open var double: Double? {
        return value as? Double
    }
    
    open var float: Float? {
        return value as? Float
    }
    
    open var cgFloat: CGFloat? {
        return value as? CGFloat
    }
    
    open var array: AJArray? {
        return value as? AJArray
    }
    
    open var image: UIImage? {
        return AJValue.toImage(buffer)
    }
    
    open var object: AJObject? {
        return value as? AJObject
    }
    
    open var buffer: Data? {
        if let id = int {
            return AJBuffer.get(id)
        }
        
        return nil
    }
    
    open var color: UIColor? {
        return AJValue.toColor(object)
    }
    
    static func toColor(_ object: AJObject?) -> UIColor? {
        if  let r = object?.get("r")?.cgFloat,
            let g = object?.get("g")?.cgFloat,
            let b = object?.get("b")?.cgFloat {
            let a = object?.get("a")?.cgFloat ?? CGFloat(255)
            
            return RGBA(r, g, b, a)
        }
        
        return nil
    }
    
    static func toBuffer(_ id: Int?) -> Data? {
        if let id = id {
            return AJBuffer.get(id)
        } else {
            return nil
        }

    }
    
    static func toImage(_ buffer: Data?) -> UIImage? {
        guard let buffer = buffer else {
            return nil
        }
        
        return UIImage(data: buffer)
    }
    
    open override func isEqual(_ object: Any?) -> Bool {
        if let other = object as? AJValue {
            if other.key != self.key {
                return false
            }
            
            if other.isObject {
                if !isObject {
                    return false
                }
                
                if other.object == nil && self.object == nil {
                    return true
                }
                
                if other.object == nil && self.object != nil {
                    return false
                }
                
                if other.object != nil && self.object == nil {
                    return false
                }
                
                return other.object!.isEqual(self.object!)
            } else if other.isArray {
                if other.isArray {
                    if !isArray {
                        return false
                    }
                    
                    if other.array == nil && self.array == nil {
                        return true
                    }
                    
                    if other.array == nil && self.array != nil {
                        return false
                    }
                    
                    if other.array != nil && self.array == nil {
                        return false
                    }
                    
                    return other.array!.isEqual(self.array!)
                }
            } else {
                if value == nil && other.value == nil {
                    return true
                }
                
                if (value != nil && other.value == nil) {
                    return false
                }
                
                if (value == nil && other.value != nil) {
                    return false
                }
                
                let a1 = value! as AnyObject
                let a2 = other.value! as AnyObject
                
                return  a1.isEqual(a2)
            }
        }
        
        return false
    }
    
}

open class AJObject: AJValueBase {
    var values = [AJValue]()
    
    var count: Int {
        return values.count
    }
    
    var first: AJValue? {
        return values[0]
    }
    
    public override init() {
        super.init()
    }
    
    public init(json: String) {
        super.init()
        guard let data = json.data(using: String.Encoding.utf8) else {
            NSLog("Error in json data: \(json)")
            return;
        }
        
        guard let jsonObject = try? JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments) else {
            NSLog("Error in json parse: \(json)")
            return;
        }
        
        guard let dict = jsonObject as? [String: Any] else {
            NSLog("Error in json cast: \(json)")
            return;
        }

        load(dict)
    }
    
    public init(value: Any) {
        super.init()
        
        _ = set("", value)
    }
    
    public init(dict: [String: Any]) {
        super.init()
        load(dict)
    }
    
    public init(key: String, value: Any) {
        super.init()
        
        _ = set(key, value)
    }
    
    fileprivate func _toAJValueValue(_ original: Any) -> Any {
        if original is [String: Any] {
            let child = AJObject()
            child.load(original as! [String: Any])
            return child
        } else if (original is NSArray || original is [Any]) {
            let arr = AJArray()
            for item in original as! NSArray {
                arr.append(_toAJValueValue(item as Any))
            }
            return arr
        } else {
            return original
        }

    }
    
    fileprivate func _fromAJValueValue(_ original: Any) -> Any {
        if original is AJObject {
            let child = (original as! AJObject).toDict()
            return child as Any
        } else if (original is AJArray) {
            var arr = [Any]()
            for v in (original as! AJArray).list {
                arr.append(_fromAJValueValue(v))
            }
            return arr as Any
        } else {
            return original
        }
        
    }
    

    
    open func load(_ data: [String: Any]) {
        for (key, value) in data {
            _ = set(key, _toAJValueValue(value))
        }
    }
    
    open subscript(key: String) -> Any? {
        get {
            return get(key)?.value
        }
         
        set(value) {
            _ = set(key, value)
        }
    }
    
    open func get(_ key: String) -> AJValue? {
        return values.filter { $0.key == key } .first
    }
    
    open func set(_ key: String, _ value: Any?) -> AJObject {
        var v = values.filter { $0.key == key } .first
        if v == nil {
            v = AJValue(key: key, value: value)
            values.append(v!)
        } else {
            v!.value = value
        }
        
        return self
    }
    
    open func toJson() -> String {
        guard let data = try? JSONSerialization.data(withJSONObject: toDict(), options: JSONSerialization.WritingOptions.prettyPrinted) else {
            fatalError("Cannot deserialize")
        }
        
        let json = String(data: data, encoding: String.Encoding.utf8)

        return json!
    }
    
    open func toDict() -> [String: Any] {
        var dict = [String: Any]()
        for v in values {
            if v.value != nil {
                dict[v.key] = _fromAJValueValue(v.value!)
            }
        }
        
        return dict
    }
    
    //MARK: single value or values converter
    
    open var string: String? {
        return first?.value as? String
    }
    
    open var int: Int? {
        return first?.value as? Int
    }
    
    open var bool: Bool? {
        return first?.value as? Bool
    }
    
    open var double: Double? {
        return first?.value as? Double
    }
    
    open var float: Float? {
        return first?.value as? Float
    }
    
    open var array: Array<Any>? {
        return first?.value as? Array
    }
    
    open var object: AJObject? {
        return first?.value as? AJObject
    }
    
    open var cgFloat: CGFloat? {
        return first?.value as? CGFloat
    }
    
    open var color: UIColor? {
        return first?.color
    }
    
    open var image: UIImage? {
        return first?.image
    }
    
    open override func isEqual(_ object: Any?) -> Bool {
        if let other = object as? AJObject {
            if other.values.count != self.values.count {
                return false
            }
            
            for value in self.values {
                let key = value.key
                if let otherValue = other.values.filter({$0.key == key}).first {
                    if !value.isEqual(otherValue) {
                        return false
                    }
                } else {
                    return false
                }
            }

            for otherValue in other.values {
                let key = otherValue.key
                if let value = self.values.filter({$0.key == key}).first {
                    if !value.isEqual(otherValue) {
                        return false
                    }
                } else {
                    return false
                }
            }

        } else {
            return false
        }
        
        return true
    }
    
    open static func create() -> AJObject {
        return AJObject()
    }
    
    open static func empty() -> AJObject {
        return AJObject()
    }

    open func at(path: String) -> AJValue? {
        return traverse(obj: self, path: path)
    }
    
    open func differs(at path: String) -> AJDiff {
        return AJDiff(original: self).at(path: path)
    }
    
    open func differs(from other: AJObject?) -> Bool {
        return AJDiff(original: other).differs(from: self)
    }
}

open class AJArray: AJValueBase {
    fileprivate var _internal = [Any]()
    
    open subscript(index: Int) -> Any {
        return _internal[index]
    }
    
    open var count: Int {
        get {
            return _internal.count
        }
    }
    
    open func append(_ item: Any) {
        _internal.append(item)
    }
    
    open func removeAtIndex(_ index: Int) {
        _internal.remove(at: index)
    }
    
    open func stringAt(_ index: Int) -> String? {
        return self[index] as? String
    }
    
    open func intAt(_ index: Int) -> Int? {
        return self[index] as? Int
    }
    
    open func boolAt(_ index: Int) -> Bool? {
        return self[index] as? Bool
    }
    
    open func doubleAt(_ index: Int) -> Double? {
        return self[index] as? Double
    }
    
    open func floatAt(_ index: Int) -> Float? {
        return self[index] as? Float
    }
    
    open func arrayAt(_ index: Int) -> AJArray? {
        return self[index] as? AJArray
    }
    
    open func objectAt(_ index: Int) -> AJObject? {
        return self[index] as? AJObject
    }
    
    open func cgFloatAt(_ index: Int) -> CGFloat? {
        return self[index] as? CGFloat
    }
    
    open func bufferAt(_ index: Int) -> Data? {
        return AJValue.toBuffer(intAt(index))
    }
    
    open func colorAt(_ index: Int) -> UIColor? {
        return AJValue.toColor(objectAt(index))
    }
    
    open func imageAt(_ index: Int) -> UIImage? {
        return AJValue.toImage(bufferAt(index))
    }

    open var list: [Any] {
        get {
            return _internal
        }
    }
    
    open override func isEqual(_ object: Any?) -> Bool {
        if let other = object as? AJArray {
            if other.list.count != self.list.count {
                return false
            }
            
            var index = 0
            for value in self.list {
                let otherValue = other.list[index]
                let anyValue = value as AnyObject
                let anyOtherValue = otherValue as AnyObject
                
                if !anyValue.isEqual(anyOtherValue) {
                    return false
                }            
                
                index += 1
            }
        } else {
            return false
        }
        
        return true
    }
}

func traverse(obj: AJObject, path: String) -> AJValue? {
    if let indexOfDot = path.range(of: ".") {
        let property = path.substring(to: indexOfDot.lowerBound)
        if let v = obj.get(property) {
            if let vo = v.object {
                let newPath = path.substring(from: indexOfDot.upperBound)
                return traverse(obj: vo as AJObject, path: newPath)
            } else {
                return v
            }
        } else {
            return nil
        }
    } else {
        return obj.get(path)
    }
    
}

open class AJDiff {
    var original: AJObject?
    
    public init(original: AJObject? = nil) {
        self.original = original
    }
    
    private var _path: String?
    
    open func at(path: String) -> AJDiff {
        _path = path
        return self
    }
    
    open func from(_ objectToCompare: AJObject) -> Bool {
        return differs(from: objectToCompare)
    }
    
    open func differs(from objectToCompare: AJObject) -> Bool {
        if let path = _path, let original = self.original {
            if let v = traverse(obj: original, path: path) {
                if let ov = traverse(obj: objectToCompare, path: path) {
                    return !v.isEqual(ov)
                } else {
                    return true
                }
            } else {
                if let _ = traverse(obj: objectToCompare, path: path) {
                    return true
                }
            }
        } else {
            return !objectToCompare.isEqual(original)
        }
        
        return false
    }
}

