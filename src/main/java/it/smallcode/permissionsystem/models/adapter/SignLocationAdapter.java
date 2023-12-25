package it.smallcode.permissionsystem.models.adapter;

import it.smallcode.permissionsystem.models.SignLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SignLocationAdapter {

  public SignLocation fromLocation(Location location) {
    if (location.getWorld() == null) {
      return null;
    }
    return new SignLocation(
        location.getWorld().getName(),
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ()
    );
  }

  public Location toLocation(SignLocation signLocation) {
    if (Bukkit.getWorld(signLocation.world()) == null) {
      return null;
    }
    return new Location(
        Bukkit.getWorld(signLocation.world()),
        signLocation.x(),
        signLocation.y(),
        signLocation.z()
    );
  }
}
