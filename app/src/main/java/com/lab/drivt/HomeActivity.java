package com.lab.drivt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import com.lab.drivt.interfaces.DriverInterface;
import com.lab.drivt.providers.ProviderFactory;
import com.lab.drivt.utils.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String acao = intent.getStringExtra("acao");
			switch (acao) {
				case "makeOffline":
				case "makeOnline":
					String provider = "Provider = " + intent.getStringExtra("lastProvider");
					String status = intent.getStringExtra("status");

					List<String> providers = Arrays.asList(intent.getStringArrayExtra("providers"));
					if (providers.size() > 0) {
						Tools.callApp(context, acao, providers);
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						Intent broadcast1 = new Intent("serviceReceiver");
						broadcast1.putExtra("acao", "listen");
						LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast1);

						//Tools.openApp(context, "com.lab.drivt", false);
					}
					break;
				case "updateStatus":
					provider = intent.getStringExtra("provider");
					status = intent.getStringExtra("status");

//					TextView lblStatus = (TextView)findViewById(R.id.statusLbl);
//					lblStatus.setText(status);

					/*if(status.equals("serviceJustEnabled"))
					{
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Tools.openApp(context, "com.lab.drivt", false);
					}*/

					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("classReceiver"));

	}

	public void OnlineAll(View view)
	{
		String action = "onlineAll";

		ExecutarAcao(action);
	}

	public void OfflineAll(View view)
	{
		String action = "offlineAll";

		ExecutarAcao(action);
	}

	private void ExecutarAcao(String action)
	{
		switch (action)
		{
			case "isAccessibilityEnabled":
				if(!Tools.internalIsAccessibilityOn(Objects.requireNonNull(HomeActivity.this), DrivtService.class))
				{
				}
				else
				{
				}
				break;
			case "enableAcessibility":
				if (!Tools.internalIsAccessibilityOn(Objects.requireNonNull(HomeActivity.this), DrivtService.class)) {
					Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
					HomeActivity.this.startActivity(intent);
				}
				break;
			case "onlineAll":
				List<String> providers = new ArrayList<>();
				providers.add("ee.mtakso.driver");
				// providers.add("com.example.fakebolt");
				// providers.add("com.example.fakekapten");

				Tools.callApp(HomeActivity.this, "makeOnline", providers);
				break;
			case "offlineAll":
				providers = new ArrayList<>();
				providers.add("ee.mtakso.driver");
				// providers.add("com.example.fakebolt");
				// providers.add("com.example.fakekapten");

				Tools.callApp(HomeActivity.this, "makeOffline", providers);
				break;
		}
	}
}