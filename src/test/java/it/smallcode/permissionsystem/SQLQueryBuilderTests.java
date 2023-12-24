package it.smallcode.permissionsystem;

import it.smallcode.permissionsystem.datasource.mysql.builder.SQLQueryBuilder;
import it.smallcode.permissionsystem.datasource.mysql.builder.condition.BaseCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SQLQueryBuilderTests {

  @Test
  public void testInsert() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test");
      Assertions.assertNull(builder.insert());

    }
    {
      String query = "INSERT INTO test(id,field2) VALUES (?,?);";

      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("field2");
      Assertions.assertEquals(query, builder.insert());
    }
  }

  @Test
  public void testSelect() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .where(new BaseCondition("name = 'Test'"));

      String expected = "SELECT id,name FROM test WHERE name = 'Test';";
      Assertions.assertEquals(expected, builder.select());
    }

    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .limit(1);

      String expected = "SELECT id,name FROM test LIMIT 1;";
      Assertions.assertEquals(expected, builder.select());
    }

    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .where(new BaseCondition("name = 'Test'"))
          .limit(1);

      String expected = "SELECT id,name FROM test WHERE name = 'Test' LIMIT 1;";
      Assertions.assertEquals(expected, builder.select());
    }
  }
}
