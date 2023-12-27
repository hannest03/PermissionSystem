package it.smallcode.permissionsystem.commands;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.subcommand.BaseSubCommand;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BaseSubCommandTest {

  @Test
  public void testBaseSubCommandAutoComplete() {
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
        list.add("asd");
        return list;
      }
    };

    Assertions.assertArrayEquals(new String[]{"asd"},
        baseSubCommand.autoComplete(sender, new String[]{}).toArray(String[]::new));
  }

}
