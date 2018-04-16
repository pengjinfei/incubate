alter table user
  add column tags varchar(50);
update user u
set u.tags = 'girl,beaty'
where u.id = 2;
update user u
set u.tags = 'test,strong,man'
where u.id = 1;


