//
//  AJHttpClient.swift
//  AJ
//
//  Created by Bruno Fortunato on 20/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit
import JavaScriptCore
import ApplicaFramework

@objc
public protocol AJHttpClientProtocol: JSExport {
    func request(_ url: String, _ method: String, _ data: JSValue, _ headers: JSValue, _ raw: Bool, _ cb: JSValue)
}

@objc
open class AJHttpClient: NSObject, AJHttpClientProtocol {
    
    let runtime: AJRuntime
    let handleQueue = DispatchQueue(label: "AJ.AJHttpClient.handleQueue", attributes: DispatchQueue.Attributes.concurrent)
    
    init(runtime: AJRuntime) {
        self.runtime = runtime
    }
    
    public struct Method {
        static let GET = "GET"
        static let POST = "POST"
        static let PUT = "PUT"
        static let DELETE = "DELETE"
    }
    
    open func request(_ url: String, _ method: String, _ data: JSValue, _ headers: JSValue, _ raw: Bool, _ cb: JSValue) {
        handleQueue.async {
            var finalUrl = url
            var requestBody: String? = nil
            for (key, value) in data.toDictionary() {
                let separator = requestBody == nil ? "" : "&"
                if requestBody == nil {
                    requestBody = ""
                }
                requestBody? += "\(separator)\(key)=\(value)"
            }
            
            NSLog("HTTP url: \(finalUrl), data: \(requestBody)")
            
            if Method.GET == method && requestBody != nil {
                let separator = finalUrl.range(of: "?") != nil ? "&" : "?"
                finalUrl = "\(url)\(separator)\(requestBody!)"
            }
            
            var request = URLRequest(url: URL(string: finalUrl)!)
            request.httpMethod = method
            request.httpBody = requestBody?.data(using: String.Encoding.utf8, allowLossyConversion: true)
            
            UIApplication.shared.isNetworkActivityIndicatorVisible = true
            
            let task = URLSession.shared.dataTask(with: request, completionHandler: { (data, response, error) -> Void in
                runui { UIApplication.shared.isNetworkActivityIndicatorVisible = false }
                
                if data != nil && error == nil {
                    if !raw {
                        let response = String(data: data!, encoding: String.Encoding.utf8)!
                        cb.call(withArguments: [false, response])
                    } else {
                        cb.call(withArguments: [false, AJBuffer.create(with: data!)])
                    }
                } else {
                    cb.call(withArguments: [true])
                }
            })
            
            task.resume()
        }
    }
}
