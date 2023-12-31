package it.smallcode.permissionsystem.datasource.mysql.builder;

import it.smallcode.permissionsystem.datasource.mysql.builder.condition.Condition;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLQueryBuilder {

  private final String table;
  private final List<String> fields = new LinkedList<>();

  private Condition whereCondition = null;

  private Integer limit = null;

  private final List<Join> joins = new LinkedList<>();
  private final List<String> orders = new LinkedList<>();

  public SQLQueryBuilder(String table) {
    this.table = table;
  }

  public SQLQueryBuilder field(String field) {
    fields.add(field);
    return this;
  }

  public SQLQueryBuilder join(String table, Condition condition) {
    joins.add(new Join(table, condition));
    return this;
  }

  public SQLQueryBuilder where(Condition whereCondition) {
    this.whereCondition = whereCondition;
    return this;
  }

  public SQLQueryBuilder limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  public SQLQueryBuilder order(String order) {
    orders.add(order);
    return this;
  }

  public String select() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT ");
    query.append(String.join(",", fields));
    query.append(" FROM ").append(table);

    if (!joins.isEmpty()) {
      for (Join join : joins) {
        query.append(join.get());
      }
    }

    if (whereCondition != null) {
      query.append(" WHERE ").append(whereCondition.get());
    }

    if (!orders.isEmpty()) {
      query.append(" ORDER BY ").append(String.join(",", orders));
    }

    if (limit != null && limit >= 1) {
      query.append(" LIMIT ").append(limit);
    }

    query.append(";");

    return query.toString();
  }

  public String insert() {
    if (fields.isEmpty()) {
      return null;
    }

    StringBuilder fieldString = new StringBuilder();
    StringBuilder valueString = new StringBuilder();

    for (String field : fields) {
      fieldString.append(field).append(",");
      valueString.append("?,");
    }

    fieldString.deleteCharAt(fieldString.length() - 1);
    valueString.deleteCharAt(valueString.length() - 1);

    return "INSERT INTO " + table + "(" + fieldString + ") VALUES (" + valueString + ");";
  }

  public String update() {
    if (fields.isEmpty()) {
      return null;
    }

    String setFields = fields.stream().map(line -> line + " = ?").collect(Collectors.joining(", "));

    String where = "";
    if (whereCondition != null) {
      where = " WHERE " + whereCondition.get();
    }

    return "UPDATE " + table + " SET " + setFields + where + ";";
  }

  public String replace() {
    if (fields.isEmpty()) {
      return null;
    }

    StringBuilder fieldString = new StringBuilder();
    StringBuilder valueString = new StringBuilder();

    for (String field : fields) {
      fieldString.append(field).append(",");
      valueString.append("?,");
    }

    fieldString.deleteCharAt(fieldString.length() - 1);
    valueString.deleteCharAt(valueString.length() - 1);

    return "REPLACE INTO " + table + "(" + fieldString + ") VALUES (" + valueString + ");";
  }

  public String delete() {
    StringBuilder query = new StringBuilder();
    query.append("DELETE FROM ").append(table);

    if (whereCondition != null) {
      query.append(" WHERE ").append(whereCondition.get());
    }

    query.append(";");
    return query.toString();
  }

  private class Join {

    private final String table;
    private final Condition condition;

    private Join(String table, Condition condition) {
      this.table = table;
      this.condition = condition;
    }

    public String get() {
      return " INNER JOIN " + table + " ON " + condition.get();
    }
  }
}
