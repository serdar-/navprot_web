# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table task (
  id                        varchar(255) primary key,
  email                     varchar(255),
  initial_pdb               varchar(255),
  initial_chain             varchar(255),
  final_pdb                 varchar(255),
  final_chain               varchar(255),
  rmsd                      varchar(255),
  is_done                   integer(1),
  is_running                integer(1),
  is_cancelled              integer(1),
  upload_date               timestamp,
  start_time                timestamp,
  end_time                  timestamp)
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table task;

PRAGMA foreign_keys = ON;

