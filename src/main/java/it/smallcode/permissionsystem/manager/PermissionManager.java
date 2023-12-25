package it.smallcode.permissionsystem.manager;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PermissionManager {

  private final PermissionDataSource dataSource;

  public PermissionManager(PermissionDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void init() {
    if (dataSource.getDefaultGroup() == null) {
      Group group = new Group("Default", "&8", 0);
      group.setDefault(true);
      dataSource.createGroup(group);
    }
  }

  public Group getPrimaryGroup(UUID uuid) {
    return dataSource.getPrimaryGroup(uuid);
  }

  public List<PlayerGroup> getPlayerGroups(UUID uuid) {
    return dataSource.getPlayerGroups(uuid);
  }

  public void addPlayerGroup(UUID uuid, Group group) {
    addPlayerGroup(uuid, group, null);
  }

  public void addPlayerGroup(UUID uuid, Group group, Instant instant) {
    dataSource.addPlayerGroup(uuid, group, instant);
  }

  public void removePlayerGroup(UUID uuid, Group group) {
    dataSource.removePlayerGroup(uuid, group);
  }

  public void createGroup(Group group) {
    if (group == null) {
      return;
    }
    dataSource.createGroup(group);
  }

  public Group getGroupByName(String name) {
    for (Group group : getGroups()) {
      if (group.getName().equalsIgnoreCase(name)) {
        return group;
      }
    }
    return null;
  }

  public List<Group> getGroups() {
    return dataSource.getGroups();
  }

  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    return dataSource.getPlayerPermissions(uuid);
  }
}
