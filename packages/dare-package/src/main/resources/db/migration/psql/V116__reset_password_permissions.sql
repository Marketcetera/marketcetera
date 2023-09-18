insert into metc_permissions values(((select max(id) from metc_permissions)+1),now(),0,'Access to reset user passwords','ResetUserPasswordAction');
insert into metc_roles_permissions select a.id as roles_id,b.id as permissions_id from metc_roles a,metc_permissions b where b.name = 'ResetUserPasswordAction' and (a.name='Admin');
