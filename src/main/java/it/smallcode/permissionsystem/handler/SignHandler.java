package it.smallcode.permissionsystem.handler;

import com.google.common.collect.ImmutableList;
import it.smallcode.permissionsystem.datasource.observable.LanguageChangeObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventType;
import it.smallcode.permissionsystem.datasource.observable.SignEventObserver;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.SignLocation;
import it.smallcode.permissionsystem.models.adapter.SignLocationAdapter;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.SignService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.LinkedList;
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

public class SignHandler implements Listener, PermissionEventObserver, SignEventObserver,
    LanguageChangeObserver {

  private static final String SIGN_FIRST_LINE = "sign_first_line";
  private static final String SIGN_SECOND_LINE = "sign_second_line";
  private static final String SIGN_THIRD_LINE = "sign_third_line";
  private static final String SIGN_FOURTH_LINE = "sign_fourth_line";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final SignService signService;
  private final LanguageService languageService;

  private final SignLocationAdapter signLocationAdapter = new SignLocationAdapter();

  public SignHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.signService = serviceRegistry.getService(SignService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

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

  @Override
  public void onEvent(PermissionEventType eventType) {
    if (eventType != PermissionEventType.GROUP_CHANGED) {
      return;
    }
    List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    for (Player player : players) {
      eventUpdate(player.getUniqueId());
    }
  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType == PermissionEventType.PLAYER_GROUP_CHANGED) {
      eventUpdate(uuid);
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

  @Override
  public void onLanguageChange(UUID uuid) {
    eventUpdate(uuid);
  }

  private void eventUpdate(UUID uuid) {
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

  private void updateSign(Player player, SignLocation signLocation) {
    Group group = permissionService.getPrimaryGroup(player.getUniqueId());
    Language language = languageService.getLanguage(player.getUniqueId());

    List<String> rawLines = new LinkedList<>();
    rawLines.add(language.getTranslation(SIGN_FIRST_LINE));
    rawLines.add(language.getTranslation(SIGN_SECOND_LINE));
    rawLines.add(language.getTranslation(SIGN_THIRD_LINE));
    rawLines.add(language.getTranslation(SIGN_FOURTH_LINE));

    String[] lines = rawLines.stream().map(
            line ->
                line
                    .replaceAll("%id%", group.getId().toString())
                    .replaceAll("%group%", group.getName())
                    .replaceAll("%prefix%", group.getPrefix())
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%priority%", String.valueOf(group.getPriority()))
        ).map(line -> ChatColor.translateAlternateColorCodes('&', line))
        .toArray(String[]::new);

    player.sendSignChange(signLocationAdapter.toLocation(signLocation), lines);
  }
}
