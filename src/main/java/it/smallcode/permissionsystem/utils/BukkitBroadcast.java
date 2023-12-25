package it.smallcode.permissionsystem.utils;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitBroadcast {

  /**
   * This method is a thread-safe variant to broadcast a message to all online players
   *
   * @param message Message to broadcast to every player
   */
  public static void broadcast(String message) {
    // Copy online players list so that list doesn't change while iterating
    List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    for (Player p : players) {
      p.sendMessage(message);
    }
  }

}
