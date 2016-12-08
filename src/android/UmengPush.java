package xwang.cordova.umeng.push;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.tag.TagManager;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class UmengPush extends CordovaPlugin {
  public static final int ALIAS_TYPE_SINA = 0;
  public static final int ALIAS_TYPE_TENCENT = 1;
  public static final int ALIAS_TYPE_QQ = 2;
  public static final int ALIAS_TYPE_WEIXIN = 3;
  public static final int ALIAS_TYPE_BAIDU = 4;
  public static final int ALIAS_TYPE_RENREN = 5;
  public static final int ALIAS_TYPE_KAIXIN = 6;
  public static final int ALIAS_TYPE_DOUBAN = 7;
  public static final int ALIAS_TYPE_FACEBOOK = 8;
  public static final int ALIAS_TYPE_TWITTER = 9;

  public static final String ERROR_INVALID_PARAMETERS = "参数错误";
  public static final String ERROR_INVALID_MIUI = "MIUIじゃない";
  public static final String TAG = "xwang.cordova.umeng.push";

  protected String xiaomiAppID;
  protected String xiaomiAppKey;
  protected boolean isMiPush;
  protected PushAgent mPushAgent;
  protected Context mContext;

  private CallbackContext mCallbackContext;
  private JSONObject pendingNotification;

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    mContext = cordova.getActivity().getApplicationContext();
    xiaomiAppID = preferences.getString("XIAOMIAPPID", "");
    xiaomiAppKey = preferences.getString("XIAOMIAPPKEY", "");
    isMiPush = XiaomiUtils.isMIUI() && !xiaomiAppID.isEmpty() && !xiaomiAppKey.isEmpty();
    if (isMiPush) {
      Log.d(TAG, "MIUI detected.");
      if (shouldInit(mContext)) {
        MiPushClient.registerPush(mContext, xiaomiAppID, xiaomiAppKey);
      }
      LoggerInterface newLogger = new LoggerInterface() {
        public void setTag(String tag) {
          // ignore
        }
        public void log(String content, Throwable t) {
          Log.d(TAG, content, t);
        }
        public void log(String content) {
          Log.d(TAG, content);
        }
      };
      Logger.setLogger(mContext, newLogger);
    }
    else {
      Log.d(TAG, "Non-MIUI detected.");
      this.mPushAgent = PushAgent.getInstance(mContext);
      this.mPushAgent.enable();
      this.mPushAgent.onAppStart();
      UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        public void launchApp(Context context, UMessage uMessage) {
          super.launchApp(context, uMessage);
          sendNotification(new JSONObject(uMessage.extra));
        }

        public void openUrl(Context context, UMessage uMessage) {
          super.openUrl(context, uMessage);
        }

        public void openActivity(Context context, UMessage uMessage) {
          super.openActivity(context, uMessage);
        }

        public void dealWithCustomAction(Context context, UMessage uMessage) {
          super.dealWithCustomAction(context, uMessage);
        }
      };
      this.mPushAgent.setNotificationClickHandler(notificationClickHandler);
      // this.mPushAgent.setDebugMode(true);
    }
  }

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("addTag")) {
      return addTag(args, callbackContext);
    }
    else if (action.equals("removeTag")) {
      return removeTag(args, callbackContext);
    }
    else if (action.equals("getTags")) {
      return getTags(callbackContext);
    }
    else if (action.equals("removeAllTags")) {
      return removeAllTags(callbackContext);
    }
    else if (action.equals("addAlias")) {
      return addAlias(args, callbackContext);
    }
    else if (action.equals("setAlias")) {
      return setAlias(args, callbackContext);
    }
    else if (action.equals("removeAlias")) {
      return removeAlias(args, callbackContext);
    }
    else if (action.equals("getRemoteNotification")) {
      return getRemoteNotification(args, callbackContext);
    }
    return false;
  }

  protected boolean getRemoteNotification(CordovaArgs args, final CallbackContext callbackContext) {
    mCallbackContext = callbackContext;
    if (pendingNotification == null) {
      try {
        pendingNotification = new JSONObject("{}");
      }
      catch (JSONException e) {
        mCallbackContext.error(e.getMessage());
      }
    }
    sendNotification(pendingNotification);
    return true;
  }

  private void sendNotification(JSONObject json) {
    if (mCallbackContext == null) {
      pendingNotification = json;
      return;
    }
    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    mCallbackContext.sendPluginResult(result);
  }

  protected boolean addTag(CordovaArgs args, final CallbackContext callbackContext) {
    if (isMiPush) {
      callbackContext.error(ERROR_INVALID_MIUI);
      return true;
    }
    final String tags;
    try {
      tags = args.getString(0);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          TagManager.Result result = mPushAgent.getTagManager().add(tags);
          if (result.status == "success") {
            callbackContext.success("添加成功");
          }
          else {
            callbackContext.success(result.toString());
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean removeTag(CordovaArgs args, final CallbackContext callbackContext) {
    if (isMiPush) {
      callbackContext.error(ERROR_INVALID_MIUI);
      return true;
    }
    final String tags;
    try {
      tags = args.getString(0);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          TagManager.Result result = mPushAgent.getTagManager().delete(tags);
          if (result.status == "success") {
            callbackContext.success("移除成功");
          }
          else {
            callbackContext.success(result.toString());
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean removeAllTags(final CallbackContext callbackContext) {
    if (isMiPush) {
      callbackContext.error(ERROR_INVALID_MIUI);
      return true;
    }
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          mPushAgent.getTagManager().reset();
          callbackContext.success("重置成功");
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean getTags(final CallbackContext callbackContext) {
    if (isMiPush) {
      callbackContext.error(ERROR_INVALID_MIUI);
      return true;
    }
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          java.util.List<String> result = mPushAgent.getTagManager().list();
          callbackContext.success(new JSONArray(result.toString()));
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean addAlias(CordovaArgs args, final CallbackContext callbackContext) {
    if (isMiPush) {
      callbackContext.error(ERROR_INVALID_MIUI);
      return true;
    }
    final String alias;
    try {
      alias = args.getString(0);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    Object obj;
    try {
      obj = args.get(1);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    final String aliasType;
    if (obj instanceof String) {
      aliasType = (String) obj;
    } else if (obj instanceof Integer){
      aliasType = mapUMessageAliasType((Integer) obj);
    } else {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          boolean result = mPushAgent.addAlias(alias, aliasType);
          if (result) {
            callbackContext.success("添加成功");
          }
          else {
            callbackContext.success("添加失败");
          }
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean setAlias(CordovaArgs args, final CallbackContext callbackContext) {
    final String alias;
    try {
      alias = args.getString(0);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    Object obj;
    try {
      obj = args.get(1);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    final String aliasType;
    if (obj instanceof String) {
      aliasType = (String) obj;
    } else if (obj instanceof Integer){
      aliasType = mapUMessageAliasType((Integer) obj);
    } else {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    if (isMiPush) {
      MiPushClient.setAlias(mContext, alias, null); 
    }
    else {
      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          try {
            boolean result = mPushAgent.addAlias(alias, aliasType);
            if (result) {
              callbackContext.success("添加成功");
            }
            else {
              callbackContext.success("添加失败");
            }
          } catch (Exception e) {
            callbackContext.error(e.getMessage());
          }
        }
      });
    }

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean removeAlias(CordovaArgs args, final CallbackContext callbackContext) {
    final String alias;
    try {
      alias = args.getString(0);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    Object obj;
    try {
      obj = args.get(1);
    } catch (JSONException e) {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    final String aliasType;
    if (obj instanceof String) {
      aliasType = (String) obj;
    } else if (obj instanceof Integer){
      aliasType = mapUMessageAliasType((Integer) obj);
    } else {
      callbackContext.error(ERROR_INVALID_PARAMETERS);
      return true;
    }

    if (isMiPush) {
      MiPushClient.unsetAlias(mContext, alias, null);
    }
    else {
      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          try {
            boolean result = mPushAgent.removeAlias(alias, aliasType);
            if (result) {
              callbackContext.success("移除成功");
            }
            else {
              callbackContext.success("移除失败");
            }
          } catch (Exception e) {
            callbackContext.error(e.getMessage());
          }
        }
      });
    }

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  private String mapUMessageAliasType(int intType) {
    switch (intType) {
      case ALIAS_TYPE_SINA:
        return "SINA_WEIBO";
      case ALIAS_TYPE_TENCENT:
        return "TENCENT_WEIBO";
      case ALIAS_TYPE_QQ:
        return "QQ";
      case ALIAS_TYPE_WEIXIN:
        return "WEIXIN";
      case ALIAS_TYPE_BAIDU:
        return "BAIDU";
      case ALIAS_TYPE_RENREN:
        return "RENREN";
      case ALIAS_TYPE_KAIXIN:
        return "KAIXIN";
      case ALIAS_TYPE_DOUBAN:
        return "DOUBAN";
      case ALIAS_TYPE_FACEBOOK:
        return "FACEBOOK";
      case ALIAS_TYPE_TWITTER:
        return "TWITTER";
      default:
        return "UNKOWN";
    }
  }

  private void sendNoResultPluginResult(CallbackContext callbackContext) {
    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  private boolean shouldInit(Context context) {
    ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
    List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
    String mainProcessName = context.getPackageName();
    int myPid = Process.myPid();
    for (RunningAppProcessInfo info : processInfos) {
      if (info.pid == myPid && mainProcessName.equals(info.processName)) {
        return true;
      }
    }
    return false;
  }
}
