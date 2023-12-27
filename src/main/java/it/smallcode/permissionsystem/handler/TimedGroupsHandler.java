package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.time.Instant;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TimedGroupsHandler {

  private final PermissionService permissionService;

  public TimedGroupsHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.permissionService = serviceRegistry.getService(PermissionService.class);

    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        List<PlayerGroup> playerGroups = permissionService.getPlayerGroups(player.getUniqueId());
        for (PlayerGroup playerGroup : playerGroups) {
          if (playerGroup.end() == null) {
            continue;
          }
          if (Instant.now().isAfter(playerGroup.end())) {
            permissionService.removePlayerGroup(player.getUniqueId(), playerGroup.group());
          }
        }
      }
    }, 0, 20);
  }
}
