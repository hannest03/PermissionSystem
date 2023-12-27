package it.smallcode.permissionsystem.datasource;

import java.util.UUID;

public interface LanguageDataSource {

  String getLanguageCode(UUID uuid);

  void setLanguageCode(UUID uuid, String languageCode);
}
