package xwang.cordova.umeng.push;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class XiaomiUtils {
  private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
  private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
  private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

  public static boolean isMIUI() {
    try {
      Properties properties = new Properties();
      properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
      return properties.getProperty(KEY_MIUI_VERSION_CODE, null) != null
        || properties.getProperty(KEY_MIUI_VERSION_NAME, null) != null
        || properties.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
    } catch (final IOException e) {
      return false;
    }
  }
}
