package it.smallcode.permissionsystem.listeners;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.services.LanguageService;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.ServiceRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

public class AsyncPreLoginListener implements Listener {

  private final PermissionService permissionService;
  private final LanguageService languageService;

  public AsyncPreLoginListener(Plugin plugin, ServiceRegistry serviceRegistry) {
    this.permissionService = serviceRegistry.getService(PermissionService.class);
    this.languageService = serviceRegistry.getService(LanguageService.class);

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  /*
    This gets executed asynchronously, but the server still waits for this event to finish before accepting the login.
    That means the following code definitely gets executed before the player joins and by that is safe.

    A potential problem could be that the database query takes to long which causes the player to timeout.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
    Group group = permissionService.getPrimaryGroup(e.getUniqueId());
    if (group == null) {
      Group defaultGroup = permissionService.getDefaultGroup();
      if (defaultGroup != null) {
        permissionService.addPlayerGroup(e.getUniqueId(), defaultGroup);
      }
    }

    languageService.loadPlayer(e.getUniqueId());
  }

}
