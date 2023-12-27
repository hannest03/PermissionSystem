package it.smallcode.permissionsystem.commands.group;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class DefaultSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String GROUP_ALREADY_DEFAULT = "group_already_default";
  private static final String GROUP_CHANGED_DEFAULT = "group_changed_default";

  private final Plugin plugin;
  private final PermissionService permissionService;

  public DefaultSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("default", "default <group>", serviceRegistry);

    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    if (args.length != 1) {
      sendHelp(sender);
      return;
    }
    final String groupName = args[0];
    Language language = getLanguage(sender.sender());
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getGroupByName(groupName);
      if (group == null) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_DOES_NOT_EXIST)));
        return;
      }

      if (group.isDefault()) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_ALREADY_DEFAULT)));
        return;
      }

      Group oldDefault = permissionService.getDefaultGroup();
      group.setDefault(true);
      oldDefault.setDefault(false);

      permissionService.updateGroup(oldDefault);
      permissionService.updateGroup(group);
      
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
          language.getTranslation(GROUP_CHANGED_DEFAULT)));
    });
  }

  @Override
  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 1) {
      return List.of("<group>");
    }
    return List.of();
  }
}
