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

  console.info("Running before_plugin_uninstall.Hook: " + context.hook + ", Package: " + packageName + ", Path: " + projectRoot + ".");

  if (!packageName) {
    console.error("Package name could not be found!");
    return;
  }

  if (context.opts.cordova.platforms.indexOf("android") < 0) {
    console.info("Android platform has not been added.");
    return;
  }

  var cordovaGradleBuilderPath = path.join(projectRoot, "platforms", "android", "cordova", "lib", "builders", "GradleBuilder.js");
  fs.readFile(cordovaGradleBuilderPath, {encoding: 'utf-8'}, function (err, data) {
    if (err) {
      throw err;
    }

    var regex = /GENERATED FILE - DO NOT EDIT\\ninclude ":PushSDK"\\n/g;
    var str = 'GENERATED FILE - DO NOT EDIT\\n';

    data = data.replace(regex, str);
    fs.writeFileSync(cordovaGradleBuilderPath, data, "utf-8");
  });
};