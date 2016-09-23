//
//  AFBluetooth.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 07/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import CoreBluetooth
import ApplicaFramework

open class AFBluetoothPeripheralCharacteristic {
    var id: String
    var value: String?
    
    init(id: String, value: String?) {
        self.id = id
        self.value = value
    }
}


open class AFBluetoothPeripheralService {
    var id: String
    var characteristics = [AFBluetoothPeripheralCharacteristic]()
    
    init(id: String) {
        self.id = id
    }
}

@objc
open class AFBluetoothPeripheral: NSObject, CBPeripheralManagerDelegate {
    
    open var onReady = AFEvent()
    open var onStartAdvertising = AFEvent()
    open var onWriteValue = AFEvent()
    
    open var name: String = "iOS Device"
    
    fileprivate var _peripheralManager: CBPeripheralManager!
    fileprivate var _services = [AFBluetoothPeripheralService]()
    fileprivate var _configured = false
    
    public override init() {
        super.init()
    }
    
    open func configure() {
        _peripheralManager = CBPeripheralManager(delegate: self, queue: DispatchQueue(label: "applica.bluetooth.peripheral", attributes: []))
        
        _configured = true
    }
    
    open func startAdvertising() {
        if (!_configured) {
            fatalError("Not configured")
        }
        
        _peripheralManager.startAdvertising([
            CBAdvertisementDataServiceUUIDsKey: _services.map({ CBUUID(string: $0.id)}),
            CBAdvertisementDataLocalNameKey: name
        ])
    }
    
    open func stopAdvertising() {
        _peripheralManager.stopAdvertising()
        
        NSLog("Advertising stopped")
    }
    
    open func addService(_ id: String) {
        let svc = AFBluetoothPeripheralService(id: id)
        _services.append(svc)
    }
    
    open func addCharacterisicToService(_ serviceId: String, id: String, value: String?) {
        guard let svc = _services.filter({$0.id == serviceId}).first else {
            fatalError("Service not found: \(serviceId)")
        }
        
        let c = AFBluetoothPeripheralCharacteristic(id: id, value: value)
        svc.characteristics.append(c)
    }
    
    open func setValue(_ value: String, forCharacteristic characteristicId: String, serviceId: String) {
        guard let service = _services.filter({$0.id == serviceId}).first else {
            fatalError("Service not found: \(serviceId)")
        }
        
        guard let characteristic = service.characteristics.filter({$0.id == characteristicId}).first else {
            fatalError("Characteristic not found: \(characteristicId)")
        }
        
        characteristic.value = value
    }
    
    
    //MARK: Delegate implementations
    
    open func peripheralManagerDidStartAdvertising(_ peripheral: CBPeripheralManager, error: Error?) {
        if (error != nil) {
            NSLog("Error advertising: \(error!.localizedDescription)");
        }
        
        NSLog("Advertising started")
        
        onStartAdvertising.invoke(sender: self)
    }
    
    open func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        if peripheral.state == .poweredOn {
            var services = [CBMutableService]()
            for s in _services {
                let service = CBMutableService(type: CBUUID(string: s.id), primary: true)
                
                var characteristics = [CBMutableCharacteristic]()
                for c in s.characteristics {
                    var properties = [CBCharacteristicProperties]()
                    var permissions = [CBAttributePermissions]()
                    
                    let characteristic = CBMutableCharacteristic(
                        type: CBUUID(string: c.id),
                        properties: [.read, .write],
                        value: nil,
                        permissions: [.readable, .writeable])
                    
                    characteristics.append(characteristic)
                }
                
                service.characteristics = characteristics
                
                _peripheralManager.add(service)
            }

            
            onReady.invoke(sender: self)
        }
        
        switch peripheral.state {
        case .poweredOff:
            NSLog("Bluetooth state: PoweredOff")
            break;
        case .poweredOn:
            NSLog("Bluetooth state: PoweredOn")
            break;
        case .resetting:
            NSLog("Bluetooth state: Resetting")
            break;
        case .unauthorized:
            NSLog("Bluetooth state: Unauthorized")
            break;
        case .unsupported:
            NSLog("Bluetooth state: Unsupported")
            break;
        default:
            break
        }
    }
    
    open func peripheralManager(_ peripheral: CBPeripheralManager, didAdd service: CBService, error: Error?) {
        if (error != nil) {
            NSLog("Error publishing service: \(error!.localizedDescription)");
        }
        
        NSLog("Service added: \(service.uuid.uuidString)")
    }
    
    open func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveRead request: CBATTRequest) {
        guard let service = _services.filter({$0.id == request.characteristic.service.uuid.uuidString}).first else {
            fatalError("Service not found: \(request.characteristic.service.uuid.uuidString)")
        }
        
        guard let characteristic = service.characteristics.filter({$0.id == request.characteristic.uuid.uuidString}).first else {
            fatalError("Characteristic not found: \(request.characteristic.uuid.uuidString)")
        }
        
        request.value = characteristic.value?.data(using: String.Encoding.utf8)
        
        peripheral.respond(to: request, withResult: CBATTError.Code.success)
    }   
    
    open func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveWrite requests: [CBATTRequest]) {
        for request in requests {
            if let service = _services.filter({$0.id == request.characteristic.service.uuid.uuidString}).first {
                if var characteristic = service.characteristics.filter({$0.id == request.characteristic.uuid.uuidString}).first {
                    if let v = request.value {
                        characteristic.value = String(data: v, encoding: String.Encoding.utf8)
                    } else {
                        characteristic.value = nil
                    }
                    
                    onWriteValue.invoke(sender: self, params: [
                        "serviceId": service.id as AnyObject,
                        "characteristicId": characteristic.id as AnyObject,
                        "value": characteristic.value ?? ""
                    ])
                }
            }
        }
    }
    
    
}
