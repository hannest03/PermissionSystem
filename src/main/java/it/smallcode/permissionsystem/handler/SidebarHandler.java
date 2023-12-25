package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.ScoreboardManager;
import it.smallcode.permissionsystem.manager.observer.PermissionEventObserver;
import it.smallcode.permissionsystem.manager.observer.PermissionEventType;
import it.smallcode.permissionsystem.models.Group;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class SidebarHandler implements Listener, PermissionEventObserver {

  private final Plugin plugin;
  private final PermissionManager permissionManager;
  private final ScoreboardManager scoreboardManager;

  public SidebarHandler(Plugin plugin, PermissionManager permissionManager,
      ScoreboardManager scoreboardManager) {
    this.plugin = plugin;
    this.permissionManager = permissionManager;
    this.scoreboardManager = scoreboardManager;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(player);
    });
  }

  private void updateScoreboard(Player player) {
    Group group = permissionManager.getPrimaryGroup(player.getUniqueId());
    String groupName = group != null ? group.getName() : "Undefined";

    //TODO: add translation
    final String title = "Permission System";

    synchronized (scoreboardManager) {
      Scoreboard scoreboard = scoreboardManager.getScoreboard(player);

      Objective sidebar = getObjective(scoreboard, "sidebar");
      sidebar.setDisplayName(title);
      sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

      Team team = getOrCreateTeam(scoreboard, "sidebar_group");
      team.setPrefix(groupName);
      team.addEntry("§c");

      sidebar.getScore("§c").setScore(0);

      scoreboardManager.updateScoreboard(player);
    }
  }

  private Objective getObjective(Scoreboard scoreboard, String name) {
    Objective objective = scoreboard.getObjective(name);
    if (objective != null) {
      objective.unregister();
    }
    return scoreboard.registerNewObjective(name, Criteria.DUMMY, name);
  }

  private Team getOrCreateTeam(Scoreboard scoreboard, String name) {
    Team team = scoreboard.getTeam(name);
    if (team == null) {
      team = scoreboard.registerNewTeam(name);
    }
    return team;
  }

  @Override
  public void onEvent(PermissionEventType eventType) {

  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType != PermissionEventType.PLAYER_GROUP_CHANGED
        && eventType != PermissionEventType.GROUP_CHANGED) {
      return;
    }

    final Player player = Bukkit.getPlayer(uuid);
    if (player == null || !player.isOnline()) {
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      updateScoreboard(player);
    });
  }
}
