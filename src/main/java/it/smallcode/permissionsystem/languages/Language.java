package it.smallcode.permissionsystem.languages;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Language {

  private final String code;

  private final Map<String, String> translations = new HashMap<>();

  protected Language(String code) {
    this.code = code.toLowerCase();
  }

  protected void addTranslation(String key, String translation) {
    translations.put(key, translation);
  }

  public String getTranslation(String key) {
    if (translations.containsKey(key)) {
      return translations.get(key);
    }
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Language language = (Language) o;
    return Objects.equals(code, language.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  public String getCode() {
    return code;
  }
}
