package it.smallcode.permissionsystem.commands.command;

import java.util.List;

public interface SubCommand {

  void command(SubCommandSender sender, String[] args);

  List<String> autoComplete(SubCommandSender sender, String[] args);

  boolean hasPermission(SubCommandSender sender);

  String getName();

  List<String> getHelp(SubCommandSender sender);

  void sendHelp(SubCommandSender sender);
}
