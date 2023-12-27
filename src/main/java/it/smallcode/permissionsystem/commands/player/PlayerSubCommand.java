package it.smallcode.permissionsystem.commands.player;

import it.smallcode.permissionsystem.commands.command.bukkit.PermissionListSubCommand;
import it.smallcode.permissionsystem.commands.player.group.PlayerGroupSubCommand;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.plugin.Plugin;

public class PlayerSubCommand extends PermissionListSubCommand {

  public PlayerSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("player", serviceRegistry, "permission.player");

    addSubCommand(new PlayerGroupSubCommand(plugin, serviceRegistry));
  }
}
