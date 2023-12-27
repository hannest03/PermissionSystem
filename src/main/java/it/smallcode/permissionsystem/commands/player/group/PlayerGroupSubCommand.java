package it.smallcode.permissionsystem.commands.player.group;

import it.smallcode.permissionsystem.commands.command.subcommand.ListSubCommand;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.plugin.Plugin;

public class PlayerGroupSubCommand extends ListSubCommand {

  public PlayerGroupSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("group", "permission.player.group");

    addSubCommand(new PlayerAddGroupSubCommand(plugin, serviceRegistry));
    addSubCommand(new PlayerRemoveGroupSubCommand(plugin, serviceRegistry));
  }
}
