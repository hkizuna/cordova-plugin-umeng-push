//
//  AppDelegate+UmengPush.m
//
//  Created by xwang on 12/23/15.
//
//

#import "AppDelegate+UmengPush.h"
#import "UMessage.h"
#import <objc/runtime.h>

@implementation AppDelegate (UmengPush)

+ (void)load
{
	Method originMethod;
    Method swizzledMethod;
    
    originMethod = class_getInstanceMethod([self class],@selector(application:didRegisterForRemoteNotificationsWithDeviceToken:));
    swizzledMethod = class_getInstanceMethod([self class],@selector(swizzled_application:didRegisterForRemoteNotificationsWithDeviceToken:));
    method_exchangeImplementations(originMethod, swizzledMethod);
}

- (void)swizzled_application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
	[UMessage registerDeviceToken:deviceToken];
	[self swizzled_application:application didRegisterForRemoteNotificationsWithDeviceToken: deviceToken];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
	[UMessage didReceiveRemoteNotification:userInfo];
}

@end