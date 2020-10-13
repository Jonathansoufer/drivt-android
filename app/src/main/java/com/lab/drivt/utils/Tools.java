package com.lab.drivt.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class Tools {
  public static boolean openApp(Context context, String packageName, Boolean forceRestart) {
    PackageManager manager = context.getPackageManager();
    try {
      Intent intent = manager.getLaunchIntentForPackage(packageName);
      if (intent == null) {
        return false;
        // throw new ActivityNotFoundException();
      }

      if (forceRestart) {
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
      } else {
        context.startActivity(intent);
      }
      return true;
    } catch (ActivityNotFoundException e) {
      return false;
    }
  }

  public static GestureDescription createClick(float x, float y) {
    // for a single tap a duration of 1 ms is enough
    final int DURATION = 200;

    Path clickPath = new Path();
    clickPath.moveTo(x, y);
    clickPath.lineTo(x + 700, y);
    GestureDescription.StrokeDescription clickStroke = new GestureDescription.StrokeDescription(clickPath, 50,
        DURATION);
    GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
    clickBuilder.addStroke(clickStroke);
    return clickBuilder.build();
  }

  public static void callApp(Context context, String acao, List<String> providers) {
    Intent broadcast1 = new Intent("serviceReceiver");
    broadcast1.putExtra("providers", providers.toArray(new String[0]));
    broadcast1.putExtra("acao", acao);

    LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast1);
    openApp(context, providers.get(0), true);
  }

  public static boolean internalIsAccessibilityOn(Context context, Class<? extends AccessibilityService> clazz) {
    int accessibilityEnabled = 0;
    final String service = context.getPackageName() + "/" + clazz.getCanonicalName();
    try {
      accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
        Settings.Secure.ACCESSIBILITY_ENABLED);
    } catch (Settings.SettingNotFoundException ignored) {
    }
    TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
    if (accessibilityEnabled == 1) {
      String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
      if (settingValue != null) {
        colonSplitter.setString(settingValue);
        while (colonSplitter.hasNext()) {
          String accessibilityService = colonSplitter.next();

          if (accessibilityService.equalsIgnoreCase(service)) {
            return true;
          }
        }
      }
    }

    return false;
  }
}


