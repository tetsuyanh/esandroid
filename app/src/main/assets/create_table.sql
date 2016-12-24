CREATE TABLE IF NOT EXISTS urls (_id integer primary key autoincrement, team_name text not null, key text not null, url text not null, created_at integer not null, updated_at integer);

CREATE TABLE IF NOT EXISTS bookmarks (_id integer primary key autoincrement, team_name text not null, post_id integer unique not null, title text not null, created_at integer not null, updated_at integer);

CREATE TABLE IF NOT EXISTS histories (_id integer primary key autoincrement, team_name text not null, post_id integer unique not null, title text not null, created_at integer not null, updated_at integer);
