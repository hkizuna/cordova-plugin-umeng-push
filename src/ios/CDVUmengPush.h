//
//  CDVUmengPush.h
//
//  Created by xwang on 12/23/15.
//
//

#import <Cordova/CDV.h>

typedef NS_ENUM(NSInteger, kAliasType)
{
	kAliasTypeSina = 0,
	kAliasTypeTencent = 1,
	kAliasTypeQQ = 2,
	kAliasTypeWeiXin = 3,
	kAliasTypeBaidu = 4,
	kAliasTypeRenRen = 5,
	kAliasTypeKaixin = 6,
	kAliasTypeDouban = 7,
	kAliasTypeFacebook = 8,
	kAliasTypeTwitter = 9,
};

@interface CDVUmengPush:CDVPlugin

@property (nonatomic, strong) NSString *umengPushAppId;

- (void)addTag:(CDVInvokedUrlCommand *)command;
- (void)removeTag:(CDVInvokedUrlCommand *)command;
- (void)getTags:(CDVInvokedUrlCommand *)command;
- (void)removeAllTags:(CDVInvokedUrlCommand *)command;
- (void)addAlias:(CDVInvokedUrlCommand *)command;
- (void)setAlias:(CDVInvokedUrlCommand *)command;
- (void)removeAlias:(CDVInvokedUrlCommand *)command;

@end