package it.smallcode.permissionsystem;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.smallcode.permissionsystem.handler.permissible.OptimizedPermissions;
import it.smallcode.permissionsystem.handler.permissible.PlayerPermissibleBase;
import it.smallcode.permissionsystem.models.PermissionInfo;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.permissions.Permission;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PermissibleBaseTest {

  private ServerMock server;
  private PlayerMock player;

  private PlayerPermissibleBase playerPermissibleBase;

  @BeforeEach
  public void setUp() {
    server = MockBukkit.mock();
    player = server.addPlayer();

    Set<PermissionInfo> permissionInfos = new HashSet<>();
    permissionInfos.add(new PermissionInfo("test", 1));
    permissionInfos.add(new PermissionInfo("-test2", 0));
    permissionInfos.add(new PermissionInfo("test3", 0));
    permissionInfos.add(new PermissionInfo("-test3", 1));

    playerPermissibleBase = new PlayerPermissibleBase(player,
        new OptimizedPermissions(permissionInfos));
  }

  @Test
  public void testPermissibleHasPermission() {
    Assertions.assertTrue(playerPermissibleBase.hasPermission("test"));
    Assertions.assertFalse(playerPermissibleBase.hasPermission("test2"));
    Assertions.assertTrue(playerPermissibleBase.hasPermission("test3"));

    Assertions.assertTrue(playerPermissibleBase.hasPermission(new Permission("test")));
    Assertions.assertFalse(playerPermissibleBase.hasPermission(new Permission("test2")));
    Assertions.assertTrue(playerPermissibleBase.hasPermission(new Permission("test3")));

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> playerPermissibleBase.hasPermission((Permission) null));
  }

  @Test
  public void testPermissibleHasPermissionCheckerNull() {
    playerPermissibleBase.setPermissionChecker(null);
    Assertions.assertFalse(playerPermissibleBase.hasPermission("test"));
  }

  @Test
  public void testPermissibleIsPermissionSet() {
    Assertions.assertTrue(playerPermissibleBase.isPermissionSet("test"));
    Assertions.assertTrue(playerPermissibleBase.isPermissionSet("test2"));
    Assertions.assertTrue(playerPermissibleBase.isPermissionSet("test3"));
    Assertions.assertFalse(playerPermissibleBase.isPermissionSet("not_set"));

    Assertions.assertTrue(playerPermissibleBase.isPermissionSet(new Permission("test")));
    Assertions.assertFalse(playerPermissibleBase.isPermissionSet(new Permission("not_set")));

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> playerPermissibleBase.isPermissionSet((Permission) null));
  }

  @Test
  public void testPermissiblePlayer() {
    PlayerPermissibleBase permissibleBase = new PlayerPermissibleBase(player);
    Assertions.assertFalse(permissibleBase.hasPermission("test"));
  }

  @AfterEach
  public void tearDown() {
    MockBukkit.unmock();
  }
}
