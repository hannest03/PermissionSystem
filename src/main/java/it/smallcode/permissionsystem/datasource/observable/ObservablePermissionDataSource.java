package it.smallcode.permissionsystem.datasource.observable;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ObservablePermissionDataSource implements PermissionDataSource {

  private final PermissionDataSource dataSource;

  private Set<PermissionEventObserver> observers = new HashSet<>();

  public ObservablePermissionDataSource(PermissionDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void createGroup(Group group) {
    dataSource.createGroup(group);
  }

  @Override
  public void updateGroup(Group group) {
    dataSource.updateGroup(group);

    notify(PermissionEventType.GROUP_CHANGED);
  }

  @Override
  public void deleteGroup(Group group) {
    dataSource.deleteGroup(group);

    notify(PermissionEventType.GROUP_CHANGED);
    notify(PermissionEventType.GROUP_PERMISSION_CHANGED);
  }

  @Override
  public void addPermission(Group group, String permission) {
    dataSource.addPermission(group, permission);
    notify(PermissionEventType.GROUP_PERMISSION_CHANGED);
  }

  @Override
  public void removePermission(Group group, String permission) {
    dataSource.removePermission(group, permission);
    notify(PermissionEventType.GROUP_PERMISSION_CHANGED);
  }

  @Override
  public List<Group> getGroups() {
    return dataSource.getGroups();
  }

  @Override
  public Group getDefaultGroup() {
    return dataSource.getDefaultGroup();
  }

  @Override
  public List<PlayerGroup> getPlayerGroups(UUID uuid) {
    return dataSource.getPlayerGroups(uuid);
  }

  @Override
  public Group getPrimaryGroup(UUID uuid) {
    return dataSource.getPrimaryGroup(uuid);
  }

  @Override
  public void addPlayerGroup(UUID uuid, Group group, Instant end) {
    dataSource.addPlayerGroup(uuid, group, end);

    notify(PermissionEventType.PLAYER_PERMISSION_CHANGED, uuid);
    notify(PermissionEventType.PLAYER_GROUP_CHANGED, uuid);
  }

  @Override
  public void removePlayerGroup(UUID uuid, Group group) {
    dataSource.removePlayerGroup(uuid, group);

    notify(PermissionEventType.PLAYER_PERMISSION_CHANGED, uuid);
    notify(PermissionEventType.PLAYER_GROUP_CHANGED, uuid);
  }

  @Override
  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    return dataSource.getPlayerPermissions(uuid);
  }

  public void subscribe(PermissionEventObserver observer) {
    observers.add(observer);
  }

  public void unsubscribe(PermissionEventObserver observer) {
    observers.remove(observer);
  }

  private void notify(PermissionEventType eventType) {
    for (PermissionEventObserver observer : observers) {
      observer.onEvent(eventType);
    }
  }

  private void notify(PermissionEventType eventType, UUID uuid) {
    for (PermissionEventObserver observer : observers) {
      observer.onEvent(eventType, uuid);
    }
  }
}
