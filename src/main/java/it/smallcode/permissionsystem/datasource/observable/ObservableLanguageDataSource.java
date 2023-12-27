package it.smallcode.permissionsystem.datasource.observable;

import it.smallcode.permissionsystem.datasource.LanguageDataSource;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ObservableLanguageDataSource implements LanguageDataSource {

  private final LanguageDataSource dataSource;
  private final Set<LanguageChangeObserver> observers = new HashSet<>();

  public ObservableLanguageDataSource(LanguageDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public String getLanguageCode(UUID uuid) {
    return dataSource.getLanguageCode(uuid);
  }

  @Override
  public void setLanguageCode(UUID uuid, String languageCode) {
    dataSource.setLanguageCode(uuid, languageCode);
    notifyLanguageChange(uuid);
  }

  public void subscribe(LanguageChangeObserver observer) {
    observers.add(observer);
  }

  public void unsubscribe(LanguageChangeObserver observer) {
    observers.remove(observer);
  }

  public void notifyLanguageChange(UUID uuid) {
    for (LanguageChangeObserver observer : observers) {
      observer.onLanguageChange(uuid);
    }
  }
}
