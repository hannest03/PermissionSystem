package it.smallcode.permissionsystem.commands.command.bukkit;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.subcommand.BaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PermissionBaseSubCommand extends BaseSubCommand {

  private static final String OUTPUT_FORMAT = "output_format";

  private final LanguageService languageService;

  public PermissionBaseSubCommand(String name, String help, ServiceRegistry serviceRegistry) {
    this(name, help, serviceRegistry, null);
  }

  public PermissionBaseSubCommand(String name, String help, ServiceRegistry serviceRegistry,
      String permission) {
    super(name, help, permission);

    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  public void sendHelp(SubCommandSender sender) {
    Language language = getLanguage(sender.sender());

    final String format = language.getTranslation(OUTPUT_FORMAT);
    for (String s : getHelp(sender)) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', format.replaceAll("%output%", s)));
    }
  }

  protected Language getLanguage(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      return languageService.getLanguage(((Player) commandSender).getUniqueId());
    }
    return languageService.getDefaultLanguage();
  }
}
