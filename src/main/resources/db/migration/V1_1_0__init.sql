create schema if not exists test;

CREATE TABLE IF NOT EXISTS test.authors
(
--                                           `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `id`    varchar(100),
    `first_name`  varchar(100),
    `last_name` varchar(100)
);