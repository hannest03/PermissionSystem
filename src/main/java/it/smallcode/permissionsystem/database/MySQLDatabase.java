package it.smallcode.permissionsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class MySQLDatabase {

  private final String hostname;
  private final int port;

  private final String database;

  private final String user;
  private final Supplier<String> password;

  private Connection connection;

  public MySQLDatabase(String hostname, int port, String database, String user,
      Supplier<String> password) {
    this.hostname = hostname;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
  }

  public void connect() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      return;
    }
    String connectionUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
    connection = DriverManager.getConnection(connectionUrl, user, password.get());
  }

  public void disconnect() throws SQLException {
    if (connection == null || connection.isClosed()) {
      return;
    }
    connection.close();
  }

  public Connection getConnection() {
    return connection;
  }
}
