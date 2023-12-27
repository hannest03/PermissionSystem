package it.smallcode.permissionsystem.services.impl;

import it.smallcode.permissionsystem.datasource.LanguageDataSource;
import it.smallcode.permissionsystem.languages.Language;
import it.smallcode.permissionsystem.languages.LanguageManager;
import it.smallcode.permissionsystem.services.LanguageService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ImplLanguageService implements LanguageService {

  private final LanguageManager languageManager;
  private final Map<UUID, String> language = new HashMap<>();

  private final LanguageDataSource dataSource;

  public ImplLanguageService(LanguageManager languageManager, LanguageDataSource dataSource) {
    this.languageManager = languageManager;
    this.dataSource = dataSource;
  }

  @Override
  public void loadPlayer(UUID uuid) {
    String languageCode = dataSource.getLanguageCode(uuid);
    if (languageCode == null) {
      languageCode = LanguageManager.DEFAULT_LANG_CODE;
    }
    language.put(uuid, languageCode);
  }

  @Override
  public void unloadPlayer(UUID uuid) {
    language.remove(uuid);
  }

  @Override
  public Language getLanguage(UUID uuid) {
    if (!language.containsKey(uuid)) {
      language.put(uuid, LanguageManager.DEFAULT_LANG_CODE);
    }
    final String langCode = language.get(uuid);
    return languageManager.getLanguage(langCode);
  }

  @Override
  public Language getDefaultLanguage() {
    return languageManager.getLanguage(null);
  }

  @Override
  public Set<String> getLanguageCodes() {
    return languageManager.getLanguageCodes();
  }

  @Override
  public void setLanguage(UUID uuid, String languageCode) {
    if (!getLanguageCodes().contains(languageCode)) {
      return;
    }
    language.put(uuid, languageCode);
    dataSource.setLanguageCode(uuid, languageCode);
  }
}
