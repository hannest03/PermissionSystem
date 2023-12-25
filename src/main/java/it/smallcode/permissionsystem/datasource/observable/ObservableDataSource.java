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

public class ObservableDataSource implements PermissionDataSource {

  private final PermissionDataSource dataSource;

  private Set<PermissionEventObserver> observers = new HashSet<>();

  public ObservableDataSource(PermissionDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void createGroup(Group group) {
    dataSource.createGroup(group);
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
  public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
    return dataSource.getPlayerPermissions(uuid);
  }

  public void subscribe(PermissionEventObserver observer) {
    observers.add(observer);
  }

  public void unsubscribe(PermissionEventObserver observer) {
    observers.remove(observer);
  }

  private void notify(PermissionEventType eventType, UUID uuid) {
    for (PermissionEventObserver observer : observers) {
      observer.onEvent(eventType, uuid);
    }
  }
}
