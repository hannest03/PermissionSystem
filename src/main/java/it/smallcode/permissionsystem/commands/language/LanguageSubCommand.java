package it.smallcode.permissionsystem.commands.language;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LanguageSubCommand extends PermissionBaseSubCommand {

  private static final String ONLY_FOR_PLAYERS = "command_only_for_players";

  private final Plugin plugin;

  private final LanguageService languageService;

  public LanguageSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("language", "language [<code>]", serviceRegistry);

    this.plugin = plugin;
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    Language language = getLanguage(sender.sender());
    if (!(sender.sender() instanceof Player player)) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', language.getTranslation(ONLY_FOR_PLAYERS)));
      return;
    }
    if (args.length == 1) {
      String languageCode = args[0].toLowerCase();
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        languageService.setLanguage(player.getUniqueId(), languageCode);
      });
      return;
    }
    sendHelp(sender);
  }

  @Override
  protected List<String> handleAutoComplete(SubCommandSender sender, String[] args) {
    if (args.length == 1) {
      return languageService.getLanguageCodes().stream().toList();
    }
    return List.of();
  }
}
