DROP TABLE IF EXISTS health;

CREATE TABLE health
(
    id INT PRIMARY KEY,
    up BOOLEAN
);

INSERT INTO health
    (id, up)
VALUES (1, true);

DROP TABLE IF EXISTS authorized_user;

CREATE TABLE authorized_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    role TEXT NOT NULL
);
