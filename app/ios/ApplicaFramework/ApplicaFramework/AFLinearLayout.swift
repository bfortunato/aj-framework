//
//  AJLinearLayout.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 09/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import UIKit

open class AFLinearLayout: UIScrollView {
    
    let animationTime: TimeInterval = 0.1
    open var computedHeight: CGFloat = 0
    open var onLayout = AFEvent()
    open var animate = false
    
    open var orientation: Orientation {
        didSet {
            setNeedsLayout()
        }
    }
    
    open var padding: CGFloat = 0 {
        didSet {
            setNeedsLayout()
        }
    }
    
    public enum Orientation {
        case vertical
        case horizontal
    }
    
    public init(frame: CGRect, orientation: Orientation = .vertical) {
        self.orientation = orientation
        super.init(frame: frame)
        showsHorizontalScrollIndicator = false
        showsVerticalScrollIndicator = false
    }

    public required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        
        var x: CGFloat = 0//self.contentInset.left
        var y: CGFloat = 0//self.contentInset.top
        
        for view in subviews {
            x = (orientation == .horizontal ? x : view.frame.origin.x)
            y = (orientation == .vertical ? y : view.frame.origin.y)
            
            if animate {
                UIView.animate(withDuration: animationTime, animations: { () -> Void in
                    view.frame = CGRect(x: x, y: y, width: view.frame.width, height: view.frame.height)
                })
            } else {
                view.frame = CGRect(x: x, y: y, width: view.frame.width, height: view.frame.height)
            }
            
            x += (orientation == .horizontal ? view.frame.width + padding : 0)
            y += (orientation == .vertical ? view.frame.height + padding : 0)
        }
        
        let width = orientation == .horizontal ? x : bounds.width - padding// + self.contentInset.bottom
        let height = orientation == .vertical ? y : bounds.height - padding// + self.contentInset.right
        
        contentSize = CGSize(width: width, height: height)
        
        onLayout.invoke(sender: self)
    }
    
}


