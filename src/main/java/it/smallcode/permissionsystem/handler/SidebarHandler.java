package it.smallcode.permissionsystem.handler;

import com.google.common.collect.ImmutableList;
import fr.mrmicky.fastboard.FastBoard;
import it.smallcode.permissionsystem.datasource.observable.LanguageChangeObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventType;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class SidebarHandler implements Listener, PermissionEventObserver, LanguageChangeObserver {

  private static final String SCOREBOARD_UNDEFINED = "scoreboard_undefined";
  private static final String SCOREBOARD_TITLE = "scoreboard_title";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final LanguageService languageService;

  private final Map<UUID, FastBoard> scoreboards = new HashMap<>();

  public SidebarHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

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
    Language language = languageService.getLanguage(uuid);

    Group group = permissionService.getPrimaryGroup(uuid);
    String groupName =
        group != null ? group.getName() : language.getTranslation(SCOREBOARD_UNDEFINED);

    final String title = language.getTranslation(SCOREBOARD_TITLE);

    FastBoard scoreboard = scoreboards.get(uuid);
    if (scoreboard == null) {
      return;
    }
    scoreboard.updateTitle(title);
    scoreboard.updateLines(groupName);
  }

  @Override
  public void onEvent(PermissionEventType eventType) {
    if (eventType != PermissionEventType.GROUP_CHANGED) {
      return;
    }

    final List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      for (Player player : players) {
        updateScoreboard(player.getUniqueId());
      }
    });
  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType != PermissionEventType.PLAYER_GROUP_CHANGED) {
      return;
    }

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(uuid);
    });
  }

  @Override
  public void onLanguageChange(UUID uuid) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(uuid);
    });
  }
}
