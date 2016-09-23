-- :name create-user-table
-- :command :execute
-- :result :raw
-- :doc Create user table
create table users (
id         integer auto_increment primary key,
username   varchar(40),
email      varchar(50),
alipay     varchar(100),
created_at timestamp not null default current_timestamp
)

-- :name create-event-table
-- :command :execute
-- :result :raw
-- :doc Create event table
create table events (
id         integer auto_increment primary key,
created_at timestamp not null default current_timestamp,
start_at   timestamp not null,
end_at     timestamp not null,
fee        float not null
)

-- :name create-attendee-table
-- :command :execute
-- :result :raw
-- :doc Create event table
create table attendee (
id         integer auto_increment primary key,
created_at timestamp not null default current_timestamp,
fee        float not null
)

-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(username, email, alipay)
VALUES (:username, :email, :alipay)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id
