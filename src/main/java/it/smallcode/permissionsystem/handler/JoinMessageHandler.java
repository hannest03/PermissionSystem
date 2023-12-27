package it.smallcode.permissionsystem.handler;

import com.google.common.collect.ImmutableList;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinMessageHandler implements Listener {

  private static final String JOIN_MESSAGE_FORMAT = "join_message_format";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final LanguageService languageService;

  public JoinMessageHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player player = e.getPlayer();

    e.setJoinMessage(null);
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getPrimaryGroup(player.getUniqueId());
      if (group == null) {
        return;
      }

      List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
      for (Player all : players) {
        Language language = languageService.getLanguage(all.getUniqueId());
        String joinMessage = language.getTranslation(JOIN_MESSAGE_FORMAT);
        final String message = joinMessage.replaceAll("%player%",
            group.getPrefix() + player.getName());
        all.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      }
    });
  }

}
