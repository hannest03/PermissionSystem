package it.smallcode.permissionsystem.datasource.mysql.builder.condition;

public class BaseCondition implements Condition {

  private final String value;

  public BaseCondition(String value) {
    this.value = value;
  }

  @Override
  public String get() {
    return value;
  }
}
