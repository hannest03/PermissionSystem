package it.smallcode.permissionsystem.datasource;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionDataSource {

  void createGroup(Group group);

  List<Group> getGroups();

  List<Group> getPlayerGroups(UUID uuid);

  Group getPrimaryGroup(UUID uuid);

  Set<PermissionInfo> getPlayerPermissions(UUID uuid);

  Group getDefaultGroup();
}
