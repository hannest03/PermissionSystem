package it.smallcode.permissionsystem.listeners;

import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class QuitListener implements Listener {

  private final LanguageService languageService;

  public QuitListener(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.languageService = serviceRegistry.getService(LanguageService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    languageService.unloadPlayer(e.getPlayer().getUniqueId());
  }
}
