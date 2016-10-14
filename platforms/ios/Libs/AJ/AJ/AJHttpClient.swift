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
    func request(
        _ url: String,
        _ method: String,
        _ data: JSValue,
        _ headers: JSValue,
        _ accept: JSValue,
        _ contentType: JSValue,
        _ rawResponse: Bool,
        _ cb: JSValue
    )
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
    
    open func request(
        _ url: String,
        _ method: String,
        _ data: JSValue,
        _ headers: JSValue,
        _ accept: JSValue,
        _ contentType: JSValue,
        _ rawResponse: Bool,
        _ cb: JSValue) {
        
        handleQueue.async {
            var finalUrl = url
            
            NSLog("HTTP url: \(finalUrl), data: \(data)")
            
            if !data.isNull {
                if Method.POST != method && Method.PUT != method {
                    let separator = finalUrl.range(of: "?") != nil ? "&" : "?"
                    finalUrl = "\(url)\(separator)\(data)"
                }
            }
            
            var request = URLRequest(url: URL(string: finalUrl)!)
            request.timeoutInterval = 5
            request.httpMethod = method
            
            if !contentType.isNull {
                request.addValue(contentType.toString(), forHTTPHeaderField: "Content-Type")
            }
            
            if !accept.isNull {
                request.addValue(accept.toString(), forHTTPHeaderField: "Accept")
            }
            
            if Method.POST == method || Method.PUT == method {
                if !data.isNull {
                    request.httpBody = data.toString().data(using: String.Encoding.utf8, allowLossyConversion: true)
                }
            }
            
            if !headers.isNull {
                if let dict = headers.toDictionary() {
                    for (key, value) in dict {
                        request.addValue("\(value)", forHTTPHeaderField: "\(key)")
                    }
                }
            }
            
            UIApplication.shared.isNetworkActivityIndicatorVisible = true
            
            let task = URLSession.shared.dataTask(with: request, completionHandler: { (data, response, error) -> Void in
                runui { UIApplication.shared.isNetworkActivityIndicatorVisible = false }
                
                if data != nil && error == nil {
                    if !rawResponse {
                        let response = String(data: data!, encoding: String.Encoding.utf8)!
                        cb.call(withArguments: [false, response])
                    } else {
                        cb.call(withArguments: [false, AJBuffer.create(with: data!)])
                    }
                } else {
                    NSLog("Error loading resource from \(url): \(error)")
                    cb.call(withArguments: [true])
                }
            })
            
            task.resume()
        }
    }
}
