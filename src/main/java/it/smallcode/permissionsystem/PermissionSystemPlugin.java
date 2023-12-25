package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.commands.PermissionCommand;
import it.smallcode.permissionsystem.database.MySQLDatabase;
import it.smallcode.permissionsystem.datasource.cache.CacheSignDataSource;
import it.smallcode.permissionsystem.datasource.mysql.MySQLDataSource;
import it.smallcode.permissionsystem.datasource.observable.ObservablePermissionDataSource;
import it.smallcode.permissionsystem.datasource.observable.ObservableSignDataSource;
import it.smallcode.permissionsystem.handler.ChatMessageHandler;
import it.smallcode.permissionsystem.handler.JoinMessageHandler;
import it.smallcode.permissionsystem.handler.PermissibleBaseHandler;
import it.smallcode.permissionsystem.handler.SidebarHandler;
import it.smallcode.permissionsystem.handler.SignHandler;
import it.smallcode.permissionsystem.manager.PermissionManager;
import it.smallcode.permissionsystem.manager.SignManager;
import it.smallcode.permissionsystem.utils.CraftBukkitPermissibleBaseUtils;
import it.smallcode.permissionsystem.utils.PermissibleBaseUtils;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionSystemPlugin extends JavaPlugin {

  private MySQLDatabase database;

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

    MySQLDataSource dataSource = new MySQLDataSource(database);
    ObservablePermissionDataSource observableDataSource = new ObservablePermissionDataSource(
        dataSource);

    PermissionManager permissionManager = new PermissionManager(observableDataSource);
    permissionManager.init();

    ObservableSignDataSource observableSignDataSource = new ObservableSignDataSource(
        new CacheSignDataSource(dataSource));
    SignManager signManager = new SignManager(observableSignDataSource);

    PermissibleBaseUtils permissibleBaseUtils = new CraftBukkitPermissibleBaseUtils();

    PermissibleBaseHandler permissibleBaseHandler = new PermissibleBaseHandler(this,
        permissionManager,
        permissibleBaseUtils);
    SidebarHandler sidebarHandler = new SidebarHandler(this, permissionManager);
    SignHandler signHandler = new SignHandler(this, permissionManager, signManager);

    new JoinMessageHandler(this, permissionManager);
    new ChatMessageHandler(this, permissionManager);

    Bukkit.getPluginCommand("permission")
        .setExecutor(new PermissionCommand(this, permissionManager, signManager));

    observableDataSource.subscribe(permissibleBaseHandler);
    observableDataSource.subscribe(sidebarHandler);
    observableDataSource.subscribe(signHandler);

    observableSignDataSource.subscribe(signHandler);
  }

  @Override
  public void onDisable() {
    try {
      database.disconnect();
    } catch (SQLException e) {
      getLogger().warning("Couldn't disconnect from database correctly!");
    }
  }
}
