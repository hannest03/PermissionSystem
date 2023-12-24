package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.database.MySQLDatabase;
import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.datasource.mysql.MySQLDataSource;
import it.smallcode.permissionsystem.handler.PermissibleBaseHandler;
import it.smallcode.permissionsystem.permissions.PermissionManager;
import it.smallcode.permissionsystem.utils.CraftBukkitPermissibleBaseUtils;
import it.smallcode.permissionsystem.utils.PermissibleBaseUtils;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionSystemPlugin extends JavaPlugin {

  private MySQLDatabase database;

  private PermissionManager permissionManager;

  private PermissibleBaseHandler permissibleBaseHandler;

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

    PermissibleBaseUtils permissibleBaseUtils = new CraftBukkitPermissibleBaseUtils();

    permissibleBaseHandler = new PermissibleBaseHandler(this, permissionManager,
        permissibleBaseUtils);
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
