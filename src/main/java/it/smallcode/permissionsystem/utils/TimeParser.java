package it.smallcode.permissionsystem.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

  private static final String PATTERN_STRING = "((\\d+)d)?((\\d+)h)?((\\d+)m)?((\\d+)s)?";
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

  private TimeParser() {
  }

  public static Duration parseDuration(String durationString) {
    Matcher matcher = PATTERN.matcher(durationString);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid input string!");
    }
    int days = parseInt(matcher.group(2));
    int hours = parseInt(matcher.group(4));
    int minutes = parseInt(matcher.group(6));
    int seconds = parseInt(matcher.group(8));

    return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
  }

  private static int parseInt(String input) {
    return (input != null) ? Integer.parseInt(input) : 0;
  }
}
