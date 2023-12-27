package it.smallcode.permissionsystem.models;

import java.util.Objects;

public class Group {

  private Integer id;
  private String name;
  private String prefix;
  private int priority;
  private boolean isDefault;

  public Group() {
  }

  public Group(Integer id, String name, String prefix, int priority, boolean isDefault) {
    this.id = id;
    this.name = name;
    this.prefix = prefix;
    this.priority = priority;
    this.isDefault = isDefault;
  }

  public Group(String name, String prefix, int priority) {
    this(null, name, prefix, priority, false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Group group = (Group) o;
    return Objects.equals(id, group.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean aDefault) {
    isDefault = aDefault;
  }
}
