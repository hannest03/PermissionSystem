package it.smallcode.permissionsystem.commands.sign;

import it.smallcode.permissionsystem.commands.command.SubCommandSender;
import it.smallcode.permissionsystem.commands.command.bukkit.PermissionBaseSubCommand;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.models.adapter.SignLocationAdapter;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.SignService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AddSignSubCommand extends PermissionBaseSubCommand {

  private static final String ONLY_FOR_PLAYERS = "command_only_for_players";
  private static final String HAVE_TO_LOOK_AT_SIGN = "have_to_look_at_sign";

  private final Plugin plugin;
  private final SignService signService;
  private final LanguageService languageService;

  private final SignLocationAdapter signLocationAdapter = new SignLocationAdapter();

  public AddSignSubCommand(Plugin plugin, ServiceRegistry serviceRegistry) {
    super("add", "add", serviceRegistry, "permission.sign.add");

    this.plugin = plugin;
    this.signService = serviceRegistry.getService(SignService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);
  }

  @Override
  protected void handleCommand(SubCommandSender sender, String[] args) {
    Language language = getLanguage(sender.sender());
    if (!(sender.sender() instanceof Player)) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', language.getTranslation(ONLY_FOR_PLAYERS)));
      return;
    }
    final Player player = (Player) sender.sender();
    final Block block = player.getTargetBlockExact(4);
    if (block == null || block.getType() != Material.OAK_SIGN) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          language.getTranslation(HAVE_TO_LOOK_AT_SIGN)));
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      signService.addSignLocation(signLocationAdapter.fromLocation(block.getLocation()));
    });
  }
}
