package xwang.cordova.umeng.push;

import android.util.Log;

import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.PushAgent;
import com.umeng.message.tag.TagManager;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.UTrack;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
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
  public static final String TAG = "Cordova.Plugin.Push";

  protected PushAgent mPushAgent;

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    this.mPushAgent = PushAgent.getInstance(webView.getContext());
    this.mPushAgent.onAppStart();
    this.mPushAgent.register(new IUmengRegisterCallback() {
      @Override
      public void onSuccess(String deviceToken) {
        Log.d(TAG, "device token: " + deviceToken);
      }

      @Override
      public void onFailure(String s, String s1) {
        Log.d(TAG, s + s1);
      }
    });
    this.mPushAgent.setDebugMode(true);
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
    else if (action.equals("removeAlias")) {
      return removeAlias(args, callbackContext);
    }
    return false;
  }

  protected boolean addTag(CordovaArgs args, final CallbackContext callbackContext) {
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
          mPushAgent.getTagManager().add(new TagManager.TCallBack() {
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
              if (isSuccess) {
                callbackContext.success("添加成功");
              }
              else {
                callbackContext.success(result.toString());
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean removeTag(CordovaArgs args, final CallbackContext callbackContext) {
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
          mPushAgent.getTagManager().delete(new TagManager.TCallBack() {
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
              if (isSuccess) {
                callbackContext.success("移除成功");
              }
              else {
                callbackContext.success(result.toString());
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean removeAllTags(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          mPushAgent.getTagManager().reset(new TagManager.TCallBack() {
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
              if (isSuccess) {
                callbackContext.success("重置成功");
              }
              else {
                callbackContext.success(result.toString());
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean getTags(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          mPushAgent.getTagManager().list(new TagManager.TagListCallBack() {
            public void onMessage(boolean isSuccess, java.util.List<String> result) {
              if (isSuccess) {
                try {
                  callbackContext.success(new JSONArray(result.toString()));
                }
                catch (Exception e) {
                }
              }
              else {
                callbackContext.success(result.toString());
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

    sendNoResultPluginResult(callbackContext);
    return true;
  }

  protected boolean addAlias(CordovaArgs args, final CallbackContext callbackContext) {
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
          mPushAgent.addAlias(alias, aliasType, new UTrack.ICallBack() {
            public void onMessage(boolean isSuccess, String message) {
              if (isSuccess) {
                callbackContext.success("添加成功");
              }
              else {
                callbackContext.success("添加失败");
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

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

    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        try {
          mPushAgent.removeAlias(alias, aliasType, new UTrack.ICallBack() {
            public void onMessage(boolean isSuccess, String message) {
              if (isSuccess) {
                callbackContext.success("移除成功");
              }
              else {
                callbackContext.success("移除失败");
              }
            }
          });
        } catch (Exception e) {
          callbackContext.error(e.getMessage());
        }
      }
    });

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
}
