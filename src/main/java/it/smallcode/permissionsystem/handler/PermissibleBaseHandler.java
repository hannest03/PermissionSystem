package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.datasource.observable.PermissionEventObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventType;
import it.smallcode.permissionsystem.handler.permissible.PlayerPermissibleBase;
import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.permissions.OptimizedPermissions;
import it.smallcode.permissionsystem.permissions.PermissionChecker;
import it.smallcode.permissionsystem.utils.PermissibleBaseUtils;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PermissibleBaseHandler implements Listener, PermissionEventObserver {

  private final Plugin plugin;

  private final PermissionManager permissionManager;
  private final PermissibleBaseUtils permissibleBaseUtils;

  private final HashMap<UUID, PlayerPermissibleBase> playerPermissible = new HashMap<>();

  public PermissibleBaseHandler(Plugin plugin, PermissionManager permissionManager,
      PermissibleBaseUtils permissibleBaseUtils) {
    this.plugin = plugin;
    this.permissionManager = permissionManager;
    this.permissibleBaseUtils = permissibleBaseUtils;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent e) {
    final Player player = e.getPlayer();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updatePermissibleBase(player);

      // Sends new command suggestion information
      Bukkit.getScheduler().runTask(plugin, player::updateCommands);
    });
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    playerPermissible.remove(e.getPlayer().getUniqueId());
  }

  private void updatePermissibleBase(Player player) {
    PlayerPermissibleBase permissibleBase = playerPermissible.getOrDefault(player.getUniqueId(),
        null);
    if (permissibleBase == null) {
      permissibleBase = new PlayerPermissibleBase(player);
      permissibleBaseUtils.setPermissibleBase(player, permissibleBase);
      playerPermissible.put(player.getUniqueId(), permissibleBase);
    }
    Set<PermissionInfo> permissionInfos = permissionManager.getPlayerPermissions(
        player.getUniqueId());
    PermissionChecker permissionChecker = new OptimizedPermissions(permissionInfos);
    permissibleBase.setPermissionChecker(permissionChecker);
  }

  @Override
  public void onEvent(PermissionEventType eventType) {
  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType != PermissionEventType.GROUP_PERMISSION_CHANGED
        && eventType != PermissionEventType.PLAYER_PERMISSION_CHANGED) {
      return;
    }
    final Player player = Bukkit.getPlayer(uuid);
    if (player == null || !player.isOnline()) {
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updatePermissibleBase(player);
      Bukkit.getScheduler().runTask(plugin, player::updateCommands);
    });
  }
}
