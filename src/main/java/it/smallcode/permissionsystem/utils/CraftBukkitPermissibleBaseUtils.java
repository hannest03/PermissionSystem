package it.smallcode.permissionsystem.utils;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

/*
The base of this class is from the LuckPerms Repository and a bukkit.org thread

https://github.com/LuckPerms/LuckPerms/blob/94809c2a665c62c20ce05e4b96173db6958acf71/bukkit/src/main/java/me/lucko/luckperms/bukkit/util/CraftBukkitImplementation.java
https://bukkit.org/threads/how-to-override-permission-checks.407679/
 */

public class CraftBukkitPermissibleBaseUtils implements PermissibleBaseUtils {

  private static final String SERVER_PACKAGE_VERSION;

  static {
    Class<?> server = Bukkit.getServer().getClass();
    Matcher matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$")
        .matcher(server.getName());
    if (matcher.matches()) {
      SERVER_PACKAGE_VERSION = '.' + matcher.group(1) + '.';
    } else {
      SERVER_PACKAGE_VERSION = ".";
    }
  }

  private final Field PERM_FIELD;

  public CraftBukkitPermissibleBaseUtils() {
    Field permField = null;
    try {
      Class<?> craftHumanEntityClass = obcClass("entity.CraftHumanEntity");
      permField = craftHumanEntityClass.getDeclaredField("perm");
      permField.setAccessible(true);
    } catch (NoSuchFieldException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    PERM_FIELD = permField;
  }

  private String obc(String className) {
    return "org.bukkit.craftbukkit" + SERVER_PACKAGE_VERSION + className;
  }

  private Class<?> obcClass(String className) throws ClassNotFoundException {
    return Class.forName(obc(className));
  }

  public void setPermissibleBase(Player p, PermissibleBase permissibleBase) {
    try {
      PERM_FIELD.set(p, permissibleBase);
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
