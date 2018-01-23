CREATE TABLE `artifact_associations` ( `association_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `artifact_id` INTEGER, `group_id` INTEGER );
CREATE TABLE `artifact_store` ( `artifact_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name`TEXT, `desc` TEXT, `price` INTEGER );
CREATE TABLE `group_names` ( `group_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `group_name` TEXT UNIQUE );
CREATE TABLE `predefined_levels` ( `threshold` INTEGER NOT NULL UNIQUE, `level_name` TEXT UNIQUE, PRIMARY KEY(`threshold`) );
CREATE TABLE `quest_associations` ( `association_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `quest_id` INTEGER, `group_id` INTEGER );
CREATE TABLE `quest_store` ( `quest_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT UNIQUE, `desc` TEXT, `reward` INTEGER );
CREATE TABLE `user_artifacts` ( `user_artifact_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_id` INTEGER, `artifact_id` INTEGER, `used` TEXT );
CREATE TABLE `user_associations` ( `association_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_id` INTEGER, `group_id` INTEGER );
CREATE TABLE 'user_experience' ( `experience_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_id` INTEGER UNIQUE, `experience_gained` INTEGER );
CREATE TABLE `user_privilage_levels` ( `privilage_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `privilage_name` TEXT UNIQUE );
CREATE TABLE `user_roles` ( `role_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_id` INTEGER UNIQUE, `user_privilage_level` INTEGER );
CREATE TABLE `user_wallet` ( `wallet_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_id` INTEGER, `balance` INTEGER );
CREATE TABLE `users` ( `user_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `nickname` TEXT NOT NULL UNIQUE, `password` TEXT NOT NULL, `email` TEXT );
INSERT INTO users(nickname, password, email) VALUES('admin','admin','adminjerzy@cc.com.pl');