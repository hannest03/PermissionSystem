package it.smallcode.permissionsystem.datasource;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionDataSource {

  void createGroup(Group group);

  List<Group> getGroups();

  Group getDefaultGroup();

  List<PlayerGroup> getPlayerGroups(UUID uuid);

  Group getPrimaryGroup(UUID uuid);

  void addPlayerGroup(UUID uuid, Group group, Instant end);

  void removePlayerGroup(UUID uuid, Group group);

  Set<PermissionInfo> getPlayerPermissions(UUID uuid);

}
