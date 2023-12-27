package it.smallcode.permissionsystem.commands;

import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.models.adapter.SignLocationAdapter;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.SignService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionCommand implements CommandExecutor {

  private static final String ONLY_FOR_PLAYERS = "command_only_for_players";
  private static final String YOUR_GROUPS_ARE = "your_groups_are";
  private static final String GROUP_EXISTS_ALREADY = "group_exists_already";
  private static final String GROUP_CREATED = "group_created";
  private static final String GROUP_DOES_NOT_EXIST = "group_does_not_exist";
  private static final String PLAYER_GROUP_ADDED = "player_group_added";
  private static final String PLAYER_GROUP_REMOVED = "player_group_removed";
  private static final String HAVE_TO_LOOK_AT_SIGN = "have_to_look_at_sign";

  private final Plugin plugin;
  private final PermissionService permissionService;
  private final SignService signService;
  private final LanguageService languageService;

  private final SignLocationAdapter signLocationAdapter = new SignLocationAdapter();

  public PermissionCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.plugin = plugin;
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.signService = serviceRegistry.getService(SignService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
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
          player.sendMessage(groupText);
        });
      }
    } else {
      String type = args[0].toLowerCase();
      switch (type) {
        case "group" -> handleGroupCommand(sender, args);
        case "player" -> handlePlayerCommand(sender, args);
        case "sign" -> handleSignCommand(sender, args);
        case "language" -> handleLanguageCommand(sender, args);
      }
    }
    return false;
  }

  private void handleLanguageCommand(CommandSender sender, String[] args) {
    if (!(sender instanceof Player)) {
      return;
    }
    final Player player = (Player) sender;
    if (args.length != 2) {
      player.sendMessage("/permission language <code>");
      return;
    }

    String languageCode = args[1].toLowerCase();
    languageService.setLanguage(player.getUniqueId(), languageCode);
  }

  private void handleGroupCommand(CommandSender sender, String[] args) {
    if (args.length == 1) {
      sender.sendMessage("/permission group create <name>");
      sender.sendMessage("/permission group prefix <group> <prefix>");
      sender.sendMessage("/permission group add <group> <permission>");
      sender.sendMessage("/permission group priority <group> <priority>");
      sender.sendMessage("/permission group info <group>");
      return;
    }

    Language language = getLanguage(sender);
    if (args[1].equalsIgnoreCase("create")) {
      if (args.length != 3) {
        sender.sendMessage("/permission group create <name>");
        return;
      }
      final String groupName = args[2];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionService.getGroupByName(groupName);
        if (group != null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_EXISTS_ALREADY)));
          return;
        }
        permissionService.createGroup(new Group(groupName, "", 0));

        String message = language.getTranslation(GROUP_CREATED)
            .replaceAll("%group%", groupName);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      });
    }
  }

  private void handlePlayerCommand(CommandSender sender, String[] args) {
    if (args.length == 1) {
      sender.sendMessage("/permission player add <name> <group> [<dd:hh:mm:ss>]");
      sender.sendMessage("/permission player remove <name> <group>");
      return;
    }

    Language language = getLanguage(sender);
    if (args[1].equalsIgnoreCase("add")) {
      if (args.length != 4) {
        sender.sendMessage("/permission player add <name> <group> [<dd:hh:mm:ss>]");
        return;
      }
      final String playerName = args[2];
      final String groupName = args[3];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        // TODO: Check if player already has group
        Group group = permissionService.getGroupByName(groupName);
        if (group == null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_DOES_NOT_EXIST)));
          return;
        }

        //TODO: replace with UUID Fetcher
        Player player = Bukkit.getPlayer(playerName);
        permissionService.addPlayerGroup(player.getUniqueId(), group);

        String message = language.getTranslation(PLAYER_GROUP_ADDED)
            .replaceAll("%player%", playerName)
            .replaceAll("%group%", groupName);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      });
    } else if (args[1].equalsIgnoreCase("remove")) {
      if (args.length != 4) {
        sender.sendMessage("/permission player remove <name> <group>");
        return;
      }
      final String playerName = args[2];
      final String groupName = args[3];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionService.getGroupByName(groupName);
        if (group == null) {
          sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              language.getTranslation(GROUP_DOES_NOT_EXIST)));
          return;
        }

        //TODO: replace with UUID Fetcher
        Player player = Bukkit.getPlayer(playerName);
        permissionService.removePlayerGroup(player.getUniqueId(), group);

        String message = language.getTranslation(PLAYER_GROUP_REMOVED)
            .replaceAll("%player%", playerName)
            .replaceAll("%group%", groupName);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
      });
    }
  }

  private void handleSignCommand(CommandSender sender, String[] args) {
    Language language = getLanguage(sender);
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', language.getTranslation(ONLY_FOR_PLAYERS)));
      return;
    }
    if (args.length == 1) {
      sender.sendMessage("/permission sign add");
      sender.sendMessage("/permission sign remove");
      return;
    }

    final Player player = (Player) sender;
    if (args[1].equalsIgnoreCase("add")) {
      final Block block = player.getTargetBlockExact(4);
      if (block == null || block.getType() != Material.OAK_SIGN) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(HAVE_TO_LOOK_AT_SIGN)));
        return;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        signService.addSignLocation(signLocationAdapter.fromLocation(block.getLocation()));
      });
    } else if (args[1].equalsIgnoreCase("remove")) {
      final Block block = player.getTargetBlockExact(4);
      if (block == null || block.getType() != Material.OAK_SIGN) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            language.getTranslation(HAVE_TO_LOOK_AT_SIGN)));
        return;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        signService.removeSignLocation(signLocationAdapter.fromLocation(block.getLocation()));
      });
    }
  }

  private Language getLanguage(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      return languageService.getLanguage(((Player) commandSender).getUniqueId());
    }
    return languageService.getDefaultLanguage();
  }

}
