package it.smallcode.permissionsystem.datasource.observable;

import it.smallcode.permissionsystem.models.SignLocation;

public interface SignEventObserver {

  void onNewSign(SignLocation signLocation);
}
