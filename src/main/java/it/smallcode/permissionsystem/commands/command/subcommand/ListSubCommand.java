package it.smallcode.permissionsystem.commands.command.subcommand;

import it.smallcode.permissionsystem.commands.command.SubCommand;
import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ListSubCommand implements SubCommand {

  private final String name;
  private final String permission;
  private final List<SubCommand> subCommands = new LinkedList<>();

  public ListSubCommand(String name) {
    this(name, null);
  }

  public ListSubCommand(String name, String permission) {
    this.name = name;
    this.permission = permission;
  }

  @Override
  public void command(SubCommandSender sender, String[] args) {
    if (args.length == 0) {
      sendHelp(sender);
      return;
    }
    Optional<SubCommand> optSubCommand = subCommands.stream()
        .filter(command -> command.getName().equalsIgnoreCase(args[0])).findFirst();

    if (!optSubCommand.isPresent()) {
      sendHelp(sender);
      return;
    }

    SubCommand subCommand = optSubCommand.get();

    String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
    subCommand.command(sender, newArgs);
  }

  @Override
  public List<String> autoComplete(SubCommandSender sender, String[] args) {
    if (args.length <= 1) {
      String given = args.length == 1 ? args[0] : "";
      return subCommands.stream()
          .filter(subCommand -> subCommand.hasPermission(sender))
          .map(SubCommand::getName)
          .filter(name -> name.toLowerCase().contains(given.toLowerCase()))
          .toList();
    }
    List<String> options = new LinkedList<>();
    String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
    subCommands.stream()
        .filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0]))
        .filter(subCommand -> subCommand.hasPermission(sender))
        .forEach(subCommand -> {
          List<String> suggestions = subCommand.autoComplete(sender, newArgs);
          if (suggestions != null) {
            options.addAll(suggestions);
          }
        });
    return options;
  }

  public void addSubCommand(SubCommand subCommand) {
    subCommands.add(subCommand);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean hasPermission(SubCommandSender sender) {
    if (permission == null) {
      return true;
    }
    return sender.hasPermission(permission);
  }

  @Override
  public List<String> getHelp(SubCommandSender sender) {
    List<String> help = new LinkedList<>();
    subCommands.stream()
        .filter(subCommand -> subCommand.hasPermission(sender))
        .map(subCommand -> subCommand.getHelp(sender))
        .map(list -> list.stream().map(line -> name + " " + line).toList())
        .forEach(help::addAll);
    return help;
  }

  @Override
  public void sendHelp(SubCommandSender sender) {
    for (String s : getHelp(sender)) {
      sender.sendMessage(s);
    }
  }
}
