//
//  AFUICommons.swift
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 09/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

import Foundation
import UIKit

//
//  RectBuilder.swift
//  framework-ios
//
//  Created by Giuseppe Iacobucci on 18/12/15.
//  Copyright © 2015 Applica. All rights reserved.
//

import UIKit

//MARK: dimension utils
public func X2(_ v:CGFloat) -> CGFloat{ return v / 2.0 }
public func X3(_ v:CGFloat) -> CGFloat { return v / 3.0 }


//MARK: color utils
public func RGB(_ r:CGFloat, _ g:CGFloat, _ b:CGFloat) -> UIColor {
    return UIColor(red: r / 255.0, green: g / 255.0, blue: b / 255.0, alpha: 1)
}

public func RGBA(_ r:CGFloat, _ g:CGFloat, _ b:CGFloat, _ a:CGFloat) -> UIColor {
    return UIColor(red: r / 255.0, green: g / 255.0, blue: b / 255.0, alpha: a / 255.0)
}

//MARK: interpolation utils
public func inverseLerp(_ value:CGFloat, minValue:CGFloat, maxValue:CGFloat) -> CGFloat {
    let nMax = maxValue - minValue
    let v = min(max((value - minValue) / nMax, 0), 1)
    return v
}

public func ninverseLerp(_ value:CGFloat, minValue:CGFloat, maxValue:CGFloat) -> CGFloat {
    return CGFloat(1) - inverseLerp(value, minValue: minValue, maxValue: maxValue)
}

public func lerp(_ time: CGFloat, minValue: CGFloat, maxValue: CGFloat) -> CGFloat {
    let time = max(0, min(time, 1))
    return ((maxValue - minValue) * time) + minValue
}

//MARK: size utils
func boundsOfViews(_ views: [UIView]) -> CGRect {
    var x = CGFloat.greatestFiniteMagnitude
    var y = CGFloat.greatestFiniteMagnitude
    var right = CGFloat(0)
    var bottom = CGFloat(0)
    
    for v in views {
        x = min(x, v.frame.origin.x)
        y = min(y, v.frame.origin.y)
        right = max(right, v.frame.origin.x + v.frame.size.width)
        bottom = max(bottom, v.frame.origin.y + v.frame.size.height)
    }
    
    return CGRect(x: x, y: y, width: right - x, height: bottom - y)
}

open class RB {
    
    var parent:CGRect
    var x:CGFloat = 0
    var y:CGFloat = 0
    var width:CGFloat = 0
    var height:CGFloat = 0
    
    public init(parent:CGRect) {
        self.parent = parent
    }
    
    open class func withParent(_ parent:CGRect) -> RB {
        return RB(parent: parent)
    }
    
    open class func withParentView(_ view:UIView) -> RB {
        return RB(parent: view.frame)
    }
    
    open class func fitParent(_ parent:CGRect) -> RB {
        return RB.withParent(parent).fitParent()
    }
    
    open class func withScreen() -> RB {
        let parent = UIScreen.main.bounds
        return RB(parent: parent)
    }
    
    open class func fitScreen() -> RB {
        return RB.withScreen().fitParent()
    }
    
    open func fitParent() -> RB {
        self.x = self.parent.origin.x
        self.y = self.parent.origin.y
        self.width = self.parent.size.width
        self.height = self.parent.size.height
        return self
    }
    
    open func left(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.x = value * self.parent.size.width / 100
        } else {
            self.x = value
        }
        return self
    }
    
    open func top(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.y = value * self.parent.size.height / 100.0
        } else {
            self.y = value
        }
        return self
    }
    
    open func right(_ value:CGFloat, percentual:Bool = false) -> RB {
        //  assert(self.width > 0, "please set RB.width")
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            let cv = value * self.parent.size.width / 100.0
            self.x = self.parent.size.width - cv - self.width
        } else {
            self.x = self.parent.size.width - value - self.width
        }
        return self
    }
    
    open func bottom(_ value:CGFloat, percentual:Bool = false, ignoreHeight:Bool = false) -> RB {
        //assert(self.height > 0, "please set RB.height")
        let calculatedHeight = ignoreHeight ? CGFloat(0) : self.height
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            let cv = value * self.parent.size.height / 100.0
            self.y = self.parent.size.height - cv - calculatedHeight
        } else {
            self.y = self.parent.size.height - value - calculatedHeight
        }
        
        return self
    }
    
    open func center(_ offset:CGFloat = 0, percentual:Bool = false) -> RB {
        assert(self.width > 0, "please set RB.width")
        var co = offset
        if (percentual) {
            assert(offset >= 0 && offset <= 100, "Bad percentual value")
            co = offset * self.parent.size.width / 100.0
        }
        self.x = self.parent.size.width / 2.0 - self.width / 2.0 + co
        return self
    }
    
    open func width(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.width = value * self.parent.size.width / 100.0
        } else {
            self.width = value
        }
        return self
    }
    
    open func decreaseWidth(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.width -= value * self.parent.size.width / 100.0
        } else {
            self.width -= value
        }
        return self
    }

    open func increaseWidth(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.width += value * self.parent.size.width / 100.0
        } else {
            self.width += value
        }
        return self
    }
    
    open func height(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.height = value * self.parent.size.height / 100.0
        } else {
            self.height = value
        }
        return self
    }
    
    open func decreaseHeight(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.height -= value * self.parent.size.height / 100.0
        } else {
            self.height -= value
        }
        return self
    }

    open func increaseHeight(_ value:CGFloat, percentual:Bool = false) -> RB {
        if (percentual) {
            assert(value >= 0 && value <= 100, "Bad percentual value")
            self.height += value * self.parent.size.height / 100.0
        } else {
            self.height += value
        }
        return self
    }
    
    open func size(_ value: CGSize) -> RB {
        self.height = value.height
        self.width = value.width
        return self
    }
    
    open func vcenter(_ offset:CGFloat = 0, percentual:Bool = false) -> RB {
        assert(self.height > 0, "please set RB.height")
        var co = offset
        if (percentual) {
            assert(offset >= 0 && offset <= 100, "Bad percentual value")
            co = offset * self.parent.size.height / 100.0
        }
        self.y = self.parent.size.height / 2.0 - self.height / 2.0 + co
        return self
    }
    
    open func quad(_ value:CGFloat, percentual:Bool = false) -> RB {
        _ = self.width(value, percentual: percentual)
        _ = self.height(value, percentual: percentual)
        return self
    }
    
    open func sameAs(_ rect:CGRect) -> RB {
        self.x = rect.origin.x
        self.y = rect.origin.y
        self.width = rect.size.width
        self.height = rect.size.height
        return self
    }
    
    open func translate(x:CGFloat = 0, y:CGFloat = 0, percentual:Bool = false) -> RB {
        if (percentual) {
            self.x += x * self.parent.size.width / 100.0
            self.y += y * self.parent.size.height / 100.0
        } else {
            self.x += x
            self.y += y
        }
        return self
    }
    
    open func scale(x:CGFloat = 0, y:CGFloat = 0) -> RB {
        self.height *= y
        self.width *= x
    
        return self
    }
    
    open func leftOf(_ relativeToRect:CGRect, offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            co = offset * self.parent.size.width / 100.0
        }
        self.right(co + (self.parent.size.width - relativeToRect.origin.x), percentual: false)
        return self
    }
    
    open func rightOf(_ relativeToRect:CGRect, offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            co = offset * self.parent.size.width / 100.0
        }
        self.left(co + relativeToRect.origin.x + relativeToRect.size.width, percentual: false)
        return self
    }
    
    open func topOf(_ relativeToRect:CGRect, offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            co = offset * self.parent.size.height / 100.0
        }
        self.bottom(co + (self.parent.size.height - relativeToRect.origin.y), percentual: false)
        return self
    }
    
    open func bottomOf(_ relativeToRect:CGRect, offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            co = offset * self.parent.size.height / 100.0
        }
        self.top(co + relativeToRect.origin.y + relativeToRect.size.height, percentual: false)
        return self
    }
    
    open func marginLeft(_ value:CGFloat, percentual:Bool = false) -> RB {
        decreaseWidth(value, percentual: percentual)
        left(value, percentual: percentual)
        return self
    }
    
    open func marginRight(_ value:CGFloat, percentual:Bool = false) -> RB {
        decreaseWidth(value, percentual: percentual)
        return self
    }
    
    open func marginTop(_ value:CGFloat, percentual:Bool = false) -> RB {
        decreaseHeight(value, percentual: percentual)
        top(value, percentual: percentual)
        return self
    }
    
    open func marginBottom(_ value:CGFloat, percentual:Bool = false) -> RB {
        decreaseHeight(value, percentual: percentual)
        return self
    }
    
    open func padding(_ value:CGFloat, percentual:Bool = false) -> RB {
        paddingLeft(value, percentual: percentual)
        paddingTop(value, percentual: percentual)
        paddingRight(value, percentual: percentual)
        paddingBottom(value, percentual: percentual)
        return self
    }

    open func paddingLeft(_ value:CGFloat, percentual:Bool = false) -> RB {
        increaseWidth(value, percentual: percentual)
        translate(x: value, percentual: percentual)
        return self
    }

    open func paddingRight(_ value:CGFloat, percentual:Bool = false) -> RB {
        increaseWidth(value, percentual: percentual)
        return self
    }

    open func paddingTop(_ value:CGFloat, percentual:Bool = false) -> RB {
        increaseHeight(value, percentual: percentual)
        translate(y: value, percentual: percentual)
        return self
    }

    open func paddingBottom(_ value:CGFloat, percentual:Bool = false) -> RB {
        decreaseHeight(value, percentual: percentual)
        return self
    }

    open func margin(_ value:CGFloat, percentual:Bool = false) -> RB {
        marginLeft(value, percentual: percentual)
        marginTop(value, percentual: percentual)
        marginRight(value, percentual: percentual)
        marginBottom(value, percentual: percentual)
        return self
    }
    
    open func fillWidth(_ offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            assert(offset >= 0 && offset <= 100, "Bad percentual value")
            co = offset * self.parent.size.width / 100.0
        }
        
        let toFill = parent.width - x - co
        width(toFill)
        return self
    }
    
    open func fillHeight(_ offset:CGFloat = 0, percentual:Bool = false) -> RB {
        var co = offset
        if (percentual) {
            assert(offset >= 0 && offset <= 100, "Bad percentual value")
            co = offset * self.parent.size.height / 100.0
        }
        
        let toFill = parent.height - y - co
        height(toFill)
        return self
    }
    
    open func halfWidth() -> RB {
        width /= 2
        return self
    }
    
    open func halfHeight() -> RB {
        height /= 2
        return self
    }
    
    open func make() -> CGRect {
        return CGRect(x: self.x, y: self.y, width: self.width, height: self.height)
    }
    
}

public func alert(title: String, message: String, owner: UIViewController, onCancel: (() -> Void)? = nil) {
    let alert = UIAlertController(
        title: title,
        message: message,
        preferredStyle: .alert
    )
    
    alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { (action) in
        if let fn = onCancel {
            fn()
        }
    }))
    
    owner.present(alert, animated: true, completion: nil)
}

public func toast(_ text: String, in view: UIView? = nil) {
    guard let container = view ?? UIApplication.shared.keyWindow else {
        return
    }
    
    let hud = MBProgressHUD.showAdded(to: container, animated: true)
    hud.mode = .text
    hud.label.text = text
    hud.removeFromSuperViewOnHide = true
    
    hud.hide(animated: true, afterDelay: 3)
}
