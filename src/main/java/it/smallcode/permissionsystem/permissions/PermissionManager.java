package it.smallcode.permissionsystem.permissions;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
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

  public void createGroup(Group group) {
    if (group == null) {
      return;
    }
    dataSource.createGroup(group);
  }

  public List<Group> getGroups() {
    return dataSource.getGroups();
  }

  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    return dataSource.getPlayerPermissions(uuid);
  }
}
