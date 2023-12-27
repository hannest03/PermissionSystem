package it.smallcode.permissionsystem.commands.player.group;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import it.smallcode.permissionsystem.utils.UUIDFetcher;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class PlayerRemoveGroupSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String PLAYER_GROUP_REMOVED = "player_group_removed";
  private static final String PLAYER_DOES_NOT_EXIST = "player_does_not_exist";

  private final Plugin plugin;

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public PlayerRemoveGroupSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("remove", "remove <player> <group>", serviceRegistry, "permission.player.group.remove");

    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    if (args.length != 2) {
      sendHelp(sender);
      return;
    }
    Language language = getLanguage(sender.sender());

    final String playerName = args[0];
    final String groupName = args[1];
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getGroupByName(groupName);
      if (group == null) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_DOES_NOT_EXIST)));
        return;
      }

      UUID uuid = UUIDFetcher.getUUID(playerName);
      if (uuid == null) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(PLAYER_DOES_NOT_EXIST)));
        return;
      }

      permissionService.removePlayerGroup(uuid, group);

      String message = language.getTranslation(PLAYER_GROUP_REMOVED)
          .replaceAll("%player%", playerName)
          .replaceAll("%group%", groupName);

      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    });
  }

  @Override
  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 1) {
      return List.of("<player>");
    } else if (args.length == 2) {
      return List.of("<group>");
    }
    return List.of();
  }
}
