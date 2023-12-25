package it.smallcode.permissionsystem.handler;

import fr.mrmicky.fastboard.FastBoard;
import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.observer.PermissionEventObserver;
import it.smallcode.permissionsystem.manager.observer.PermissionEventType;
import it.smallcode.permissionsystem.models.Group;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class SidebarHandler implements Listener, PermissionEventObserver {

  private final Plugin plugin;
  private final PermissionManager permissionManager;

  private final Map<UUID, FastBoard> scoreboards = new HashMap<>();

  public SidebarHandler(Plugin plugin, PermissionManager permissionManager) {
    this.plugin = plugin;
    this.permissionManager = permissionManager;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();
    scoreboards.put(player.getUniqueId(), new FastBoard(player));

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(player.getUniqueId());
    });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    final UUID uuid = e.getPlayer().getUniqueId();
    scoreboards.get(uuid).delete();
    scoreboards.remove(uuid);
  }

  private void updateScoreboard(UUID uuid) {
    Group group = permissionManager.getPrimaryGroup(uuid);
    String groupName = group != null ? group.getName() : "Undefined";

    //TODO: add translation
    final String title = "Permission System";

    FastBoard scoreboard = scoreboards.get(uuid);
    scoreboard.updateTitle(title);
    scoreboard.updateLines(groupName);
  }

  @Override
  public void onEvent(PermissionEventType eventType) {

  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(uuid);
    });
  }
}