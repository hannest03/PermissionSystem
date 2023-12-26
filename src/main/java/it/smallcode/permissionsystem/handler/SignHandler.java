package it.smallcode.permissionsystem.handler;

import com.google.common.collect.ImmutableList;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventType;
import it.smallcode.permissionsystem.datasource.observable.SignEventObserver;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.SignLocation;
import it.smallcode.permissionsystem.models.adapter.SignLocationAdapter;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.ServiceRegistry;
import it.smallcode.permissionsystem.services.SignService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class SignHandler implements Listener, PermissionEventObserver, SignEventObserver {

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final SignService signService;

  private final SignLocationAdapter signLocationAdapter = new SignLocationAdapter();

  public SignHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.signService = serviceRegistry.getService(SignService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Set<SignLocation> locations = signService.getSignLocations();

      for (SignLocation signLocation : locations) {
        if (player.getWorld().getName().equals(signLocation.world())) {
          updateSign(player, signLocation);
        }
      }
    });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBreak(BlockBreakEvent e) {
    if (e.getBlock().getType() == Material.OAK_SIGN) {
      final Location location = e.getBlock().getLocation();
      if (location.getWorld() == null) {
        return;
      }

      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        if (signService.isSignLocation(signLocationAdapter.fromLocation(location))) {
          signService.removeSignLocation(signLocationAdapter.fromLocation(location));
        }
      });
    }
  }

  private void updateSign(Player player, SignLocation signLocation) {
    Group group = permissionService.getPrimaryGroup(player.getUniqueId());

    // TODO: add translation

    String[] lines = {
        ChatColor.translateAlternateColorCodes('&', "ID: " + group.getId()),
        ChatColor.translateAlternateColorCodes('&', "Group: " + group.getName()),
        ChatColor.translateAlternateColorCodes('&', group.getPrefix() + player.getName()),
        ChatColor.translateAlternateColorCodes('&', "Priority: " + group.getPriority())
    };

    player.sendSignChange(signLocationAdapter.toLocation(signLocation), lines);
  }

  @Override
  public void onEvent(PermissionEventType eventType) {

  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType == PermissionEventType.PLAYER_GROUP_CHANGED) {
      final Player player = Bukkit.getPlayer(uuid);
      if (player == null || !player.isOnline()) {
        return;
      }

      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Set<SignLocation> locations = signService.getSignLocations();

        for (SignLocation signLocation : locations) {
          if (player.getWorld().getName().equals(signLocation.world())) {
            updateSign(player, signLocation);
          }
        }
      });
    }
  }

  @Override
  public void onNewSign(SignLocation signLocation) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
      for (Player player : players) {
        updateSign(player, signLocation);
      }
    });
  }
}
