package it.smallcode.permissionsystem.commands;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.subcommand.BaseSubCommand;
import it.smallcode.permissionsystem.commands.command.subcommand.ListSubCommand;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListSubCommandTest {

  @Test
  public void testSubCommandAutoComplete() {
    SubCommandSender sender = new SubCommandSender() {
      @Override
      public boolean hasPermission(String permission) {
        return true;
      }

      @Override
      public void sendMessage(String message) {
      }

      @Override
      public CommandSender sender() {
        return null;
      }
    };

    BaseSubCommand baseSubCommand = new BaseSubCommand("handle", "") {
      @Override
      protected void handleCommand(SubCommandSender sender, String[] args) {
      }

      @Override
      protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
        List<String> list = new LinkedList<>();
        list.add("handle");
        return list;
      }
    };

    ListSubCommand listSubCommand = new ListSubCommand("test");
    listSubCommand.addSubCommand(baseSubCommand);

    Assertions.assertArrayEquals(new String[]{"handle"},
        listSubCommand.autoComplete(sender, new String[]{}).toArray(String[]::new));

    Assertions.assertArrayEquals(new String[]{"handle"},
        listSubCommand.autoComplete(sender, new String[]{"han"}).toArray(String[]::new));
    Assertions.assertArrayEquals(new String[]{"handle"},
        listSubCommand.autoComplete(sender, new String[]{""}).toArray(String[]::new));

    listSubCommand.addSubCommand(new BaseSubCommand("handle2", "") {
      @Override
      protected void handleCommand(SubCommandSender sender, String[] args) {

      }
    });

    Assertions.assertArrayEquals(new String[]{"handle", "handle2"},
        listSubCommand.autoComplete(sender, new String[]{"handle"}).toArray(String[]::new));

    Assertions.assertArrayEquals(new String[]{"handle2"},
        listSubCommand.autoComplete(sender, new String[]{"handle2"})
            .toArray(String[]::new));

    ListSubCommand secondListSubCommand = new ListSubCommand("test2");
    secondListSubCommand.addSubCommand(listSubCommand);

    Assertions.assertArrayEquals(new String[]{"test"},
        secondListSubCommand.autoComplete(sender, new String[]{""}).toArray(String[]::new));

    Assertions.assertArrayEquals(new String[]{"test"},
        secondListSubCommand.autoComplete(sender, new String[]{"tes"}).toArray(String[]::new));

    Assertions.assertArrayEquals(new String[]{"handle", "handle2"},
        secondListSubCommand.autoComplete(sender, new String[]{"test", ""}).toArray(String[]::new));

    Assertions.assertArrayEquals(new String[]{"handle", "handle2"},
        secondListSubCommand.autoComplete(sender, new String[]{"test", "handle"})
            .toArray(String[]::new));
  }

  @Test
  public void testSubCommandGetHelp() {
    SubCommandSender sender = new SubCommandSender() {
      @Override
      public boolean hasPermission(String permission) {
        return true;
      }

      @Override
      public void sendMessage(String message) {
      }

      @Override
      public CommandSender sender() {
        return null;
      }
    };

    BaseSubCommand baseSubCommand = new BaseSubCommand("handle", "test") {
      @Override
      protected void handleCommand(SubCommandSender sender, String[] args) {
      }
    };

    ListSubCommand listSubCommand = new ListSubCommand("test");
    listSubCommand.addSubCommand(baseSubCommand);

    Assertions.assertArrayEquals(new String[]{"test test"},
        listSubCommand.getHelp(sender).toArray(String[]::new));

    listSubCommand.addSubCommand(new BaseSubCommand("handle2", "test2") {
      @Override
      protected void handleCommand(SubCommandSender sender, String[] args) {

      }
    });

    Assertions.assertArrayEquals(new String[]{"test test", "test test2"},
        listSubCommand.getHelp(sender).toArray(String[]::new));
  }
}
