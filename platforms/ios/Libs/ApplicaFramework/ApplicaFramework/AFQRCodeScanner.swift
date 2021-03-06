//
//  AFCamera.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 06/09/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import AVFoundation

public typealias AFQRCodeScannerCompletion = ((String) -> Void)

@objc
open class AFQRCodeScanner : NSObject, AVCaptureMetadataOutputObjectsDelegate {
    
    open var onQRCode = AFEvent()
    
    let _cameraView: UIView
    weak var _owner: UIViewController?
    var _captureOutput: AVCaptureMetadataOutput?
   
    private var _captureSession: AVCaptureSession?
    private var _enabled = false
    
    public init(view: UIView, owner: UIViewController) {
        _cameraView = view
        _owner = owner
        super.init()
       
        checkCameraPermissionsAndInit()
    }
    
    func checkCameraPermissionsAndInit() {
        if AVCaptureDevice.authorizationStatus(for: AVMediaType.video) == AVAuthorizationStatus.notDetermined {
            AVCaptureDevice.requestAccess(for: AVMediaType.video, completionHandler: { (b) in
                self.checkCameraPermissionsAndInit()
            })
        } else if AVCaptureDevice.authorizationStatus(for: AVMediaType.video) == AVAuthorizationStatus.denied {
            if let owner = _owner {
                alert(title:"Authorization request",
                      message: "Camera access is required to use this application. Please give camera access to this application using Settings",
                      owner: owner
                )
            }
            return
        } else if AVCaptureDevice.authorizationStatus(for: AVMediaType.video) == AVAuthorizationStatus.authorized {
            initCamera()
        }
        
    }
    
    func initCamera() {
        self._captureSession = AVCaptureSession()
        
        var captureDevice: AVCaptureDevice?
        let devices = AVCaptureDevice.devices(for: AVMediaType.video)
        for d in devices {
            if (d as AnyObject).position == AVCaptureDevice.Position.back {
                captureDevice = d as AVCaptureDevice
                break
            }
        }
        
        guard let device = captureDevice else {
            if let owner = _owner {
                alert(title: "Camera access", message: "Camera not available", owner: owner)
            }
            return
        }
        
        guard let deviceInput = try? AVCaptureDeviceInput(device: device) else {
            if let owner = _owner {
                alert(title: "Camera access", message: "Camera not available", owner: owner)
            }
            return
        }
        
        self._captureSession?.addInput(deviceInput)

        let dispatchQueue = DispatchQueue(label: "applica.camera.qrcodescanner", attributes: [])
        _captureOutput = AVCaptureMetadataOutput()
        self._captureSession?.addOutput(_captureOutput!)
    
        _captureOutput?.setMetadataObjectsDelegate(self, queue: dispatchQueue)
        
        self.enableQRCodeScanner()
        
        runui {
            let layer = AVCaptureVideoPreviewLayer(session: self._captureSession!)
            layer.frame = self._cameraView.bounds
            layer.videoGravity = AVLayerVideoGravity.resizeAspectFill
            self._cameraView.layer.addSublayer(layer)
            
            self.open()
        }
    }
    
    open func open() {
        self._captureSession?.startRunning()
    }
    
    open func close() {
        self._captureSession?.stopRunning()
    }
    
    open func disableQRCodeScanner() {
        self._captureOutput?.metadataObjectTypes = []

        _enabled = false
    }

    open func enableQRCodeScanner() {
        self._captureOutput?.metadataObjectTypes = [AVMetadataObject.ObjectType.qr]

        _enabled = true
    }
    
    open func captureOutput(_ captureOutput: AVCaptureOutput!, didOutputMetadataObjects metadataObjects: [Any]!, from connection: AVCaptureConnection!) {
        if metadataObjects != nil && metadataObjects.count > 0 {
            if let metadata = metadataObjects.first {
                if let result = (metadata as? AVMetadataMachineReadableCodeObject)?.stringValue {
                    AudioServicesPlayAlertSound(kSystemSoundID_Vibrate);
                    _captureSession?.stopRunning()
                    
                    onQRCode.invoke(sender: self, params: ["result": result])
                }
            }
        }
    }

}
