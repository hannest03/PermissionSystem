package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.ServiceRegistry;
import it.smallcode.permissionsystem.utils.BukkitBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinMessageHandler implements Listener {

  private final Plugin plugin;
  private final PermissionService permissionService;

  public JoinMessageHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();

    e.setJoinMessage(null);
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getPrimaryGroup(player.getUniqueId());
      if (group == null) {
        return;
      }

      //TODO: add translation
      String joinMessage = "%player%&7 joined the game";
      final String message = joinMessage.replaceAll("%player%",
          group.getPrefix() + player.getName());

      BukkitBroadcast.broadcast(ChatColor.translateAlternateColorCodes('&', message));
    });
  }

}
