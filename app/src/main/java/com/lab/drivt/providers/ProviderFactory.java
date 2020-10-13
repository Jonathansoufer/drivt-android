package com.lab.drivt.providers;

import com.lab.drivt.interfaces.DriverInterface;

public class ProviderFactory {

  public static DriverInterface getProvider(String packageName) {
    switch (packageName) {
      case "ee.mtakso.driver":
        DriverInterface provider = new com.lab.drivt.providers.BoltDriver(packageName, true);
        return provider;
      default:
        return null;
    }
  }

}
