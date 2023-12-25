package it.smallcode.permissionsystem.handler.permissible;

import it.smallcode.permissionsystem.permissions.PermissionChecker;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

public class PlayerPermissibleBase extends PermissibleBase {

  private PermissionChecker permissionChecker;

  public PlayerPermissibleBase(Player player) {
    this(player, null);
  }

  public PlayerPermissibleBase(Player player, PermissionChecker permissionChecker) {
    super(player);
    this.permissionChecker = permissionChecker;
  }

  @Override
  public boolean hasPermission(String permission) {
    if (permissionChecker != null && permissionChecker.isPermissionSet(permission)) {
      return permissionChecker.hasPermission(permission);
    }
    return super.hasPermission(permission);
  }

  @Override
  public boolean hasPermission(Permission permission) {
    if (permission == null) {
      throw new IllegalArgumentException("Permission is null!");
    }
    return hasPermission(permission.getName());
  }

  @Override
  public boolean isPermissionSet(String permission) {
    if (permissionChecker != null && permissionChecker.isPermissionSet(permission)) {
      return true;
    }
    return super.isPermissionSet(permission);
  }

  @Override
  public boolean isPermissionSet(Permission permission) {
    if (permission == null) {
      throw new IllegalArgumentException("Permission is null!");
    }
    return isPermissionSet(permission.getName());
  }

  public void setPermissionChecker(
      PermissionChecker permissionChecker) {
    this.permissionChecker = permissionChecker;
  }
}
