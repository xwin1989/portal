insert into message(created,summary,text) values ('2013-10-04 10:00:00','Hello Rob','This message is for Rob');
insert into message(created,summary,text) values ('2013-10-04 10:00:00','Hello Luke','This message is for Luke');

insert into user(name,password,salt) values ('neal','password','neal');
insert into user(name,password,salt) values ('user','password','user');

insert into role(name,description,available) values ('admin','admin',1)
insert into role(name,description,available) values ('user','user',1)

insert into user_role values (1,1)
insert into user_role values (1,2)