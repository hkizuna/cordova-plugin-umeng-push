//
//  AppDelegate+UmengPush.h
//
//  Created by xwang on 12/23/15.
//
//

#import "AppDelegate.h"
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
#import <UserNotifications/UserNotifications.h>
#endif

@interface AppDelegate (UmengPush) <UNUserNotificationCenterDelegate>
@end