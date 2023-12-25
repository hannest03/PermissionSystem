package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.permissions.PermissionManager;
import it.smallcode.permissionsystem.utils.BukkitBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatMessageHandler implements Listener {

  private final Plugin plugin;
  private final PermissionManager permissionManager;

  public ChatMessageHandler(Plugin plugin, PermissionManager permissionManager) {
    this.plugin = plugin;
    this.permissionManager = permissionManager;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    e.setCancelled(true);

    Player player = e.getPlayer();
    String message = e.getMessage();

    Group group = permissionManager.getPrimaryGroup(player.getUniqueId());
    if (group == null) {
      return;
    }

    String playerName = group.getPrefix() + player.getName();

    //TODO: add translation
    final String messagePattern = "%player% &8|&f %message%";
    final String toSend = messagePattern.replaceAll("%player%", playerName)
        .replaceAll("%message%", message);

    BukkitBroadcast.broadcast(ChatColor.translateAlternateColorCodes('&', toSend));
  }
}
