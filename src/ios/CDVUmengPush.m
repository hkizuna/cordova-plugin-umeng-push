//
//  CDVUmengPush.m
//
//  Created by xwang on 12/23/15.
//
//

#import "CDVUmengPush.h"
#import "UMessage.h"
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
#import <UserNotifications/UserNotifications.h>
#endif

@implementation CDVUmengPush

#pragma mark Initialization
- (void)pluginInitialize
{
	NSString* appId = [[self.commandDelegate settings] objectForKey:@"umengpushappid"];
	if (appId)
	{
		self.umengPushAppId = appId;
	}
	[[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(applicationDidFinishLaunching:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (void)applicationDidFinishLaunching:(NSNotification *)notification
{
	NSDictionary *launchOptions = [notification userInfo];
	if (self.umengPushAppId)
	{
    [UMessage startWithAppkey:self.umengPushAppId launchOptions:launchOptions];
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    center.delegate = self.appDelegate;
    UNAuthorizationOptions types10 = UNAuthorizationOptionBadge | UNAuthorizationOptionAlert | UNAuthorizationOptionSound;
    [center requestAuthorizationWithOptions:types10 completionHandler:^(BOOL granted, NSError * _Nullable error) {}];
#endif
    [UMessage registerForRemoteNotifications];
    [UMessage setLogEnabled:YES];
	}
}

#pragma mark API
- (void)addTag:(CDVInvokedUrlCommand *)command
{
	NSArray *arguments = [command arguments];
	if ([arguments count] != 1)
	{
		[self failWithCallbackId:command.callbackId withMessage:@"参数错误"];
		return;
	}

	[UMessage addTag:[arguments objectAtIndex:0]
			response:^(id responseObject, NSInteger remain, NSError *error) {
				if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"添加成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

- (void)removeTag:(CDVInvokedUrlCommand *)command
{
	NSArray *arguments = [command arguments];
	if ([arguments count] != 1)
	{
		[self failWithCallbackId:command.callbackId withMessage:@"参数错误"];
		return;
	}

	[UMessage removeTag:[arguments objectAtIndex:0]
			response:^(id responseObject, NSInteger remain, NSError *error) {
				if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"移除成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

- (void)getTags:(CDVInvokedUrlCommand *)command
{
	[UMessage getTags:^(NSSet *responseTags, NSInteger remain, NSError *error) {
		if (responseTags)
		{
			CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
															   messageAsArray:[responseTags allObjects]];
			[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
		}
		else
		{
			[self failWithCallbackId:command.callbackId withError:error];
		}
	  }];
}

- (void)removeAllTags:(CDVInvokedUrlCommand *)command
{
	[UMessage removeAllTags:^(id responseObject, NSInteger remain, NSError *error) {
				if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"重置成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

- (void)addAlias:(CDVInvokedUrlCommand *)command
{
	NSArray *arguments = [command arguments];
	if ([arguments count] != 2)
	{
		[self failWithCallbackId:command.callbackId withMessage:@"参数错误"];
		return;
	}

	[UMessage addAlias:[arguments objectAtIndex:0]
				  type:[[arguments objectAtIndex:1] isKindOfClass:[NSString class]]?[arguments objectAtIndex:1]:[self mapUMessageAliasType:[[arguments objectAtIndex:1] integerValue]]
			  response:^(id responseObject, NSError *error) {
			  	if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"添加成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

- (void)setAlias:(CDVInvokedUrlCommand *)command
{
	NSArray *arguments = [command arguments];
	if ([arguments count] != 2)
	{
		[self failWithCallbackId:command.callbackId withMessage:@"参数错误"];
		return;
	}

	[UMessage setAlias:[arguments objectAtIndex:0]
                  type:[[arguments objectAtIndex:1] isKindOfClass:[NSString class]]?[arguments objectAtIndex:1]:[self mapUMessageAliasType:[[arguments objectAtIndex:1] integerValue]]
			  response:^(id responseObject, NSError *error) {
			  	if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"重新添加成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

- (void)removeAlias:(CDVInvokedUrlCommand *)command
{
	NSArray *arguments = [command arguments];
	if ([arguments count] != 2)
	{
		[self failWithCallbackId:command.callbackId withMessage:@"参数错误"];
		return;
	}

	[UMessage removeAlias:[arguments objectAtIndex:0]
				  type:[[arguments objectAtIndex:1] isKindOfClass:[NSString class]]?[arguments objectAtIndex:1]:[self mapUMessageAliasType:[[arguments objectAtIndex:1] integerValue]]
			  response:^(id responseObject, NSError *error) {
			  	if (responseObject)
				{
					[self successWithCallbackId:command.callbackId withMessage:@"删除成功"];
				}
				else
				{
					[self failWithCallbackId:command.callbackId withError:error];
				}
			}];
}

#pragma mark Helper Function
- (void)successWithCallbackId:(NSString *)callbackId withMessage:(NSString *)message
{
	CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
													  messageAsString:message];
	[self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)failWithCallbackId:(NSString *)callbackId withMessage:(NSString *)message
{
	CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
													  messageAsString:message];
	[self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)failWithCallbackId:(NSString *)callbackId withError:(NSError *)error
{
	[self failWithCallbackId:callbackId withMessage:[error localizedDescription]];
}

- (NSString *)mapUMessageAliasType:(NSInteger)intType
{
	NSString* alias;
	switch(intType)
	{
		case kAliasTypeSina:
			alias = kUMessageAliasTypeSina;
			break;
		case kAliasTypeTencent:
			alias = kUMessageAliasTypeTencent;
			break;
		case kAliasTypeQQ:
			alias = kUMessageAliasTypeQQ;
			break;
		case kAliasTypeWeiXin:
			alias = kUMessageAliasTypeWeiXin;
			break;
		case kAliasTypeBaidu:
			alias = kUMessageAliasTypeBaidu;
			break;
		case kAliasTypeRenRen:
			alias = kUMessageAliasTypeRenRen;
			break;
		case kAliasTypeKaixin:
			alias = kUMessageAliasTypeKaixin;
			break;
		case kAliasTypeDouban:
			alias = kUMessageAliasTypeDouban;
			break;
		case kAliasTypeFacebook:
			alias = kUMessageAliasTypeFacebook;
			break;
		case kAliasTypeTwitter:
			alias = kUMessageAliasTypeTwitter;
			break;
	}
	return alias;
}
@end