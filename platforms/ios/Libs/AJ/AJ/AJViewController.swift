//
//  AJViewController.swift
//  AJ
//
//  Created by Bruno Fortunato on 14/03/2017.
//  Copyright © 2017 Bruno Fortunato. All rights reserved.
//

import Foundation
import UIKit

open class AJStoreDefinitions {
    public var stores = [String]()
    
    open func add(store: String) -> AJStoreDefinitions {
        stores.append(store)
        return self
    }
    
    open func remove(store: String) -> AJStoreDefinitions {
        if let index = stores.firstIndex(of: store) {
            stores.remove(at: index)
        }
        return self
    }
}

open class AJViewController : UIViewController {
    
    public var definitions = AJStoreDefinitions()
    
    private var _lastStates = [String : AJObject]()
    
    public init() {
        super.init(nibName: nil, bundle: nil)
        
        defineStores(definitions)
        initStores()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        deinitStores()
    }
    
    open override func loadView() {
        super.loadView()
        
        onSetupView(view)
    }
    
    open override func viewDidLoad() {
        super.viewDidLoad()
        
        onViewLoaded()
    }
    
    open func defineStores(_ stores: AJStoreDefinitions) {
        fatalError("define(stores:) has not been implemented")
    }
    
    open func initStores() {
        for store in definitions.stores {
            AJ.subscribe(to: store, owner: self) { [weak self] state in
                if let me = self {
                    let lastState = me._lastStates[store] ?? AJObject.empty()
                    me._lastStates[store] = state
                    me.onUpdateView(me.view, store: store, state: state, lastState: lastState)
                }
            }
        }
    }
    
    open func deinitStores() {
        for store in definitions.stores {
            AJ.unsubscribe(from: store, owner: self)
        }
    }
    
    open func onSetupView(_ view: UIView) {
        fatalError("onSetupView(_ view:state:lastState:) has not been implemented")
    }
    
    open func onUpdateView(_ view: UIView, store: String, state: AJObject, lastState: AJObject) {
        fatalError("onUpdateView(_ view:state:lastState:) has not been implemented")
    }
    
    open func onViewLoaded() {

    }
    
    
}

