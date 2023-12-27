# Permission System

## Setup

To run this plugin you need a MariaDB database or equivalent.
First please import the `schema.sql` into your database.
Then start the server, it will probably say that it couldn't connect to the database and deactivate.
Change the database credentials in the newly generated `config.yml` and restart the server.
Now the plugin should function correctly.

## Commands

| Command                                                           | Permission                             | Description                                                        |
|-------------------------------------------------------------------|----------------------------------------|--------------------------------------------------------------------|
| `/permissions`                                                    | -                                      | Shows your current groups                                          |
| `/permissions group`                                              | `permissions.group`                    | -                                                                  |
| `/permissions group create <group>`                               | `permissions.group.create`             | Creates a new group                                                |
| `/permissions group default <group>`                              | `permissions.group.default`            | Sets a new default group                                           |
| `/permissions group delete <group>`                               | `permissions.group.delete`             | Deletes a group (can't delete default group)                       |
| `/permissions group info <group>`                                 | `permissions.group.info`               | Shows the info of a group                                          |
| `/permissions group prefix <group> [<prefix>]`                    | `permissions.group.prefix`             | Shows/sets the prefix of a group                                   |
| `/permissions group priority <group> [<priority>]`                | `permissions.group.priority`           | Shows/sets the priority of a group                                 |
| `/permissions group permissions`                                  | `permissions.group.permissions`        | -                                                                  |
| `/permissions group permissions add <group> <permission>`         | `permissions.group.permissions.add`    | Adds a permission to a group (supports * and negative permissions) |
| `/permissions group permissions remove <group> <permission>`      | `permissions.group.permissions.remove` | Removes a permission to a group                                    |
| `/permissions language <langCode>`                                | -                                      | Sets the language of a player                                      |
| `/permissions player`                                             | `permissions.player`                   | -                                                                  |
| `/permissions player group`                                       | `permissions.player.group`             | -                                                                  |
| `/permissions player group add <player> <group> [<[d][h][m][s]>]` | `permissions.player.group.add`         | Adds a player to a group                                           |
| `/permissions player group remove <player> <group>`               | `permissions.player.group.remove`      | Removes a player from a group                                      |
| `/permissions sign`                                               | `permissions.sign`                     | -                                                                  |
| `/permissions sign add`                                           | `permissions.sign.add`                 | Marks a sign to show information about a player                    |
| `/permissions sign remove`                                        | `permissions.sign.remove`              | Unmarks a sign                                                     |

## Time Format

The time format used when adding a timed group to a player is `[d][h][m][s]` an example would
be `10d5s` for 10 days and 5 seconds.
`1d2h3m4s` this would be 1 day 2 hours 3 minutes and 4 seconds.