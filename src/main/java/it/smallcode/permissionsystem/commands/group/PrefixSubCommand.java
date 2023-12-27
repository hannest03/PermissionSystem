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

public class PrefixSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String GROUP_PREFIX_CHANGED = "group_prefix_changed";
  private static final String GROUP_PREFIX_SHOW = "group_prefix_show";

  private final Plugin plugin;

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public PrefixSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("prefix", "prefix <group> [<prefix>]", serviceRegistry, "permission.group.prefix");

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
            language.getTranslation(GROUP_PREFIX_SHOW)
                .replaceAll("%prefix%", group.getPrefix()));
        sender.sendMessage(message);
      });
    } else if (args.length == 2) {
      final String groupName = args[0];
      final String prefix = args[1];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionService.getGroupByName(groupName);
        if (group == null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_DOES_NOT_EXIST)));
          return;
        }
        group.setPrefix(prefix);

        permissionService.updateGroup(group);

        String message = language.getTranslation(GROUP_PREFIX_CHANGED)
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
      return List.of("[<prefix>]");
    }
    return List.of();
  }
}
