//
//  AJHomeScreen.swift
//  AJLibrary
//
//  Created by Bruno Fortunato on 08/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import UIKit
import AJ
import ApplicaFramework

class HomeViewController: UIViewController {

    private var _textView: UITextView?
    
    init() {
        super.init(nibName: nil, bundle: nil)
        
        AJApp.runtime().subscribe(to: Stores.HOME, owner: self) { [weak self] (state) in
            self?._textView?.text = state.get("message")?.string
            self?._textView?.sizeToFit()
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        AJApp.runtime().unsubscribe(from: Stores.HOME, owner: self)
    }
    
    override func loadView() {
        super.loadView()
        
        view.backgroundColor = Colors.white
        
        _textView = UITextView(frame: CGRect.zero)
        _textView?.frame.origin.y = 20
        
        view.addSubview(_textView!)
        
        _ = AJApp.runtime().run(action: Actions.GET_MESSAGE)
    }
}
