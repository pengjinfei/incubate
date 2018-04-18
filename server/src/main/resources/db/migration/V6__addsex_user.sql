alter table user
  add column sex tinyint;
update user u set u.sex = 0 where u.id = 2;
update user u set u.sex = 1 where u.id = 1;
update user u set u.sex = 1 where u.id = 4;

