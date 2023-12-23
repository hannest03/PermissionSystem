package it.smallcode.permissionsystem.datasource.mysql;

import java.util.HashMap;

public class SQLQueryBuilder {

  private final String table;

  public SQLQueryBuilder(String table) {
    this.table = table;
  }

  public InsertBuilder insert() {
    return new InsertBuilder(table);
  }

  public static class InsertBuilder {

    private final String table;

    private final HashMap<String, String> values = new HashMap<>();

    private InsertBuilder(String table) {
      this.table = table;
    }

    public InsertBuilder value(String field, String value) {
      values.put(field, value);
      return this;
    }
  }

  public static class WhereBuilder {

    private WhereBuilder() {
    }
  }
}
