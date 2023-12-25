package it.smallcode.permissionsystem.models;

import java.time.Duration;
import java.time.Instant;

public record PlayerGroup(Group group, Instant end) {

  public String toTimeLeft() {
    if (end == null) {
      return null;
    }
    Duration duration = Duration.between(Instant.now(), end);

    return String.format("%dd %02dh %02dm %02ds",
        duration.toDays(),
        duration.toHoursPart(),
        duration.toMinutesPart(),
        duration.toSecondsPart());
  }
}
