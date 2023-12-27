package it.smallcode.permissionsystem.services;

import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.services.registry.Service;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionService extends Service {

  void init();

  void updateGroup(Group group);

  void addGroupPermission(Group group, String permission);

  void removeGroupPermission(Group group, String permission);

  Group getPrimaryGroup(UUID uuid);

  List<PlayerGroup> getPlayerGroups(UUID uuid);

  void addPlayerGroup(UUID uuid, Group group);

  void addPlayerGroup(UUID uuid, Group group, Instant instant);

  boolean hasPlayerGroup(UUID uuid, Group group);

  void removePlayerGroup(UUID uuid, Group group);

  void createGroup(Group group);

  void deleteGroup(Group group);

  Group getGroupByName(String name);

  Group getDefaultGroup();

  List<Group> getGroups();

  Set<PermissionInfo> getPlayerPermissions(UUID uuid);
}
