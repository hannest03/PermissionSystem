package it.smallcode.permissionsystem.handler;

import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.ScoreboardManager;
import it.smallcode.permissionsystem.models.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SidebarHandler implements Listener {

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

      Objective objective = getObjective(scoreboard, "sidebar");
      objective.setDisplayName(title);
      objective.setDisplaySlot(DisplaySlot.SIDEBAR);

      Score score = objective.getScore(groupName);
      score.setScore(0);

      scoreboardManager.updateScoreboard(player);
    }
  }

  private Objective getObjective(Scoreboard scoreboard, String name) {
    Objective objective = scoreboard.getObjective(name);
    if (objective == null) {
      objective = scoreboard.registerNewObjective(name, Criteria.DUMMY, name);
    }
    return objective;
  }

}
