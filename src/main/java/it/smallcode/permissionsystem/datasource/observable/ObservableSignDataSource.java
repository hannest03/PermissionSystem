package it.smallcode.permissionsystem.datasource.observable;

import it.smallcode.permissionsystem.datasource.SignDataSource;
import it.smallcode.permissionsystem.models.SignLocation;
import java.util.HashSet;
import java.util.Set;

public class ObservableSignDataSource implements SignDataSource {

  private final SignDataSource dataSource;

  private Set<SignEventObserver> observers = new HashSet<>();

  public ObservableSignDataSource(SignDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Set<SignLocation> getSignLocations() {
    return dataSource.getSignLocations();
  }

  @Override
  public boolean isSignLocation(SignLocation signLocation) {
    return dataSource.isSignLocation(signLocation);
  }

  @Override
  public void addSignLocation(SignLocation signLocation) {
    dataSource.addSignLocation(signLocation);
    notify(signLocation);
  }

  @Override
  public void removeSignLocation(SignLocation signLocation) {
    dataSource.removeSignLocation(signLocation);
  }

  public void subscribe(SignEventObserver observer) {
    observers.add(observer);
  }

  public void unsubscribe(SignEventObserver observer) {
    observers.remove(observer);
  }

  private void notify(SignLocation signLocation) {
    for (SignEventObserver observer : observers) {
      observer.onNewSign(signLocation);
    }
  }
}
