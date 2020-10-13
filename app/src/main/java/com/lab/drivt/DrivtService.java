package com.lab.drivt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lab.drivt.interfaces.DriverInterface;
import com.lab.drivt.providers.ProviderFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DrivtService extends AccessibilityService {

	private String acao = "listen";
	private String packageName = "";
	private List<String> providers;
	private Boolean isServiceEnabled = false;


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("DrivtService", "onServiceConnected");
		android.os.Debug.waitForDebugger();


		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				acao = intent.getStringExtra("acao");

				if (!acao.equals("listen")) {
					providers = new LinkedList<>(Arrays.asList(intent.getStringArrayExtra("providers")));
					packageName = providers.get(0);
				}
			}
		};

		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("serviceReceiver"));

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.isServiceEnabled = false;
	}



	private boolean ExistsNodeOrChildren(AccessibilityNodeInfo n) {

		// For God sake do this first if you think n might actually be null!!!
		// Or just don't do it, and let n.toString() throw a NPE. (BAD IDEA)
		if (n == null)
			return false;

		for (int i = 0; i < n.getChildCount(); i++) {
			AccessibilityNodeInfo child = n.getChild(i);

			if (child != null && child.getViewIdResourceName() != null && child.getPackageName().equals("com.android.settings")) {
				// List<AccessibilityNodeInfo> tester =
				// getRootInActiveWindow().findAccessibilityNodeInfosByText("SET DESTINATION");
				// if(tester.size() > 0){
				String x = child.getViewIdResourceName();
				x = x != null ? x.replace("ee.mtakso.driver:id/", "") : "";

				String z = child.getText() != null ? child.getText().toString() : "";

				String y = child.getParent() != null && child.getParent().getViewIdResourceName() != null
						? child.getParent().getViewIdResourceName()
						: "";
				y = y != null ? y.replace("ee.mtakso.driver:id/", "") : "";

				String w = child.getClassName().toString();

				String log = w + "\t" + y + "\t" + x + "\t" + z;
				//appendLog("drivtService.csv", log);
				Log.e("DRIVT SERVICE", log);
				// }
			}
			// Skip recursion the times n.getChild() returns n.
			// The check really is this simple, because we can only skip this
			// When the child is literally the same object, otherwise it might be
			// a node that has identical properties, which can happen.
			if (child != n) {
				if (ExistsNodeOrChildren(n.getChild(i)))
					return true;
			}
		}

		return false;
	}

	@Override
	public void onAccessibilityEvent(final AccessibilityEvent event) {

		//ExistsNodeOrChildren(getRootInActiveWindow());

		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

			DriverInterface provider = ProviderFactory.getProvider(event.getPackageName().toString());

			if (provider != null) {
				switch (acao) {
					case "makeOnline":
						Log.e("DRIVT-TESTE", "1");
						if (event.getPackageName().equals(packageName)) {
							Log.e("DRIVT-TESTE", "2");
							Boolean sucesso = provider.goOnline(this, getRootInActiveWindow());
							Log.e("DRIVT-TESTE", "3");

							if (sucesso) {
								Log.e("DRIVT-TESTE", "4");
								returnLoopToMainClass("makeOnline", "online");
								Log.e("DRIVT-TESTE", "5");
							}
						}
						break;
					case "makeOffline":
						if (event.getPackageName().equals(packageName)) {
							Boolean sucesso = provider.goOfflineStep1(this, getRootInActiveWindow());

							if (sucesso) {
								if (provider.hasOfflineStep2()) {
									acao = "makeOffline2";
								} else {
									returnLoopToMainClass("makeOffline", "offline");
								}
							}
						}
						break;
					case "makeOffline2":
						if (event.getPackageName().equals(packageName)) {
							Boolean sucesso = provider.goOfflineStep2(this, getRootInActiveWindow());

							if (sucesso) {
								returnLoopToMainClass("makeOffline", "offline");
							}
						}
						break;
					case "listen":
						String status = provider.getStatus(this, getRootInActiveWindow());

						if (status != null) {
							notifyStatusToMainClass(event.getPackageName().toString(), status);
						}

						break;
				}
			}
			else
			{
				if(event.getPackageName().equals("com.android.settings"))
				{
					List<AccessibilityNodeInfo> drivtServiceTitle = getRootInActiveWindow().findAccessibilityNodeInfosByText("drivt");
					List<AccessibilityNodeInfo> onOffWidget = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.android.settings:id/switch_widget");

					if(drivtServiceTitle.size() > 0 && onOffWidget.size() > 0) {

						if (!this.isServiceEnabled) {
							notifyStatusToMainClass(event.getPackageName().toString(), "serviceJustEnabled");
						}
						this.isServiceEnabled = true;
					}
				}
			}
		}


	}

	private void returnLoopToMainClass(String acao, String status) {
		String log = packageName + "\t" + status;
		appendLog("drivtProviderStatus.csv", log);

		providers.remove(0);
		this.acao = "";

		Intent intent = new Intent("classReceiver");
		intent.putExtra("acao", acao);
		intent.putExtra("lastProvider", packageName);
		intent.putExtra("status", status);
		intent.putExtra("providers", providers.toArray(new String[0]));
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		packageName = "";
	}

	private void notifyStatusToMainClass(String packageName, String status) {
		String log = packageName + "\t" + status;
		appendLog("drivtProviderStatus.csv", log);

		Intent intent = new Intent("classReceiver");
		intent.putExtra("acao", "updateStatus");
		intent.putExtra("provider", packageName);
		intent.putExtra("status", status);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	public void onInterrupt() {
	}

	private void appendLog(String fileName, String text) {
		String todayAsString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		text = todayAsString + "\t" + text;

		File logFile = new File(this.getFilesDir(), fileName);
		// File logFile = new File("sdcard/drivtLog.txt");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
