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

class HomeViewController : AJViewController {

    private var _textView: UITextView?

    override func defineStores(_ stores: AJStoreDefinitions) {
        _ = stores.add(store: Stores.HOME)
    }
    
    override func onSetupView(_ view: UIView) {
        view.backgroundColor = Colors.white
        
        _textView = UITextView(frame: CGRect.zero)
        _textView?.frame.origin.y = 20
        
        view.addSubview(_textView!)
    }
    
    override func onUpdateView(_ view: UIView, store: String, state: AJObject, lastState: AJObject) {
        if state.differs(at: "message").from(lastState) {
            self._textView?.text = state.get("message")?.string
            self._textView?.sizeToFit()
        }
    }
    
    override func onViewLoaded() {
        _ = AJ.run(action: Actions.GET_MESSAGE)
    }
    
}
