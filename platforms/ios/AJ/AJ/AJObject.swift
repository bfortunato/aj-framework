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
open class AJValue: NSObject {
    open let key: String
    open var value: Any?
    
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

    
    public init(key: String, value: Any?) {
        self.key = key
        self.value = value
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
    
}

@objc
open class AJObject: NSObject {
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
        
        set("", value)
    }
    
    public init(dict: [String: Any]) {
        super.init()
        load(dict)
    }
    
    public init(key: String, value: Any) {
        super.init()
        set(key, value)
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
            set(key, _toAJValueValue(value))
        }
    }
    
    open subscript(key: String) -> Any? {
        get {
            return get(key)?.value
        }
         
        set(value) {
            set(key, value)
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
    
    open static func create() -> AJObject {
        return AJObject()
    }
    
    open static func empty() -> AJObject {
        return AJObject()
    }

}

open class AJArray {
    fileprivate var _internal = [Any]()
    
    public init() {
        
    }
    
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
    
}

