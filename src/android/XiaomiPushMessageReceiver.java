package xwang.cordova.umeng.push;

import android.content.Context;
import android.util.Log;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushClient;

public class XiaomiPushMessageReceiver extends PushMessageReceiver {
  public static final String TAG = "xwang.cordova.umeng.push";

  public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
    super.onReceivePassThroughMessage(context, message);
    Log.d(TAG, message.toString());
  }

  public void onNotificationMessageClicked(Context context, MiPushMessage message) {
    super.onNotificationMessageClicked(context, message);
    Log.d(TAG, message.toString());
  }

  public void onNotificationMessageArrived(Context context, MiPushMessage message) {
    super.onNotificationMessageArrived(context, message);
    Log.d(TAG, message.toString());
  }

  public void onCommandResult(Context context, MiPushCommandMessage message) {
    super.onCommandResult(context, message);
    Log.d(TAG, message.toString());
  }

  public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
    super.onReceiveRegisterResult(context, message);
    Log.d(TAG, message.toString());
  }
}
