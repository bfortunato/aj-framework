//
//  AJReflectionUtils.h
//  ApplicaFramework
//
//  Created by Bruno Fortunato on 09/03/16.
//  Copyright © 2016 Bruno Fortunato. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AJReflectionUtils : NSObject

+ (id)invokeMethod:(NSString *)methodName ofInstance:(id)instance withArguments:(NSArray *)arguments;

@end
