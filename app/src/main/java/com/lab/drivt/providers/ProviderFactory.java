package com.drivt.providers;

import com.drivt.interfaces.DriverInterface;

public class ProviderFactory {

  public static DriverInterface getProvider(String packageName) {
    switch (packageName) {
      case "ee.mtakso.driver":
        DriverInterface provider = new BoltDriver(packageName, true);
        return provider;
      default:
        return null;
    }
  }

}
