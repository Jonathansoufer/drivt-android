package com.lab.drivt.providers;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lab.drivt.interfaces.DriverInterface;
import com.lab.drivt.utils.Tools;

import java.util.List;

public class BoltDriver implements DriverInterface {

  private String packageName;
  private Boolean offlineStep2;

  public BoltDriver(String packageName, Boolean hasOfflineStep2) {
    this.packageName = packageName;
    this.offlineStep2 = hasOfflineStep2;
  }

  @Override
  public Boolean goOnline(AccessibilityService context, AccessibilityNodeInfo nodeInfo) {

    Log.e("DRIVT-TESTE", "2.1");
    List<AccessibilityNodeInfo> btnOnline = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/startWorkBtn");
    Log.e("DRIVT-TESTE", "2.2");

    if(btnOnline != null && !btnOnline.isEmpty()) {
      Log.e("DRIVT-TESTE", "2.3");

      List<AccessibilityNodeInfo> slider = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/slideIcon");
      Log.e("DRIVT-TESTE", "2.4");

      try {
        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to
        Log.e("DRIVT-TESTE", "2.5");

        Rect rect = new Rect();
        slider.get(0).getBoundsInScreen(rect);
        context.dispatchGesture(Tools.createClick(rect.centerX(), rect.centerY()), null, null);
        Log.e("DRIVT-TESTE", "2.6");

        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to
        Log.e("DRIVT-TESTE", "2.7");

        return true;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }

    return false;
  }

  @Override
  public Boolean hasOfflineStep2() {
    return this.offlineStep2;
  }

  @Override
  public Boolean goOfflineStep1(AccessibilityService context, AccessibilityNodeInfo nodeInfo) {

    List<AccessibilityNodeInfo> btnOffline = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/homeOfflineButton");

    if (btnOffline.size() == 1) {

      List<AccessibilityNodeInfo> slider = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/slideIcon");

      try {
        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to

        Rect rect = new Rect();
        slider.get(0).getBoundsInScreen(rect);
        context.dispatchGesture(Tools.createClick(rect.centerX(), rect.centerY()), null, null);

        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to

      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return true;

    }

    return false;
  }

  @Override
  public Boolean goOfflineStep2(AccessibilityService context, AccessibilityNodeInfo nodeInfo) {

    List<AccessibilityNodeInfo> container = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/container");

    if (container.size() > 0) {
      try {
        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to

        container.get(0).getChild(3).performAction(AccessibilityNodeInfo.ACTION_CLICK);

        Thread.sleep(500); // hack for certain devices in which the immediate back click is too fast to

      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return true;

    }

    return false;
  }

  @Override
  public String getStatus(AccessibilityService context, AccessibilityNodeInfo nodeInfo) {

    if(nodeInfo != null)
    {
      List<AccessibilityNodeInfo> nodeStatus = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/startWorkBtn");

      if (nodeStatus.size() > 0) {
        return "offline";
      }

      nodeStatus = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/homeOfflineButton");

      if (nodeStatus.size() > 0) {
        return "online";
      }

      nodeStatus = nodeInfo.findAccessibilityNodeInfosByViewId(packageName + ":id/active_order_swipe_button");

      if (nodeStatus.size() > 0) {
        return "onride";
      }
    }

    return null;
  }

}
