package it.smallcode.permissionsystem.commands.group.permission;

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

public class RemovePermissionSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String GROUP_PERMISSION_CHANGED = "group_permission_changed";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final LanguageService languageService;

  public RemovePermissionSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("remove", "remove <group> <permission>", serviceRegistry,
        "permission.group.permission.remove");

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

    final String groupName = args[0];
    final String permission = args[1];
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getGroupByName(groupName);
      if (group == null) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_DOES_NOT_EXIST)));
        return;
      }
      permissionService.removeGroupPermission(group, permission);

      String message = language.getTranslation(GROUP_PERMISSION_CHANGED)
          .replaceAll("%group%", groupName);
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    });
  }

  @Override
  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 1) {
      return List.of("<group>");
    } else if (args.length == 2) {
      return List.of("<permission>");
    }

    return List.of();
  }

}
