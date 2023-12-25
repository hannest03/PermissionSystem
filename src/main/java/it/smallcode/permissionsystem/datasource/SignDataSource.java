package it.smallcode.permissionsystem.datasource;

import it.smallcode.permissionsystem.models.SignLocation;
import java.util.Set;

public interface SignDataSource {

  Set<SignLocation> getSignLocations();

  boolean isSignLocation(SignLocation signLocation);

  void addSignLocation(SignLocation signLocation);

  void removeSignLocation(SignLocation signLocation);
}
