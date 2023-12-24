package it.smallcode.permissionsystem.datasource.mysql.builder.condition;

public class OrCondition implements Condition {

  private final Condition firstCondition;
  private final Condition secondCondition;

  public OrCondition(Condition firstCondition, Condition secondCondition) {
    this.firstCondition = firstCondition;
    this.secondCondition = secondCondition;
  }

  @Override
  public String get() {
    return "(" + firstCondition.get() + " OR " + secondCondition.get() + ")";
  }
}

