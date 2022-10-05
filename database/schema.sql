drop schema if exists myrecipedb;

create schema myrecipedb;

use myrecipedb;

create table user(
    user_id int not null auto_increment,
    username varchar(20) not null,
    email varchar(128) not null,
    password varchar(286) not null,
    primary key(user_id)
);

create table recipe(
    recipe_id int not null auto_increment,
    name varchar(64) not null,
    category varchar(32),
    country varchar(32),
    instructions mediumtext not null,
    thumbnail varchar(128),
    youtubeLink varchar(128),
    user_id int not null,
    primary key(recipe_id),
    constraint fk_user_id
        foreign key(user_id) 
        references user(user_id)
);

create table ingredient(
    ingredient_id int not null auto_increment,
    name varchar(64) not null,
    measurement varchar(16) not null,
    recipe_id int not null,
    primary key(ingredient_id),
    constraint fk_recipe_id
        foreign key(recipe_id) 
        references recipe(recipe_id)
);
