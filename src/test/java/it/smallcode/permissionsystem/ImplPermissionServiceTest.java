package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.datasource.PermissionDataSource;
import it.smallcode.permissionsystem.models.Group;
import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.models.PlayerGroup;
import it.smallcode.permissionsystem.services.PermissionService;
import it.smallcode.permissionsystem.services.impl.ImplPermissionService;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImplPermissionServiceTest {

  private static PermissionService permissionService;
  private static MockPermissionDataSource mockPermissionDataSource;

  private static Group defaultGroup;
  private static Group secondGroup;

  private static UUID playerUUID = UUID.randomUUID();

  @BeforeEach
  public void init() {
    mockPermissionDataSource = new MockPermissionDataSource();
    defaultGroup = new Group();
    defaultGroup.setDefault(true);
    defaultGroup.setName("Default");
    defaultGroup.setId(1);
    defaultGroup.setPriority(1);
    mockPermissionDataSource.groupSet.add(defaultGroup);

    secondGroup = new Group();
    secondGroup.setDefault(false);
    secondGroup.setName("Test");
    secondGroup.setId(3);
    secondGroup.setPriority(10);
    mockPermissionDataSource.groupSet.add(secondGroup);

    mockPermissionDataSource.userInfos.add(new PlayerGroup(secondGroup, null));

    permissionService = new ImplPermissionService(mockPermissionDataSource);
  }

  @Test
  public void testGetGroupByName() {
    Assertions.assertEquals(secondGroup, permissionService.getGroupByName("Test"));
  }

  @Test
  public void testHasGroup() {
    Assertions.assertTrue(permissionService.hasPlayerGroup(playerUUID, secondGroup));
    Assertions.assertFalse(permissionService.hasPlayerGroup(playerUUID, defaultGroup));
  }

  @Test
  public void testAddGroup() {
    permissionService.addPlayerGroup(playerUUID, defaultGroup);

    Assertions.assertTrue(
        mockPermissionDataSource.userInfos
            .contains(mockPermissionDataSource.getPlayerGroupFromGroup(defaultGroup)));
  }

  @Test
  public void testRemoveGroup() {
    permissionService.removePlayerGroup(playerUUID, secondGroup);

    Assertions.assertFalse(
        mockPermissionDataSource.userInfos.contains(new PlayerGroup(secondGroup, null)));
    Assertions.assertTrue(
        mockPermissionDataSource.userInfos.contains(new PlayerGroup(defaultGroup, null)));
  }

  @Test
  public void testInit() {
    MockPermissionDataSource dataSource = new MockPermissionDataSource();
    ImplPermissionService implPermissionService = new ImplPermissionService(dataSource);
    implPermissionService.init();

    Assertions.assertEquals(1, dataSource.groupSet.size());
    Assertions.assertNotNull(dataSource.getDefaultGroup());
  }

  private static class MockPermissionDataSource implements PermissionDataSource {

    private final Set<Group> groupSet = new HashSet<>();

    private final List<PlayerGroup> userInfos = new LinkedList<>();

    @Override
    public void createGroup(Group group) {
      groupSet.add(group);
    }

    @Override
    public void updateGroup(Group group) {
      groupSet.add(group);
    }

    @Override
    public void deleteGroup(Group group) {
      groupSet.remove(group);
    }

    @Override
    public void addPermission(Group group, String permission) {
    }

    @Override
    public void removePermission(Group group, String permission) {

    }

    @Override
    public List<Group> getGroups() {
      return groupSet.stream().toList();
    }

    @Override
    public Group getDefaultGroup() {
      return groupSet.stream().filter(Group::isDefault).findFirst().orElse(null);
    }

    @Override
    public List<PlayerGroup> getPlayerGroups(UUID uuid) {
      return userInfos;
    }

    @Override
    public Group getPrimaryGroup(UUID uuid) {
      return null;
    }

    @Override
    public void addPlayerGroup(UUID uuid, Group group, Instant end) {
      userInfos.add(new PlayerGroup(group, end));
    }

    @Override
    public void removePlayerGroup(UUID uuid, Group group) {
      PlayerGroup current = getPlayerGroupFromGroup(group);
      userInfos.remove(current);
    }

    @Override
    public Set<PermissionInfo> getPlayerPermissions(UUID uuid) {
      return null;
    }

    private PlayerGroup getPlayerGroupFromGroup(Group group) {
      PlayerGroup current = null;
      for (PlayerGroup playerGroup : userInfos) {
        if (playerGroup.group().equals(group)) {
          current = playerGroup;
          break;
        }
      }
      return current;
    }
  }
}
