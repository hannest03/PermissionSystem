package it.smallcode.permissionsystem.listeners;

import it.smallcode.permissionsystem.manager.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

  private final ScoreboardManager scoreboardManager;

  public JoinListener(ScoreboardManager scoreboardManager) {
    this.scoreboardManager = scoreboardManager;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(PlayerJoinEvent e) {
    scoreboardManager.initPlayer(e.getPlayer());
  }

}
