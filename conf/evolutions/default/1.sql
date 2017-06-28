# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table my_activity (
  id                            bigint not null,
  user_id                       bigint not null,
  activity_type                 varchar(255),
  location                      varchar(255),
  distance                      double precision,
  created_on                    timestamp,
  last_update                   timestamp,
  constraint pk_my_activity primary key (id)
);
create sequence my_activity_seq;

create table my_location (
  id                            bigint not null,
  activity_id                   bigint not null,
  latitude                      float,
  longitude                     float,
  created_on                    timestamp,
  constraint pk_my_location primary key (id)
);
create sequence my_location_seq;

create table my_user (
  id                            bigint not null,
  firstname                     varchar(255),
  lastname                      varchar(255),
  email                         varchar(255) not null,
  password                      varchar(255) not null,
  phase_interval                integer,
  api_key                       varchar(255),
  constraint uq_my_user_email unique (email),
  constraint pk_my_user primary key (id)
);
create sequence my_user_seq;

create table user_user (
  source_user_id                bigint not null,
  target_user_id                bigint not null,
  constraint pk_user_user primary key (source_user_id,target_user_id)
);

alter table my_activity add constraint fk_my_activity_user_id foreign key (user_id) references my_user (id) on delete restrict on update restrict;
create index ix_my_activity_user_id on my_activity (user_id);

alter table my_location add constraint fk_my_location_activity_id foreign key (activity_id) references my_activity (id) on delete restrict on update restrict;
create index ix_my_location_activity_id on my_location (activity_id);

alter table user_user add constraint fk_user_user_my_user_1 foreign key (source_user_id) references my_user (id) on delete restrict on update restrict;
create index ix_user_user_my_user_1 on user_user (source_user_id);

alter table user_user add constraint fk_user_user_my_user_2 foreign key (target_user_id) references my_user (id) on delete restrict on update restrict;
create index ix_user_user_my_user_2 on user_user (target_user_id);


# --- !Downs

alter table my_activity drop constraint if exists fk_my_activity_user_id;
drop index if exists ix_my_activity_user_id;

alter table my_location drop constraint if exists fk_my_location_activity_id;
drop index if exists ix_my_location_activity_id;

alter table user_user drop constraint if exists fk_user_user_my_user_1;
drop index if exists ix_user_user_my_user_1;

alter table user_user drop constraint if exists fk_user_user_my_user_2;
drop index if exists ix_user_user_my_user_2;

drop table if exists my_activity;
drop sequence if exists my_activity_seq;

drop table if exists my_location;
drop sequence if exists my_location_seq;

drop table if exists my_user;
drop sequence if exists my_user_seq;

drop table if exists user_user;

