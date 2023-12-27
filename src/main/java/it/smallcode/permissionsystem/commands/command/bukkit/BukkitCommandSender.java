package it.smallcode.permissionsystem.commands.command.bukkit;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import org.bukkit.command.CommandSender;

public record BukkitCommandSender(CommandSender sender) implements SubCommandSender {

  @Override
  public boolean hasPermission(String permission) {
    return sender.hasPermission(permission);
  }

  @Override
  public void sendMessage(String message) {
    sender.sendMessage(message);
  }
}
