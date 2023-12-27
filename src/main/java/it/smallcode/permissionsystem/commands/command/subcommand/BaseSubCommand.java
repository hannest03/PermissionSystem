package it.smallcode.permissionsystem.commands.command.subcommand;

import it.smallcode.permissionsystem.commands.command.SubCommand;
import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseSubCommand implements SubCommand {

  private final String name;
  private final String permission;
  private final String help;

  public BaseSubCommand(String name, String help) {
    this(name, help, null);
  }

  public BaseSubCommand(String name, String help, String permission) {
    this.name = name;
    this.help = help;
    this.permission = permission;
  }

  @Override
  public void command(SubCommandSender sender, String[] args) {
    if (permission != null && !sender.hasPermission(permission)) {
      return;
    }
    handleCommand(sender, args);
  }

  protected abstract void handleCommand(SubCommandSender sender, String[] args);

  @Override
  public List<String> autoComplete(SubCommandSender sender, String[] args) {
    if (permission != null && !sender.hasPermission(permission)) {
      return new LinkedList<>();
    }
    return handleAutoComplete(sender, args);
  }

  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    return new LinkedList<>();
  }

  @Override
  public boolean hasPermission(SubCommandSender sender) {
    if (permission == null) {
      return true;
    }
    return sender.hasPermission(permission);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<String> getHelp(SubCommandSender sender) {
    return List.of(help);
  }

  @Override
  public void sendHelp(SubCommandSender sender) {
    for (String s : getHelp(sender)) {
      sender.sendMessage(s);
    }
  }
}
