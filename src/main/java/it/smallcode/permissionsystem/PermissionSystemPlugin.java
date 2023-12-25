package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.commands.PermissionCommand;
import it.smallcode.permissionsystem.database.MySQLDatabase;
import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.datasource.mysql.MySQLDataSource;
import it.smallcode.permissionsystem.handler.ChatMessageHandler;
import it.smallcode.permissionsystem.handler.JoinMessageHandler;
import it.smallcode.permissionsystem.handler.PermissibleBaseHandler;
import it.smallcode.permissionsystem.handler.SidebarHandler;
import it.smallcode.permissionsystem.listeners.JoinListener;
import it.smallcode.permissionsystem.listeners.QuitListener;
import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.ScoreboardManager;
import it.smallcode.permissionsystem.utils.CraftBukkitPermissibleBaseUtils;
import it.smallcode.permissionsystem.utils.PermissibleBaseUtils;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionSystemPlugin extends JavaPlugin {

  private MySQLDatabase database;

  private PermissionManager permissionManager;

  private PermissibleBaseHandler permissibleBaseHandler;
  private SidebarHandler sidebarHandler;

  @Override
  public void onEnable() {
    database = new MySQLDatabase("localhost", 3306, "permissions", "root",
        () -> "");

    try {
      database.connect();
    } catch (SQLException e) {
      getLogger().severe("Couldn't connect to database!");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    PermissionDataSource permissionDataSource = new MySQLDataSource(database);
    permissionManager = new PermissionManager(permissionDataSource);
    permissionManager.init();

    ScoreboardManager scoreboardManager = new ScoreboardManager();

    PermissibleBaseUtils permissibleBaseUtils = new CraftBukkitPermissibleBaseUtils();

    permissibleBaseHandler = new PermissibleBaseHandler(this, permissionManager,
        permissibleBaseUtils);
    sidebarHandler = new SidebarHandler(this, permissionManager, scoreboardManager);

    new JoinMessageHandler(this, permissionManager);
    new ChatMessageHandler(this, permissionManager);

    Bukkit.getPluginManager().registerEvents(new JoinListener(scoreboardManager), this);
    Bukkit.getPluginManager().registerEvents(new QuitListener(scoreboardManager), this);

    Bukkit.getPluginCommand("permission")
        .setExecutor(new PermissionCommand(this, permissionManager));

    permissionManager.subscribe(permissibleBaseHandler);
    permissionManager.subscribe(sidebarHandler);
  }

  @Override
  public void onDisable() {
    permissionManager.unsubscribe(permissibleBaseHandler);
    permissionManager.unsubscribe(sidebarHandler);

    try {
      database.disconnect();
    } catch (SQLException e) {
      getLogger().warning("Couldn't disconnect from database correctly!");
    }
  }
}
