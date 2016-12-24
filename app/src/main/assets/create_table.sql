CREATE TABLE IF NOT EXISTS teams (_id integer primary key autoincrement, name text unique not null, created_at integer not null, updated_at integer);

CREATE TABLE IF NOT EXISTS urls (_id integer primary key autoincrement, key text unique not null, url text not null, created_at integer not null, updated_at integer);

CREATE TABLE IF NOT EXISTS bookmarks (_id integer primary key autoincrement, team_id integer not null, post_id integer unique not null, title text not null, created_at integer not null, updated_at integer);

CREATE TABLE IF NOT EXISTS histories (_id integer primary key autoincrement, team_id integer not null, post_id integer unique not null, title text not null, created_at integer not null, updated_at integer);
