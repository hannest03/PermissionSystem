package it.smallcode.permissionsystem.handler;

import com.google.common.collect.ImmutableList;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventObserver;
import it.smallcode.permissionsystem.datasource.observable.PermissionEventType;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class TablistHandler implements Listener, PermissionEventObserver {

  private final Plugin plugin;

  private final PermissionService permissionService;

  private final Scoreboard scoreboard;
  private final Map<Integer, String> teamNames = new HashMap<>();

  public TablistHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);

    scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective objective = scoreboard.registerNewObjective("tablist", Criteria.DUMMY, "tablist");
    objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();
    player.setScoreboard(scoreboard);

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getPrimaryGroup(player.getUniqueId());

      String teamName = getTeamName(group);
      Team team = getOrCreateTeam(teamName);
      teamNames.put(group.getId(), teamName);

      updateTeam(team, group);

      team.addEntry(player.getName());
    });
  }

  @Override
  public void onEvent(PermissionEventType eventType) {
    if (eventType != PermissionEventType.GROUP_CHANGED) {
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      for (Team team : scoreboard.getTeams()) {
        team.unregister();
      }

      List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
      for (Player player : players) {
        Group group = permissionService.getPrimaryGroup(player.getUniqueId());

        String teamName = getTeamName(group);
        Team team = getOrCreateTeam(teamName);
        teamNames.put(group.getId(), teamName);

        updateTeam(team, group);

        team.addEntry(player.getName());
      }
    });
  }

  @Override
  public void onEvent(PermissionEventType eventType, UUID uuid) {
    if (eventType != PermissionEventType.PLAYER_GROUP_CHANGED) {
      return;
    }

    final Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return;
    }

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getPrimaryGroup(uuid);

      String teamName = getTeamName(group);
      Team team = getOrCreateTeam(teamName);
      if (!teamNames.containsKey(group.getId())) {
        updateTeam(team, group);
      }

      team.addEntry(player.getName());
    });
  }

  private void updateTeam(Team team, Group group) {
    String colors = ChatColor.translateAlternateColorCodes('&', group.getPrefix());
    team.setPrefix(colors);

    String lastColor = ChatColor.getLastColors(colors).replace("§", "");
    if (!lastColor.isEmpty()) {
      ChatColor chatColor = ChatColor.getByChar(lastColor);
      if (chatColor != null) {
        team.setColor(chatColor);
      }
    }
  }

  private Team getOrCreateTeam(String teamName) {
    Team team = scoreboard.getTeam(teamName);
    if (team == null) {
      team = scoreboard.registerNewTeam(teamName);
    }
    return team;
  }

  private String getTeamName(Group group) {
    return String.format("%04d-%d", group.getPriority(), group.getId());
  }
}
