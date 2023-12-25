package it.smallcode.permissionsystem.listeners;

import it.smallcode.permissionsystem.manager.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

  private final ScoreboardManager scoreboardManager;

  public QuitListener(ScoreboardManager scoreboardManager) {
    this.scoreboardManager = scoreboardManager;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onQuit(PlayerQuitEvent e) {
    scoreboardManager.removePlayer(e.getPlayer());
  }
}
