//
//  AJBluetoothPlugin.swift
//  AJLibrary
//
//  Created by Bruno Fortunato on 07/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import AJ
import ApplicaFramework

public struct AJBluetoothPeripheralActions {
    public static let changeCharacteristicValue = "changeCharacteristicValue"
    public static let setReady = "setReady"
}

open class AJBluetoothPeripheralPlugin: AJPlugin {
    
    var peripheral: AFBluetoothPeripheral?
    
    public init() {
        super.init(name: "BluetoothPeripheral")
    }
    
    open func configure(_ data: AJObject) -> AJObject {
        guard let name = data.get("name")?.string else { fatalError("Name is required") }
        guard let services = data.get("services")?.array else { fatalError("Services are required") }
        
        peripheral = AFBluetoothPeripheral()
        
        for i in 0..<services.count {
            guard let serviceId = services.objectAt(i)?.get("id")?.string else { fatalError("service.id is required") }
            guard let characteristics = services.objectAt(i)?.get("characteristics")?.array else { fatalError("service.id is required") }
            
            peripheral?.addService(serviceId)
            
            for ci in 0..<characteristics.count {
                guard let characteristicId = characteristics.objectAt(ci)?.get("id")?.string else { fatalError("service[\(i)].characteristics[\(ci)].id is required") }
                let value = characteristics.objectAt(ci)?.get("value")?.string
                
                peripheral?.addCharacterisicToService(serviceId, id: characteristicId, value: value)
            }
        }
        
        peripheral?.onWriteValue += { s, p -> Void in
            let serviceId = p!["serviceId"] as! String
            let characteristicId = p!["characteristicId"] as! String
            let value = p!["value"] as? String
            
            AJApp.runtime().run(action: AJBluetoothPeripheralActions.changeCharacteristicValue,
                                           data: AJObject.create()
                                            .set("serviceId", serviceId)
                                            .set("characteristicId", characteristicId)
                                            .set("value", value ?? "")
            )
        }
    
        peripheral?.onReady += { s, p -> Void in
            AJApp.runtime().run(action: AJBluetoothPeripheralActions.setReady)
        }
        
        self.peripheral?.configure()
        
        return AJObject.empty()
        
    }
    
    open func startAdvertising(_ data: AJObject) -> AJObject {
        peripheral?.startAdvertising()
        
        return AJObject.empty()
    }
    
    open func stopAdvertising(_ data: AJObject) -> AJObject {
        peripheral?.stopAdvertising()
        
        return AJObject.empty()
    }
    
    open func setValue(_ data: AJObject) -> AJObject {
        guard let serviceId = data.get("serviceId")?.string else { fatalError("serviceId is required") }
        guard let characteristicId = data.get("characteristicId")?.string else { fatalError("characteristicId is required") }
        guard let value = data.get("value")?.string else { fatalError("value is required") }

        peripheral?.setValue(value, forCharacteristic: characteristicId, serviceId: serviceId)
        
        return AJObject.empty()
    }
    
}
