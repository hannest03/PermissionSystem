package it.smallcode.permissionsystem.commands.group.permission;

import it.smallcode.permissionsystem.commands.command.bukkit.PermissionListSubCommand;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.plugin.Plugin;

public class PermissionSubCommand extends PermissionListSubCommand {

  public PermissionSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("permission", serviceRegistry, "permission.group.permission");
    addSubCommand(new AddPermissionSubCommand(plugin, serviceRegistry));
    addSubCommand(new RemovePermissionSubCommand(plugin, serviceRegistry));
  }
}
