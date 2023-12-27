package it.smallcode.permissionsystem.commands.group;

import it.smallcode.permissionsystem.commands.command.bukkit.PermissionListSubCommand;
import it.smallcode.permissionsystem.commands.group.permission.PermissionSubCommand;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.plugin.Plugin;

public class GroupSubCommand extends PermissionListSubCommand {

  public GroupSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("group", serviceRegistry, "permission.group");
    addSubCommand(new CreateSubCommand(plugin, serviceRegistry));
    addSubCommand(new PrefixSubCommand(plugin, serviceRegistry));
    addSubCommand(new PermissionSubCommand(plugin, serviceRegistry));
    addSubCommand(new PrioritySubCommand(plugin, serviceRegistry));
    addSubCommand(new DefaultSubCommand(plugin, serviceRegistry));
    addSubCommand(new InfoSubCommand(plugin, serviceRegistry));
  }
}
