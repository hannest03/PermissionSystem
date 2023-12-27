package it.smallcode.permissionsystem.commands.player.group;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import it.smallcode.permissionsystem.utils.TimeParser;
import it.smallcode.permissionsystem.utils.UUIDFetcher;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class PlayerAddGroupSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String PLAYER_GROUP_ADDED = "player_group_added";
  private static final String PLAYER_HAS_GROUP = "player_has_group";
  private static final String PLAYER_DOES_NOT_EXIST = "player_does_not_exist";
  private static final String INVALID_INPUT = "invalid_input";

  private final Plugin plugin;

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public PlayerAddGroupSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("add", "add <player> <group> [<[d][h][m][s]>]", serviceRegistry,
        "permission.player.group.add");

    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    if (args.length < 2) {
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

      if (permissionService.hasPlayerGroup(uuid, group)) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(PLAYER_HAS_GROUP)));
        return;
      }

      if (args.length == 2) {
        permissionService.addPlayerGroup(uuid, group);
      } else {
        String time = args[2];
        try {
          Duration duration = TimeParser.parseDuration(time);
          Instant instant = Instant.now().plus(duration);

          permissionService.addPlayerGroup(uuid, group, instant);
        } catch (Exception ex) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(INVALID_INPUT)));
          return;
        }
      }

      String message = language.getTranslation(PLAYER_GROUP_ADDED)
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
    } else if (args.length == 3) {
      return List.of("[<[d][h][m][s]>]");
    }
    return List.of();
  }
}
