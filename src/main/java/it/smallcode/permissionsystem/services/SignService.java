package it.smallcode.permissionsystem.services;

import it.smallcode.permissionsystem.models.SignLocation;
import it.smallcode.permissionsystem.services.registry.Service;
import java.util.Set;

public interface SignService extends Service {

  Set<SignLocation> getSignLocations();

  boolean isSignLocation(SignLocation signLocation);

  void addSignLocation(SignLocation signLocation);

  void removeSignLocation(SignLocation signLocation);
}
