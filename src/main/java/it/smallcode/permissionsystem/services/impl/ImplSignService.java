package it.smallcode.permissionsystem.services.impl;

import it.smallcode.permissionsystem.datasource.SignDataSource;
import it.smallcode.permissionsystem.models.SignLocation;
import it.smallcode.permissionsystem.services.SignService;
import it.smallcode.permissionsystem.services.registry.Service;
import java.util.Set;

public class ImplSignService implements Service, SignService {

  private final SignDataSource dataSource;

  public ImplSignService(SignDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Set<SignLocation> getSignLocations() {
    return dataSource.getSignLocations();
  }

  public boolean isSignLocation(SignLocation signLocation) {
    return dataSource.isSignLocation(signLocation);
  }

  public void addSignLocation(SignLocation signLocation) {
    if (!isSignLocation(signLocation)) {
      dataSource.addSignLocation(signLocation);
    }
  }

  public void removeSignLocation(SignLocation signLocation) {
    dataSource.removeSignLocation(signLocation);
  }
}
