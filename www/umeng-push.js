var exec = require('cordova/exec');

module.exports = {
	AliasType: {
		//新浪微博
		SINA: 0,
		//腾讯微博
		TENCENT: 1,
		//QQ
		QQ: 2,
		//微信
		WEIXIN: 3,
		//百度
		BAIDU: 4,
		//人人网
		RENREN: 5,
		//开心网
		KAIXIN: 6,
		//豆瓣
		DOUBAN: 7,
		//facebook
		FACEBOOK: 8,
		//twitter
		TWITTER: 9
	},

	addTag: function (tags, successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "addTag", [tags]);
	},

	removeTag: function (tags, successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "removeTag", [tags]);
	},

	getTags: function (successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "getTags", []);
	},

	removeAllTags: function (successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "removeAllTags", []);
	},

	addAlias: function (alias, type, successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "addAlias", [alias, type]);
	},

	setAlias: function (alias, type, successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "setAlias", [alias, type]);
	},

	removeAlias: function (alias, type, successCallback, errorCallback) {
		exec(successCallback, errorCallback, "UmengPush", "removeAlias", [alias, type]);
	}

	// setLocation:

	// setBadgeClear:

	// setAutoAlert:

	// setChannel:
};