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

public class CreateSubCommand extends PermissionBaseSubCommand {

  private static final String GROUP_EXISTS_ALREADY = "group_exists_already";
  private static final String GROUP_CREATED = "group_created";

  private final Plugin plugin;

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public CreateSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("create", "create <group>", serviceRegistry, "permission.group.create");

    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    if (args.length == 0) {
      sendHelp(sender);
      return;
    }
    Language language = getLanguage(sender.sender());
    final String groupName = args[0];
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Group group = permissionService.getGroupByName(groupName);
      if (group != null) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(GROUP_EXISTS_ALREADY)));
        return;
      }
      permissionService.createGroup(new Group(groupName, "", 0));

      String message = language.getTranslation(GROUP_CREATED)
          .replaceAll("%group%", groupName);
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    });
  }

  @Override
  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 0 || args.length == 1) {
      return List.of("<name>");
    }
    return List.of();
  }
}
