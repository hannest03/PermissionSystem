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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatMessageHandler implements Listener {

  private static final String CHAT_MESSAGE_FORMAT = "chat_message_format";

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public ChatMessageHandler(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    e.setCancelled(true);

    Player player = e.getPlayer();
    String message = e.getMessage();

    Group group = permissionService.getPrimaryGroup(player.getUniqueId());
    if (group == null) {
      return;
    }

    String playerName = group.getPrefix() + player.getName();
    List<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
    for (Player all : players) {
      Language language = languageService.getLanguage(all.getUniqueId());
      final String messagePattern = language.getTranslation(CHAT_MESSAGE_FORMAT);
      final String toSend = messagePattern.replaceAll("%player%", playerName)
          .replaceAll("%message%", message);
      all.sendMessage(ChatColor.translateAlternateColorCodes('&', toSend));
    }
  }
}
