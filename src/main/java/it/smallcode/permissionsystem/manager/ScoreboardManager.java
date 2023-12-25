package it.smallcode.permissionsystem.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

  private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();

  public Scoreboard getScoreboard(Player player) {
    if (!hasScoreboard(player)) {
      return null;
    }
    return scoreboards.get(player.getUniqueId());
  }

  public void updateScoreboard(Player player) {
    player.setScoreboard(scoreboards.get(player.getUniqueId()));
  }

  public synchronized void initPlayer(Player player) {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    scoreboards.put(player.getUniqueId(), scoreboard);
    updateScoreboard(player);
  }

  public synchronized void removePlayer(Player player) {
    scoreboards.remove(player.getUniqueId());
  }

  public boolean hasScoreboard(Player player) {
    return scoreboards.containsKey(player.getUniqueId());
  }
}
