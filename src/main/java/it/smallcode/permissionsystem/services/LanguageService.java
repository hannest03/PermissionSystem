package it.smallcode.permissionsystem.services;

import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.services.registry.Service;
import java.util.Set;
import java.util.UUID;

public interface LanguageService extends Service {

  void loadPlayer(UUID uuid);

  void unloadPlayer(UUID uuid);

  Language getLanguage(UUID uuid);

  Language getDefaultLanguage();

  Set<String> getLanguageCodes();

  void setLanguage(UUID uuid, String languageCode);
}
