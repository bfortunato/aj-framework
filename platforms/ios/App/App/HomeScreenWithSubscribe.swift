//
//  HomeScreenWithSubscribe.swift
//  App
//
//  Created by Bruno Fortunato on 20/03/2017.
//  Copyright © 2017 Bruno Fortunato. All rights reserved.
//


import Foundation
import UIKit
import AJ
import ApplicaFramework

class HomeViewControllerWithSubscribe: UIViewController {
    
    private var _textView: UITextView?
    
    init() {
        super.init(nibName: nil, bundle: nil)
        
        AJ.subscribe(to: Stores.HOME, owner: self) { [weak self] (state) in
            self?._textView?.text = state.get("message")?.string
            self?._textView?.sizeToFit()
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        AJ.unsubscribe(from: Stores.HOME, owner: self)
    }
    
    override func loadView() {
        super.loadView()
        
        view.backgroundColor = Colors.white
        
        _textView = UITextView(frame: CGRect.zero)
        _textView?.frame.origin.y = 20
        
        view.addSubview(_textView!)
        
        _ = AJ.run(action: Actions.GET_MESSAGE)
    }
}
