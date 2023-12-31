package it.smallcode.permissionsystem.services.impl;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.registry.Service;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ImplPermissionService implements Service, PermissionService {

  private final PermissionDataSource dataSource;

  public ImplPermissionService(PermissionDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void init() {
    if (dataSource.getDefaultGroup() == null) {
      Group group = new Group("Default", "&8", 1000);
      group.setDefault(true);
      dataSource.createGroup(group);
    }
  }

  @Override
  public void updateGroup(Group group) {
    dataSource.updateGroup(group);
  }

  @Override
  public void addGroupPermission(Group group, String permission) {
    dataSource.addPermission(group, permission);
  }

  @Override
  public void removeGroupPermission(Group group, String permission) {
    dataSource.removePermission(group, permission);
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

  @Override
  public boolean hasPlayerGroup(UUID uuid, Group group) {
    List<PlayerGroup> groups = getPlayerGroups(uuid);
    for (PlayerGroup playerGroup : groups) {
      if (playerGroup.group().equals(group)) {
        return true;
      }
    }
    return false;
  }

  public void removePlayerGroup(UUID uuid, Group group) {
    dataSource.removePlayerGroup(uuid, group);
    List<PlayerGroup> groups = getPlayerGroups(uuid);
    if (groups == null || groups.isEmpty()) {
      Group defaultGroup = getDefaultGroup();
      if (defaultGroup == null) {
        return;
      }
      addPlayerGroup(uuid, defaultGroup);
    }
  }

  public void createGroup(Group group) {
    if (group == null) {
      return;
    }
    dataSource.createGroup(group);
  }

  @Override
  public void deleteGroup(Group group) {
    dataSource.deleteGroup(group);
  }

  public Group getGroupByName(String name) {
    for (Group group : getGroups()) {
      if (group.getName().equalsIgnoreCase(name)) {
        return group;
      }
    }
    return null;
  }

  public Group getDefaultGroup() {
    return dataSource.getDefaultGroup();
  }

  public List<Group> getGroups() {
    return dataSource.getGroups();
  }

  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    return dataSource.getPlayerPermissions(uuid);
  }
}
