package it.smallcode.permissionsystem.manager;

import it.smallcode.permissionsystem.datasource.SignDataSource;
import it.smallcode.permissionsystem.models.SignLocation;
import java.util.Set;

public class SignManager {

  private final SignDataSource dataSource;

  public SignManager(SignDataSource dataSource) {
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
