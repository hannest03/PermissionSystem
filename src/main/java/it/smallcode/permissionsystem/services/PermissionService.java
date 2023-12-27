package it.smallcode.permissionsystem.services;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService extends Service {

  void init();

  Group getPrimaryGroup(UUID uuid);

  List<PlayerGroup> getPlayerGroups(UUID uuid);

  void addPlayerGroup(UUID uuid, Group group);

  void addPlayerGroup(UUID uuid, Group group, Instant instant);

  void removePlayerGroup(UUID uuid, Group group);

  void createGroup(Group group);

  Group getGroupByName(String name);

  Group getDefaultGroup();

  List<Group> getGroups();

  Set<PermissionInfo> getPlayerPermissions(UUID uuid);
}
