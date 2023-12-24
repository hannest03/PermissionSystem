package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.datasource.mysql.builder.condition.AndCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.BaseCondition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.Condition;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.OrCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConditionTest {

  @Test
  public void testBaseCondition() {
    Condition base = new BaseCondition("true");
    Assertions.assertEquals("true", base.get());
  }

  @Test
  public void testAndCondition() {
    Condition andCondition = new AndCondition(
        new BaseCondition("true"),
        new BaseCondition("false")
    );

    String expected = "(true AND false)";
    Assertions.assertEquals(expected, andCondition.get());
  }

  @Test
  public void testOrCondition() {
    Condition orCondition = new OrCondition(
        new BaseCondition("true"),
        new BaseCondition("false")
    );

    String expected = "(true OR false)";
    Assertions.assertEquals(expected, orCondition.get());
  }

  @Test
  public void testCombination() {
    Condition firstCondition = new OrCondition(
        new BaseCondition("false"),
        new BaseCondition("true")
    );
    Condition secondCondition = new OrCondition(
        new BaseCondition("true"),
        new OrCondition(
            new BaseCondition("false"),
            new BaseCondition("false")
        )
    );
    Condition combinationCondition = new AndCondition(firstCondition, secondCondition);

    String expected = "((false OR true) AND (true OR (false OR false)))";
    Assertions.assertEquals(expected, combinationCondition.get());
  }
}
