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
   
    fileprivate var _captureSession: AVCaptureSession?
    
    public init(view: UIView, owner: UIViewController) {
        _cameraView = view
        _owner = owner
        super.init()
        
        checkCameraPermissionsAndInit()
    }
    
    func checkCameraPermissionsAndInit() {
        if AVCaptureDevice.authorizationStatus(forMediaType: AVMediaTypeVideo) == AVAuthorizationStatus.notDetermined {
            AVCaptureDevice.requestAccess(forMediaType: AVMediaTypeVideo, completionHandler: { (b) in
                self.checkCameraPermissionsAndInit()
            })
        } else if AVCaptureDevice.authorizationStatus(forMediaType: AVMediaTypeVideo) == AVAuthorizationStatus.denied {
            if let owner = _owner {
                alert(title:"Authorization request",
                      message: "Camera access is required to use this application. Please give camera access to this application using Settings",
                      owner: owner
                )
            }
            return
        } else if AVCaptureDevice.authorizationStatus(forMediaType: AVMediaTypeVideo) == AVAuthorizationStatus.authorized {
            initCamera()
        }
        
    }
    
    func initCamera() {
        self._captureSession = AVCaptureSession()
        
        var captureDevice: AVCaptureDevice?
        let devices = AVCaptureDevice.devices(withMediaType: AVMediaTypeVideo)
        for d in devices! {
            if (d as AnyObject).position == AVCaptureDevicePosition.back {
                captureDevice = d as? AVCaptureDevice
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
        
        self.enableQRCodeScanner()
        
        runui {
            let layer = AVCaptureVideoPreviewLayer(session: self._captureSession!)
            layer?.frame = self._cameraView.bounds
            layer?.videoGravity = AVLayerVideoGravityResizeAspectFill
            self._cameraView.layer.addSublayer(layer!)
            
            self.open()
        }
    }
    
    open func open() {
        self._captureSession?.startRunning()
    }
    
    open func close() {
        self._captureSession?.stopRunning()
    }
    
    open func enableQRCodeScanner() {
        guard let session = _captureSession else {
            fatalError("Camera not initialized")
        }
        
        let dispatchQueue = DispatchQueue(label: "applica.camera.qrcodescanner", attributes: [])
        let captureOutput = AVCaptureMetadataOutput()
        session.addOutput(captureOutput)
        
        captureOutput.metadataObjectTypes = [AVMetadataObjectTypeQRCode]
        captureOutput.setMetadataObjectsDelegate(self, queue: dispatchQueue)
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
