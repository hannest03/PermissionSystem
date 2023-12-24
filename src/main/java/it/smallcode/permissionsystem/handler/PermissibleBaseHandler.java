package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.permissions.OptimizedPermissions;
import it.smallcode.permissionsystem.permissions.PermissionChecker;
import it.smallcode.permissionsystem.permissions.PermissionManager;
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

public class PermissibleBaseHandler implements Listener {

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
}
