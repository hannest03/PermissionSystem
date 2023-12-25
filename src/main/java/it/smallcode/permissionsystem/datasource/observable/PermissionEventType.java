package it.smallcode.permissionsystem.datasource.observable;

public enum PermissionEventType {
  // If permission changed for a specific player
  PLAYER_PERMISSION_CHANGED,
  // If permission changed for a group
  GROUP_PERMISSION_CHANGED,
  // If player joined / left a group
  PLAYER_GROUP_CHANGED,
  // If a group changed
  GROUP_CHANGED
}
