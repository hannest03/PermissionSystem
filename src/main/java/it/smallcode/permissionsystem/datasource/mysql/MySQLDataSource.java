package it.smallcode.permissionsystem.datasource.mysql;

import it.smallcode.permissionsystem.database.MySQLDatabase;
import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.datasource.mysql.builder.SQLQueryBuilder;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.AndCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.BaseCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.OrCondition;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
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

public class MySQLDataSource implements PermissionDataSource {

  public static final String GROUP_TABLE = "groups";
  public static final String PERMISSIONS_TABLE = "permissions";
  public static final String PLAYER_GROUPS_TABLE = "playergroups";

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
        .order("priority DESC");

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
}
