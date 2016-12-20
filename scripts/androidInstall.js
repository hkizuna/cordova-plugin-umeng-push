#!/usr/bin/env node

module.exports = function (context) {
  var path        = context.requireCordovaModule('path'),
      fs          = context.requireCordovaModule('fs'),
      shell       = context.requireCordovaModule('shelljs'),
      projectRoot = context.opts.projectRoot;

  var ConfigParser = null;
  try {
    ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
  } catch(e) {
    ConfigParser = context.requireCordovaModule('cordova-lib/src/configparser/ConfigParser');
  }

  var config      = new ConfigParser(path.join(context.opts.projectRoot, "config.xml")),
      packageName = config.android_packageName() || config.packageName();

  console.info("Running afterPluginAdd.Hook: " + context.hook + ", Package: " + packageName + ", Path: " + projectRoot + ".");

  if (!packageName) {
    console.error("Package name could not be found!");
    return;
  }

  if (context.opts.cordova.platforms.indexOf("android") < 0) {
    console.info("Android platform has not been added.");
    return;
  }

  var pushSDKAndroidManifestPath = path.join(projectRoot, "platforms", "android", "PushSDK/AndroidManifest.xml");
  var pluginPushSDKAndroidManifestPath = path.join(context.opts.plugin.dir, "src/android/PushSDK/AndroidManifest.xml");
  var targets = [pushSDKAndroidManifestPath, pluginPushSDKAndroidManifestPath];

  targets.forEach(function (f) {
    fs.readFile(f, {encoding: 'utf-8'}, function (err, data) {
      if (err) {
          throw err;
      }

      data = data.replace(/__PACKAGE_NAME__/g, packageName);
      fs.writeFileSync(f, data, "utf-8");
    });
  });

  var cordovaGradleBuilderPath = path.join(projectRoot, "platforms", "android", "cordova", "lib", "builders", "GradleBuilder.js");
  fs.readFile(cordovaGradleBuilderPath, {encoding: 'utf-8'}, function (err, data) {
    if (err) {
      throw err;
    }

    var regex = /GENERATED FILE - DO NOT EDIT\\n/g;
    var str = 'GENERATED FILE - DO NOT EDIT\\ninclude ":PushSDK"\\n'

    data = data.replace(regex, str);
    fs.writeFileSync(cordovaGradleBuilderPath, data, "utf-8");
  });

  var packagePath = packageName.replace(/\./g, "/");
  var mainActivityPath = path.join(projectRoot, "platforms/android/src", packagePath, "MainActivity.java");
  fs.readFile(mainActivityPath, {encoding: 'utf-8'}, function (err, data) {
    if (err) {
      throw err;
    }

    var importPattern = /import org\.apache\.cordova\.\*;\n\n/g;
    var importStr = 'import org.apache.cordova.*;\nimport xwang.cordova.umeng.push.UmengPush;\nimport com.xiaomi.mipush.sdk.MiPushMessage;\nimport com.xiaomi.mipush.sdk.PushMessageHelper;\n\n';
    data = data.replace(importPattern, importStr);

    var pattern = /loadUrl\(launchUrl\);\n/;
    var str = 'loadUrl(launchUrl);UmengPush.setPendingNotification((MiPushMessage) getIntent().getSerializableExtra(PushMessageHelper.KEY_MESSAGE));\n';
    data = data.replace(pattern, str);

    fs.writeFileSync(mainActivityPath, data, "utf-8");
  });
};
