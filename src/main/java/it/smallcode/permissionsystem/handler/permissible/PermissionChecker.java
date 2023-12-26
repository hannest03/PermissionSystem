package it.smallcode.permissionsystem.handler.permissible;

public interface PermissionChecker {

  boolean hasPermission(String permission);

  boolean isPermissionSet(String permission);
}
