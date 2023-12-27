package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.utils.TimeParser;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeParserTest {

  @Test
  public void testTimeParser() {
    String durationString = "4d7m23s";
    Duration duration = TimeParser.parseDuration(durationString);
    Duration expected = Duration.ofDays(4).plusMinutes(7).plusSeconds(23);

    Assertions.assertEquals(expected, duration);
  }

  @Test
  public void testEmpty() {
    String durationString = "";
    Duration duration = TimeParser.parseDuration(durationString);
    Duration expected = Duration.ZERO;

    Assertions.assertEquals(expected, duration);
  }

  @Test
  public void testInvalid() {
    String durationString = "asd";
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> TimeParser.parseDuration(durationString));
  }
}
