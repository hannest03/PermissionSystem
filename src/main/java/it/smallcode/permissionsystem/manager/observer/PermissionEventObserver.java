package it.smallcode.permissionsystem.manager.observer;

import java.util.UUID;

public interface PermissionEventObserver {

  void onEvent(PermissionEventType eventType);

  void onEvent(PermissionEventType eventType, UUID uuid);
}
