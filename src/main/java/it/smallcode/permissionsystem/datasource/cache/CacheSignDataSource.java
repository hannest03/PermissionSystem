package it.smallcode.permissionsystem.datasource.cache;

import it.smallcode.permissionsystem.datasource.SignDataSource;
import it.smallcode.permissionsystem.models.SignLocation;
import java.util.Collections;
import java.util.Set;

public class CacheSignDataSource implements SignDataSource {

  private final SignDataSource dataSource;

  private Set<SignLocation> signLocationCache = null;

  public CacheSignDataSource(SignDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Set<SignLocation> getSignLocations() {
    if (signLocationCache == null) {
      signLocationCache = Collections.synchronizedSet(dataSource.getSignLocations());
    }
    return signLocationCache;
  }

  @Override
  public boolean isSignLocation(SignLocation signLocation) {
    if (signLocationCache == null) {
      return dataSource.isSignLocation(signLocation);
    }
    return signLocationCache.contains(signLocation);
  }

  @Override
  public void addSignLocation(SignLocation signLocation) {
    dataSource.addSignLocation(signLocation);

    if (signLocationCache != null) {
      signLocationCache.add(signLocation);
    }
  }

  @Override
  public void removeSignLocation(SignLocation signLocation) {
    dataSource.removeSignLocation(signLocation);

    if (signLocationCache != null) {
      signLocationCache.remove(signLocation);
    }
  }
}
