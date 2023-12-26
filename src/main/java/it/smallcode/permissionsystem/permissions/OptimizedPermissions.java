package it.smallcode.permissionsystem.permissions;

import it.smallcode.permissionsystem.models.PermissionInfo;
import java.util.HashMap;
import java.util.Set;

public class OptimizedPermissions implements PermissionChecker {

  private final HashMap<String, Boolean> permissions = new HashMap<>();

  public OptimizedPermissions(Set<PermissionInfo> permissionInfos) {
    permissionInfos.stream().sorted((info1, info2) -> {
      int priorityCompare = Integer.compare(info1.priority(), info2.priority());
      if (priorityCompare != 0) {
        return priorityCompare;
      }

      char permissionStart1 = info1.permission().charAt(0);
      char permissionStart2 = info2.permission().charAt(0);

      if (permissionStart1 == '-') {
        return -1;
      }

      if (permissionStart2 == '-') {
        return 1;
      }

      return 0;
    }).forEach(info -> {
      String permission = info.permission();
      if (permission.startsWith("-")) {
        permission = permission.substring(1);
      }
      if (!permissions.containsKey(permission)) {
        permissions.put(permission, !info.permission().startsWith("-"));
      }
    });
  }

  public boolean hasPermission(String permission) {
    if (permissions.containsKey(permission)) {
      return permissions.get(permission);
    }

    if (permissions.containsKey("*")) {
      return permissions.get("*");
    }

    return false;
  }

  @Override
  public boolean isPermissionSet(String permission) {
    if (permissions.containsKey(permission)) {
      return true;
    }
    return permissions.containsKey("*");
  }

  public HashMap<String, Boolean> getPermissions() {
    return permissions;
  }
}
