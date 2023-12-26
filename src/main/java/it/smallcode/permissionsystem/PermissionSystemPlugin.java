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
import it.smallcode.permissionsystem.handler.TablistHandler;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.ServiceRegistry;
import it.smallcode.permissionsystem.services.SignService;
import it.smallcode.permissionsystem.services.impl.ImplPermissionService;
import it.smallcode.permissionsystem.services.impl.ImplSignService;
import it.smallcode.permissionsystem.utils.CraftBukkitPermissibleBaseUtils;
import it.smallcode.permissionsystem.utils.PermissibleBaseUtils;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionSystemPlugin extends JavaPlugin {

  private MySQLDatabase database;

  private ServiceRegistry serviceRegistry;

  @Override
  public void onEnable() {
    database = new MySQLDatabase("localhost", 3306, "permissions", "root",
        () -> "");

    serviceRegistry = new ServiceRegistry();

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

    PermissionService permissionService = new ImplPermissionService(observableDataSource);
    permissionService.init();

    serviceRegistry.registerService(PermissionService.class, permissionService);

    ObservableSignDataSource observableSignDataSource = new ObservableSignDataSource(
        new CacheSignDataSource(dataSource));
    SignService signService = new ImplSignService(observableSignDataSource);

    serviceRegistry.registerService(SignService.class, signService);

    PermissibleBaseUtils permissibleBaseUtils = new CraftBukkitPermissibleBaseUtils();

    serviceRegistry.registerService(PermissibleBaseUtils.class, permissibleBaseUtils);

    PermissibleBaseHandler permissibleBaseHandler = new PermissibleBaseHandler(
        this,
        serviceRegistry);
    SidebarHandler sidebarHandler = new SidebarHandler(this, serviceRegistry);
    SignHandler signHandler = new SignHandler(this, serviceRegistry);
    TablistHandler tablistHandler = new TablistHandler(this, serviceRegistry);

    new JoinMessageHandler(this, serviceRegistry);
    new ChatMessageHandler(this, serviceRegistry);

    Bukkit.getPluginCommand("permission")
        .setExecutor(new PermissionCommand(this, serviceRegistry));

    observableDataSource.subscribe(permissibleBaseHandler);
    observableDataSource.subscribe(sidebarHandler);
    observableDataSource.subscribe(signHandler);
    observableDataSource.subscribe(tablistHandler);
    
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
