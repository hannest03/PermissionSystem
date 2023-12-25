package it.smallcode.permissionsystem.commands;

import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.SignManager;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.models.adapter.SignLocationAdapter;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionCommand implements CommandExecutor {

  private final Plugin plugin;
  private final PermissionManager permissionManager;
  private final SignManager signManager;

  private final SignLocationAdapter signLocationAdapter = new SignLocationAdapter();

  public PermissionCommand(Plugin plugin, PermissionManager permissionManager,
      SignManager signManager) {
    this.plugin = plugin;
    this.permissionManager = permissionManager;
    this.signManager = signManager;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0) {
      if (sender instanceof Player) {
        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          List<PlayerGroup> groups = permissionManager.getPlayerGroups(player.getUniqueId());

          String groupText = groups.stream().map(group -> {
            String text = group.group().getName();
            if (group.end() != null) {
              text += " (" + group.toTimeLeft() + ")";
            }
            return text;
          }).collect(Collectors.joining(", "));

          //TODO: add translations
          player.sendMessage("Deine Gruppen sind:");
          player.sendMessage(groupText);
        });
      }
    } else {
      String type = args[0].toLowerCase();
      switch (type) {
        case "group" -> handleGroupCommand(sender, args);
        case "player" -> handlePlayerCommand(sender, args);
        case "sign" -> handleSignCommand(sender, args);
      }
    }
    return false;
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
    if (args[1].equalsIgnoreCase("create")) {
      if (args.length != 3) {
        sender.sendMessage("/permission group create <name>");
        return;
      }
      final String groupName = args[2];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionManager.getGroupByName(groupName);
        if (group != null) {
          //TODO: add translation
          sender.sendMessage("Diese Gruppe existiert bereits");
          return;
        }
        permissionManager.createGroup(new Group(groupName, "", 0));

        //TODO: add translation
        sender.sendMessage("Gruppe " + groupName + " erstellt!");
      });
    }
  }

  private void handlePlayerCommand(CommandSender sender, String[] args) {
    if (args.length == 1) {
      sender.sendMessage("/permission player add <name> <group> [<dd:hh:mm:ss>]");
      sender.sendMessage("/permission player remove <name> <group>");
      return;
    }
    if (args[1].equalsIgnoreCase("add")) {
      if (args.length != 4) {
        sender.sendMessage("/permission player add <name> <group> [<dd:hh:mm:ss>]");
        return;
      }
      final String playerName = args[2];
      final String groupName = args[3];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        // TODO: Check if player already has group
        Group group = permissionManager.getGroupByName(groupName);
        if (group == null) {
          //TODO: add translation
          sender.sendMessage("Diese Gruppe existiert nicht!");
          return;
        }

        //TODO: replace with UUID Fetcher
        Player player = Bukkit.getPlayer(playerName);
        permissionManager.addPlayerGroup(player.getUniqueId(), group);

        //TODO: add translation
        sender.sendMessage(playerName + " zu Gruppe " + groupName + " hinzugefügt!");
      });
    } else if (args[1].equalsIgnoreCase("remove")) {
      if (args.length != 4) {
        sender.sendMessage("/permission player remove <name> <group>");
        return;
      }
      final String playerName = args[2];
      final String groupName = args[3];
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        Group group = permissionManager.getGroupByName(groupName);
        if (group == null) {
          //TODO: add translation
          sender.sendMessage("Diese Gruppe existiert nicht!");
          return;
        }

        //TODO: replace with UUID Fetcher
        Player player = Bukkit.getPlayer(playerName);
        permissionManager.removePlayerGroup(player.getUniqueId(), group);

        //TODO: add translation
        sender.sendMessage(playerName + " von Gruppe " + groupName + " entfernt!");
      });
    }
  }

  private void handleSignCommand(CommandSender sender, String[] args) {
    if (!(sender instanceof Player)) {
      //TODO: add translation
      sender.sendMessage("Dieses Kommando ist nur für Spieler!");
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
        player.sendMessage("Du musst auf ein Schild aus Eiche schauen!");
        return;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        signManager.addSignLocation(signLocationAdapter.fromLocation(block.getLocation()));
      });
    } else if (args[1].equalsIgnoreCase("remove")) {
      final Block block = player.getTargetBlockExact(4);
      if (block == null || block.getType() != Material.OAK_SIGN) {
        player.sendMessage("Du musst auf ein Schild aus Eiche schauen!");
        return;
      }
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        signManager.removeSignLocation(signLocationAdapter.fromLocation(block.getLocation()));
      });
    }
  }

}
