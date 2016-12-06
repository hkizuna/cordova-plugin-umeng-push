var exec = require('cordova/exec'),
    cordova = require('cordova'),
    channel = require('cordova/channel');

function UmengPush() {

}

UmengPush.prototype.addTag = function(tags, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'addTag', [tags]);
};

UmengPush.prototype.removeTag = function(tags, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'removeTag', [tags]);
};

UmengPush.prototype.getTags = function(successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'getTags', []);
};

UmengPush.prototype.removeAllTags = function(successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'removeAllTags', []);
};

UmengPush.prototype.addAlias = function(alias, type, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'addAlias', [alias, type]);
};

UmengPush.prototype.setAlias = function(alias, type, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'setAlias', [alias, type]);
};

UmengPush.prototype.removeAlias = function(alias, type, successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'removeAlias', [alias, type]);
};

UmengPush.prototype.getRemoteNotification = function(successCallback, errorCallback) {
  exec(successCallback, errorCallback, 'UmengPush', 'getRemoteNotification', []);
};

UmengPush.prototype.init = function() {
  this.getRemoteNotification(function(notification) {
    cordova.fireDocumentEvent('up:notify', notification);
  },
  function (e) {
    console.log('Error initializing UmengPush: ' + e);
  });
};

module.exports = new UmengPush();
