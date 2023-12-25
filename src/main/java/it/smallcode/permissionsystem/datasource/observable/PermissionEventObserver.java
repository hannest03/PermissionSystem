package it.smallcode.permissionsystem.datasource.observable;

import java.util.UUID;

public interface PermissionEventObserver {

  void onEvent(PermissionEventType eventType);

  void onEvent(PermissionEventType eventType, UUID uuid);
}
