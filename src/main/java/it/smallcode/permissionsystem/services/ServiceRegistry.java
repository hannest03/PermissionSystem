package it.smallcode.permissionsystem.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {

  private final Map<Class<? extends Service>, Service> services = new HashMap<>();

  public <T extends Service> void registerService(Class<T> serviceInterface, T service) {
    services.put(serviceInterface, service);
  }

  public <T extends Service> T getService(Class<T> serviceInterface) {
    if (!services.containsKey(serviceInterface)) {
      return null;
    }
    return (T) services.get(serviceInterface);
  }
}
