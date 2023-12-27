package it.smallcode.permissionsystem.commands.group;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class PrioritySubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String GROUP_PRIORITY_CHANGED = "group_priority_changed";
  private static final String GROUP_PRIORITY_SHOW = "group_priority_show";
  private static final String INVALID_INPUT = "invalid_input";

  private final Plugin plugin;

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public PrioritySubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("priority", "priority <group> [<priority>]", serviceRegistry,
        "permission.group.priority");

    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    Language language = getLanguage(sender.sender());
    if (args.length == 1) {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionService.getGroupByName(args[0]);
        if (group == null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_DOES_NOT_EXIST)));
          return;
        }
        String message = ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_PRIORITY_SHOW)
                .replaceAll("%priority%", String.valueOf(group.getPriority())));
        sender.sendMessage(message);
      });
    } else if (args.length == 2) {
      final String groupName = args[0];
      final String priority = args[1];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionService.getGroupByName(groupName);
        if (group == null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_DOES_NOT_EXIST)));
          return;
        }

        try {
          int integer = Integer.parseInt(priority);
          group.setPriority(integer);
        } catch (Exception ex) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(INVALID_INPUT)));
          return;
        }

        permissionService.updateGroup(group);

        String message = language.getTranslation(GROUP_PRIORITY_CHANGED)
            .replaceAll("%group%", groupName);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      });
    } else {
      sendHelp(sender);
    }
  }

  @Override
  public List<String> autoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 0 || args.length == 1) {
      return List.of("<group>");
    }
    if (args.length == 2) {
      return List.of("[<priority>]");
    }
    return List.of();
  }
}
