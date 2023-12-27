package it.smallcode.permissionsystem.datasource.mysql;

import it.smallcode.permissionsystem.database.MySQLDatabase;
import it.smallcode.permissionsystem.datasource.LanguageDataSource;
import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.datasource.SignDataSource;
import it.smallcode.permissionsystem.datasource.mysql.builder.SQLQueryBuilder;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.AndCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.BaseCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.OrCondition;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.models.SignLocation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MySQLDataSource implements PermissionDataSource, SignDataSource, LanguageDataSource {

  public static final String GROUP_TABLE = "groups";
  public static final String PERMISSIONS_TABLE = "permissions";
  public static final String PLAYER_GROUPS_TABLE = "playergroups";

  public static final String SIGN_TABLE = "signs";

  public static final String PLAYER_LANGUAGE_TABLE = "playerlanguages";

  private final MySQLDatabase database;

  public MySQLDataSource(MySQLDatabase database) {
    this.database = database;
  }

  @Override
  public void createGroup(Group group) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(GROUP_TABLE)
        .field("name")
        .field("prefix")
        .field("priority")
        .field("is_default");

    if (group.getId() != null) {
      queryBuilder.field("id");
    }

    try (PreparedStatement statement = database.getConnection().prepareStatement(
        queryBuilder.insert())) {

      statement.setString(1, group.getName());
      statement.setString(2, group.getPrefix());
      statement.setInt(3, group.getPriority());
      statement.setBoolean(4, group.isDefault());

      if (group.getId() != null) {
        statement.setInt(5, group.getId());
      }

      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public List<Group> getGroups() {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(GROUP_TABLE)
        .field("*");

    try (PreparedStatement statement = database.getConnection().prepareStatement(
        queryBuilder.select())) {
      ResultSet resultSet = statement.executeQuery();

      List<Group> groups = new LinkedList<>();
      while (resultSet.next()) {
        groups.add(convertRowToGroup(resultSet));
      }

      resultSet.close();
      return groups;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public List<PlayerGroup> getPlayerGroups(UUID uuid) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_GROUPS_TABLE)
        .join(GROUP_TABLE,
            new BaseCondition(GROUP_TABLE + ".id = " + PLAYER_GROUPS_TABLE + ".id_group"))
        .field(GROUP_TABLE + ".*")
        .field(PLAYER_GROUPS_TABLE + ".end_date")
        .where(new BaseCondition(PLAYER_GROUPS_TABLE + ".id_player = ?"))
        .order("priority ASC");

    try (PreparedStatement statement = database.getConnection().prepareStatement(
        queryBuilder.select())) {
      statement.setString(1, uuid.toString());

      ResultSet resultSet = statement.executeQuery();

      List<PlayerGroup> groups = new LinkedList<>();
      while (resultSet.next()) {
        Group group = convertRowToGroup(resultSet);
        Timestamp timestamp = resultSet.getTimestamp("end_date");

        Instant end = timestamp != null ? timestamp.toInstant() : null;

        groups.add(new PlayerGroup(group, end));
      }

      resultSet.close();
      return groups;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public Group getPrimaryGroup(UUID uuid) {
    List<PlayerGroup> groups = this.getPlayerGroups(uuid);
    if (groups == null || groups.isEmpty()) {
      return null;
    }
    return groups.get(0).group();
  }

  @Override
  public void addPlayerGroup(UUID uuid, Group group, Instant end) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_GROUPS_TABLE)
        .field("id_player")
        .field("id_group");

    if (end != null) {
      queryBuilder.field("end_date");
    }

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.insert())) {
      statement.setString(1, uuid.toString());
      statement.setInt(2, group.getId());

      if (end != null) {
        statement.setTimestamp(3, Timestamp.from(end));
      }

      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void removePlayerGroup(UUID uuid, Group group) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_GROUPS_TABLE)
        .where(new AndCondition(
            new BaseCondition("id_player = ?"),
            new BaseCondition("id_group = ?")
        ));

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.delete())) {
      statement.setString(1, uuid.toString());
      statement.setInt(2, group.getId());

      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_GROUPS_TABLE)
        .field("permission")
        .field("priority")
        .join(GROUP_TABLE, new BaseCondition("id_group = " + GROUP_TABLE + ".id"))
        .join(PERMISSIONS_TABLE,
            new BaseCondition(GROUP_TABLE + ".id = " + PERMISSIONS_TABLE + ".id_group"))
        .where(new AndCondition(
            new BaseCondition("id_player = ?"),
            new OrCondition(
                new BaseCondition("end_date IS NULL"),
                new BaseCondition("end_date > CURRENT_TIMESTAMP")
            )
        ));

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.select())) {
      statement.setString(1, uuid.toString());

      ResultSet resultSet = statement.executeQuery();

      Set<PermissionInfo> permissions = new HashSet<>();
      while (resultSet.next()) {
        permissions.add(new PermissionInfo(
            resultSet.getString("permission"),
            resultSet.getInt("priority")
        ));
      }
      return permissions;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public Group getDefaultGroup() {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(GROUP_TABLE)
        .field("*")
        .where(new BaseCondition("is_default = TRUE"))
        .limit(1);

    try (PreparedStatement statement = database.getConnection().prepareStatement(
        queryBuilder.select())) {
      ResultSet resultSet = statement.executeQuery();

      if (!resultSet.next()) {
        resultSet.close();
        return null;
      }
      Group group = convertRowToGroup(resultSet);

      resultSet.close();
      return group;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private Group convertRowToGroup(ResultSet resultSet) throws SQLException {
    Group group = new Group();
    group.setId(resultSet.getInt("id"));
    group.setName(resultSet.getString("name"));
    group.setPrefix(resultSet.getString("prefix"));
    group.setPriority(resultSet.getInt("priority"));
    group.setDefault(resultSet.getBoolean("is_default"));

    return group;
  }

  @Override
  public Set<SignLocation> getSignLocations() {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SIGN_TABLE)
        .field("*");

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.select())) {
      ResultSet resultSet = statement.executeQuery();
      Set<SignLocation> locations = new HashSet<>();
      while (resultSet.next()) {
        locations.add(new SignLocation(
            resultSet.getString("world"),
            resultSet.getInt("x"),
            resultSet.getInt("y"),
            resultSet.getInt("z")
        ));
      }
      resultSet.close();
      return locations;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  @Override
  public boolean isSignLocation(SignLocation signLocation) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SIGN_TABLE)
        .field("COUNT(*) AS count")
        .where(
            new AndCondition(
                new BaseCondition("world = ?"),
                new AndCondition(
                    new BaseCondition("x = ?"),
                    new AndCondition(
                        new BaseCondition("y = ?"),
                        new BaseCondition("z = ?")
                    )
                )
            )
        );

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.select())) {
      statement.setString(1, signLocation.world());
      statement.setInt(2, signLocation.x());
      statement.setInt(3, signLocation.y());
      statement.setInt(4, signLocation.z());

      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        return false;
      }
      int count = resultSet.getInt("count");
      resultSet.close();
      return count == 1;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  @Override
  public void addSignLocation(SignLocation signLocation) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SIGN_TABLE)
        .field("world")
        .field("x")
        .field("y")
        .field("z");

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.insert())) {
      statement.setString(1, signLocation.world());
      statement.setInt(2, signLocation.x());
      statement.setInt(3, signLocation.y());
      statement.setInt(4, signLocation.z());

      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void removeSignLocation(SignLocation signLocation) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SIGN_TABLE)
        .where(
            new AndCondition(
                new BaseCondition("world = ?"),
                new AndCondition(
                    new BaseCondition("x = ?"),
                    new AndCondition(
                        new BaseCondition("y = ?"),
                        new BaseCondition("z = ?")
                    )
                )
            )
        );

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.delete())) {
      statement.setString(1, signLocation.world());
      statement.setInt(2, signLocation.x());
      statement.setInt(3, signLocation.y());
      statement.setInt(4, signLocation.z());

      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public String getLanguageCode(UUID uuid) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_LANGUAGE_TABLE)
        .field("language_code")
        .where(new BaseCondition("id_player = ?"))
        .limit(1);

    String languageCode = null;
    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.select())) {
      statement.setString(1, uuid.toString());

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        languageCode = resultSet.getString("language_code");
      }
      resultSet.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return languageCode;
  }

  @Override
  public void setLanguageCode(UUID uuid, String languageCode) {
    SQLQueryBuilder queryBuilder = new SQLQueryBuilder(PLAYER_LANGUAGE_TABLE)
        .field("id_player")
        .field("language_code");

    try (PreparedStatement statement = database.getConnection()
        .prepareStatement(queryBuilder.replace())) {
      statement.setString(1, uuid.toString());
      statement.setString(2, languageCode);
      statement.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
