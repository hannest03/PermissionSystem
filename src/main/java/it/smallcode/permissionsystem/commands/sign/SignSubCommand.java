package it.smallcode.permissionsystem.commands.sign;

import it.smallcode.permissionsystem.commands.command.bukkit.PermissionListSubCommand;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.plugin.Plugin;

public class SignSubCommand extends PermissionListSubCommand {

  public SignSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("sign", serviceRegistry, "permission.sign");

    addSubCommand(new AddSignSubCommand(plugin, serviceRegistry));
    addSubCommand(new RemoveSignSubCommand(plugin, serviceRegistry));
  }
}
