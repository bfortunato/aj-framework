//
//  AJTests.swift
//  AJTests
//
//  Created by bruno fortunato on 25/01/2017.
//  Copyright © 2017 Bruno Fortunato. All rights reserved.
//

import XCTest
import AJ

class AJTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testDiff() {
        let me = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let equal = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff1 = "{ " +
            "\"name\": \"bruno different\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff2 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": false, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff3 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 5, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff4 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff5 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5], \"last\"]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff6 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43, \"otherPropInside\": 3242378}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao\", \"prop2\": 234}" +
        "}";
        
        let diff7 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop1\": \"ciao ciao\", \"prop2\": 234}" +
        "}";
        
        let diff8 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]," +
            "\"inner\": { \"prop2\": 234}" +
        "}";
        
        let diff9 = "{ " +
            "\"name\": \"bruno\", " +
            "\"age\": 30, " +
            "\"active\": true, " +
            "\"array\": [1, 2, 3, \"string\", true, {\"objectInsideArray\": 2.43}, [1, 2, 3, 4, 5]]" +
        "}";
        
        assertAreEquals(me, equal);
        assertAreNotEquals(me, diff1);
        assertAreNotEquals(me, diff2);
        assertAreNotEquals(me, diff3);
        assertAreNotEquals(me, diff4);
        assertAreNotEquals(me, diff5);
        assertAreNotEquals(me, diff6);
        assertAreNotEquals(me, diff7);
        assertAreNotEquals(me, diff8);
        assertAreNotEquals(me, diff9);
        
        let obj = AJObject(json: me);
        let dob8 = AJObject(json: diff8);
        let dob7 = AJObject(json: diff7);
        
        assert(obj.at(path: "inner.prop1")?.string == "ciao");
        assert(!obj.differs(at: "inner.prop2").from(dob8));
        assert(obj.differs(at: "inner.prop1").from(dob7));
        
    }
    
    func assertAreEquals(_ me: String, _ equal: String) {
        let o1 = AJObject(json: me);
        let o2 = AJObject(json: equal);
        let equals = o1.isEqual(o2)
        assert(equals);
    }
    
    func assertAreNotEquals(_ me: String, _ equal: String) {
        let o1 = AJObject(json: me);
        let o2 = AJObject(json: equal);
        
        assert(!o1.isEqual(o2));
    }

    
}
