package com.lab.drivt.interfaces;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

public interface DriverInterface {

  public Boolean goOnline(AccessibilityService context, AccessibilityNodeInfo nodeInfo);

  public Boolean goOfflineStep1(AccessibilityService context, AccessibilityNodeInfo nodeInfo);

  public Boolean goOfflineStep2(AccessibilityService context, AccessibilityNodeInfo nodeInfo);

  public String getStatus(AccessibilityService context, AccessibilityNodeInfo nodeInfo);

  public Boolean hasOfflineStep2();

}
