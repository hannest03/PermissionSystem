package it.smallcode.permissionsystem.commands.command;

import org.bukkit.command.CommandSender;

public interface SubCommandSender {

  boolean hasPermission(String permission);

  void sendMessage(String message);

  CommandSender sender();
}
