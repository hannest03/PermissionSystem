package it.smallcode.permissionsystem.utils;

import it.smallcode.permissionsystem.services.registry.Service;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

public interface PermissibleBaseUtils extends Service {

  void setPermissibleBase(Player p, PermissibleBase permissibleBase);
}
