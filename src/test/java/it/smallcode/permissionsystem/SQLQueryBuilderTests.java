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
  public void testReplace() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test");
      Assertions.assertNull(builder.replace());

    }
    {
      String query = "REPLACE INTO test(id,field2) VALUES (?,?);";

      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("field2");
      Assertions.assertEquals(query, builder.replace());
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
  }

  @Test
  public void testLimit() {
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
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .where(new BaseCondition("name = 'Test'"))
          .limit(0);

      String expected = "SELECT id,name FROM test WHERE name = 'Test';";
      Assertions.assertEquals(expected, builder.select());
    }
  }

  @Test
  public void testOrder() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .order("id ASC");

      String expected = "SELECT id,name FROM test ORDER BY id ASC;";
      Assertions.assertEquals(expected, builder.select());
    }
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .field("name")
          .order("id ASC")
          .order("name DESC");

      String expected = "SELECT id,name FROM test ORDER BY id ASC,name DESC;";
      Assertions.assertEquals(expected, builder.select());
    }
  }

  @Test
  public void testDelete() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("asd");
      String expected = "DELETE FROM test;";
      Assertions.assertEquals(expected, builder.delete());
    }
    {
      String query = "DELETE FROM test WHERE id = 'asd';";

      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .where(new BaseCondition("id = 'asd'"));

      Assertions.assertEquals(query, builder.delete());
    }
  }

  @Test
  public void testJoin() {
    SQLQueryBuilder builder = new SQLQueryBuilder("test")
        .field("*")
        .join("test2", new BaseCondition("test.id = test2.id_test"));

    String expected = "SELECT * FROM test INNER JOIN test2 ON test.id = test2.id_test;";
    Assertions.assertEquals(expected, builder.select());
  }

  @Test
  public void testUpdate() {
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test");
      Assertions.assertNull(builder.update());
    }
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id");
      String expected = "UPDATE test SET id = ?;";
      Assertions.assertEquals(expected, builder.update());
    }
    {
      SQLQueryBuilder builder = new SQLQueryBuilder("test")
          .field("id")
          .where(new BaseCondition("id = 5"));
      String expected = "UPDATE test SET id = ? WHERE id = 5;";
      Assertions.assertEquals(expected, builder.update());
    }
  }
}
