package it.smallcode.permissionsystem.manager;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.manager.observer.PermissionEventObserver;
import it.smallcode.permissionsystem.manager.observer.PermissionEventType;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PermissionManager {

  private final PermissionDataSource dataSource;

  private Set<PermissionEventObserver> permissionEventObservers = new HashSet<>();

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
    dataSource.addGroup(uuid, group, instant);

    notify(PermissionEventType.PLAYER_PERMISSION_CHANGED, uuid);
    notify(PermissionEventType.PLAYER_GROUP_CHANGED, uuid);
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

  public void subscribe(PermissionEventObserver observer) {
    permissionEventObservers.add(observer);
  }

  public void unsubscribe(PermissionEventObserver observer) {
    permissionEventObservers.remove(observer);
  }

  private void notify(PermissionEventType eventType, UUID uuid) {
    for (PermissionEventObserver observer : permissionEventObservers) {
      observer.onEvent(eventType, uuid);
    }
  }
}
