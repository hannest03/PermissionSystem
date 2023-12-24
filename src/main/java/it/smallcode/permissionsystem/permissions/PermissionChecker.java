package it.smallcode.permissionsystem.permissions;

public interface PermissionChecker {

  boolean hasPermission(String permission);

  boolean isPermissionSet(String permission);
}
