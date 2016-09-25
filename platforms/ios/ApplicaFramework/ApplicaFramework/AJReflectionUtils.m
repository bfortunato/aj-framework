//
//  AJReflectionUtils.m
//  AJ
//
//  Created by Bruno Fortunato on 17/02/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

#import "AJReflectionUtils.h"

@implementation AJReflectionUtils

+ (id)invokeMethod:(NSString *)methodName ofInstance:(id)instance withArguments:(NSArray *)arguments {
    id value = nil;
    SEL selector = NSSelectorFromString(methodName);
    NSMethodSignature *signature = [instance methodSignatureForSelector:selector];
    if (signature) {
        NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
        if (invocation) {
            [invocation setTarget:instance];
            [invocation setSelector:selector];
            int i = 0;
            for (id item in arguments) {
                [invocation setArgument:(__bridge void * _Nonnull)(item) atIndex:i++];
            }
            [invocation invoke];
            [invocation getReturnValue:&value];
        }
    };
    
    return value;
}

@end
