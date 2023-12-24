package it.smallcode.permissionsystem.datasource.mysql.builder.condition;

public class AndCondition implements Condition {

  private final Condition firstCondition;
  private final Condition secondCondition;

  public AndCondition(Condition firstCondition, Condition secondCondition) {
    this.firstCondition = firstCondition;
    this.secondCondition = secondCondition;
  }

  @Override
  public String get() {
    return "(" + firstCondition.get() + " AND " + secondCondition.get() + ")";
  }
}

