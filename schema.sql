CREATE TABLE groups(
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(16),
    prefix VARCHAR(16),
    priority INT,
    is_default BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE playergroups(
    id_player VARCHAR(36) NOT NULL,
    id_group INT NOT NULL,
    end_date DATETIME,
    PRIMARY KEY (id_player, id_group),
    FOREIGN KEY (id_group) REFERENCES groups(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE permissions (
    id_group INT NOT NULL,
    permission VARCHAR(100),
    PRIMARY KEY (id_group, permission),
    FOREIGN KEY (id_group) REFERENCES groups(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE signs (
    world VARCHAR(50) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    PRIMARY KEY (world, x, y, z)
);

CREATE TABLE playerlanguages(
    id_player VARCHAR(36) NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    PRIMARY KEY (id_player)
);