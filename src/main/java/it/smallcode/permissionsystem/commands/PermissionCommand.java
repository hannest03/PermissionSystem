package it.smallcode.permissionsystem.commands;

import it.smallcode.permissionsystem.commands.command.SubCommand;
import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.BukkitCommandSender;
import it.smallcode.permissionsystem.commands.group.GroupSubCommand;
import it.smallcode.permissionsystem.commands.language.LanguageSubCommand;
import it.smallcode.permissionsystem.commands.player.PlayerSubCommand;
import it.smallcode.permissionsystem.commands.sign.SignSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionCommand implements CommandExecutor, TabCompleter {

  private static final String ONLY_FOR_PLAYERS = "command_only_for_players";
  private static final String YOUR_GROUPS_ARE = "your_groups_are";
  private static final String OUTPUT_FORMAT = "output_format";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final LanguageService languageService;

  private final List<SubCommand> subCommands = new LinkedList<>();

  public PermissionCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

    subCommands.add(new GroupSubCommand(plugin, serviceRegistry));
    subCommands.add(new LanguageSubCommand(plugin, serviceRegistry));
    subCommands.add(new SignSubCommand(plugin, serviceRegistry));
    subCommands.add(new PlayerSubCommand(plugin, serviceRegistry));
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          List<PlayerGroup> groups = permissionService.getPlayerGroups(player.getUniqueId());

          String groupText = groups.stream().map(group -> {
            String text = group.group().getName();
            if (group.end() != null) {
              text += " (" + group.toTimeLeft() + ")";
            }
            return text;
          }).collect(Collectors.joining(", "));

          Language language = languageService.getLanguage(player.getUniqueId());
          String message = ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(YOUR_GROUPS_ARE));
          player.sendMessage(message);

          final String format = language.getTranslation(OUTPUT_FORMAT);
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              format.replaceAll("%output%", groupText)));
        });
      } else {
        Language language = getLanguage(sender);
        sender.sendMessage(
            ChatColor.translateAlternateColorCodes('&', language.getTranslation(ONLY_FOR_PLAYERS)));
      }
    } else {
      Optional<SubCommand> possibleSubCommand = subCommands.stream()
          .filter(subCommand -> subCommand.hasPermission(new BukkitCommandSender(sender)))
          .filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0]))
          .findFirst();

      if (!possibleSubCommand.isPresent()) {
        sendHelp(sender);
      } else {
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        SubCommand subCommand = possibleSubCommand.get();
        subCommand.command(new BukkitCommandSender(sender), newArgs);
      }
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String s,
      String[] args) {

    if (args.length <= 1) {
      String given = args.length == 1 ? args[0] : "";
      return subCommands.stream()
          .filter(subCommand -> subCommand.hasPermission(new BukkitCommandSender(sender)))
          .map(SubCommand::getName)
          .filter(name -> name.toLowerCase().contains(given.toLowerCase())).toList();
    }
    List<String> options = new LinkedList<>();
    String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
    subCommands.stream()
        .filter(subCommand -> subCommand.hasPermission(new BukkitCommandSender(sender)))
        .filter(subCommand -> subCommand.getName().equalsIgnoreCase(args[0]))
        .forEach(subCommand -> options.addAll(
            subCommand.autoComplete(new BukkitCommandSender(sender), newArgs)
        ));
    return options;
  }

  private void sendHelp(CommandSender sender) {
    List<String> help = new LinkedList<>();
    help.add("");
    SubCommandSender subCommandSender = new BukkitCommandSender(sender);
    subCommands.stream()
        .filter(subCommand -> subCommand.hasPermission(subCommandSender))
        .forEach(subCommand -> help.addAll(subCommand.getHelp(subCommandSender)));

    Language language = getLanguage(sender);

    final String format = language.getTranslation(OUTPUT_FORMAT);
    for (String s : help) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&',
              format.replaceAll("%output%", "/permission " + s)));
    }
  }

  private Language getLanguage(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      return languageService.getLanguage(((Player) commandSender).getUniqueId());
    }
    return languageService.getDefaultLanguage();
  }
}
