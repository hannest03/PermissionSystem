package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.models.PermissionInfo;
import it.smallcode.permissionsystem.permissions.OptimizedPermissions;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptimizedPermissionsTest {

  @Test
  public void testOptimizer() {
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 1));
      permissionInfos.add(new PermissionInfo("-test2", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertTrue(optimizedPermissions.getPermissions().get("test"));
      Assertions.assertFalse(optimizedPermissions.getPermissions().get("test2"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 1));
      permissionInfos.add(new PermissionInfo("-test", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertFalse(optimizedPermissions.getPermissions().get("test"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 0));
      permissionInfos.add(new PermissionInfo("-test", 1));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertTrue(optimizedPermissions.getPermissions().get("test"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 0));
      permissionInfos.add(new PermissionInfo("-test", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertFalse(optimizedPermissions.getPermissions().get("test"));
    }
  }

  @Test
  public void testHasPermission() {
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 0));
      permissionInfos.add(new PermissionInfo("-test2", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertTrue(optimizedPermissions.hasPermission("test"));
      Assertions.assertFalse(optimizedPermissions.hasPermission("test2"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("test", 1));
      permissionInfos.add(new PermissionInfo("-test", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertFalse(optimizedPermissions.hasPermission("test"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      permissionInfos.add(new PermissionInfo("*", 0));
      permissionInfos.add(new PermissionInfo("-test", 0));

      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertFalse(optimizedPermissions.hasPermission("test"));
      Assertions.assertTrue(optimizedPermissions.hasPermission("random permission"));
    }
    {
      Set<PermissionInfo> permissionInfos = new HashSet<>();
      OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
      Assertions.assertFalse(optimizedPermissions.hasPermission("test"));
    }
  }

  @Test
  public void testIsPermissionSet() {
    Set<PermissionInfo> permissionInfos = new HashSet<>();
    permissionInfos.add(new PermissionInfo("test", 0));
    permissionInfos.add(new PermissionInfo("-test2", 0));

    OptimizedPermissions optimizedPermissions = new OptimizedPermissions(permissionInfos);
    Assertions.assertTrue(optimizedPermissions.isPermissionSet("test"));
    Assertions.assertTrue(optimizedPermissions.isPermissionSet("test2"));
    Assertions.assertFalse(optimizedPermissions.isPermissionSet("random permission"));
  }

}
