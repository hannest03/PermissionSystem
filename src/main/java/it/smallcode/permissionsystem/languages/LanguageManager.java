package it.smallcode.permissionsystem.languages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageManager {

  public static final String DEFAULT_LANG_CODE = "en";
  private static final String[] PREDEFINED_LANGUAGES = {"de", "en"};

  private final Map<String, Language> languages = new HashMap<>();

  public void loadLanguages(File directory) {
    if (directory == null || !directory.isDirectory()) {
      return;
    }

    // Copy language files to directory
    for (String languageCode : PREDEFINED_LANGUAGES) {
      File file = new File(directory, languageCode + ".yml");
      if (!file.exists()) {
        insertData("/translations/" + languageCode + ".yml", file.getAbsolutePath());
      }
    }

    for (File file : directory.listFiles()) {
      if (file.isDirectory() || !file.getName().endsWith(".yml")) {
        continue;
      }
      String langCode = file.getName();
      langCode = langCode.substring(0, langCode.indexOf(".yml"));

      Language language = new Language(langCode);

      FileConfiguration translations = YamlConfiguration.loadConfiguration(file);
      for (String translationKey : translations.getKeys(false)) {
        language.addTranslation(translationKey, translations.getString(translationKey));
      }

      languages.put(langCode, language);
    }
  }

  public Language getLanguage(String langCode) {
    langCode = langCode.toLowerCase();
    if (languages.containsKey(langCode)) {
      return languages.get(langCode);
    }
    if (languages.containsKey(DEFAULT_LANG_CODE)) {
      return languages.get(DEFAULT_LANG_CODE);
    }
    return null;
  }

  public Set<String> getLanguageCodes() {
    return languages.keySet();
  }

  /*
    Base of this method is from SmallPets:
    https://github.com/hannest03/SmallPets/blob/master/smallpets-core/src/main/java/it/smallcode/smallpets/core/utils/FileUtils.java
   */
  private void insertData(String from, String to) {
    File file = new File(to);
    file.delete();

    try (InputStream localInputStream = LanguageManager.class.getResourceAsStream(from)) {
      if (localInputStream != null) {
        Files.copy(localInputStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
