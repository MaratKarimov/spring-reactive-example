-- create book
CREATE TABLE IF NOT EXISTS reactive.book (
    id SERIAL
    , version INT NOT NULL DEFAULT 1
    , author TEXT NOT NULL
    , title TEXT NOT NULL
);