INSERT INTO permissions (id, last_updated,update_count,description,name) VALUES ((select max(id) from permissions)+1, now(),0,'Access to create supervisor permission action','CreateSupervisorPermissionAction');
INSERT INTO permissions (id, last_updated,update_count,description,name) VALUES ((select max(id) from permissions)+1, now(),0,'Access to read supervisor permission action','ReadSupervisorPermissionAction');
INSERT INTO permissions (id, last_updated,update_count,description,name) VALUES ((select max(id) from permissions)+1, now(),0,'Access to update supervisor permission action','UpdateSupervisorPermissionAction');
INSERT INTO permissions (id, last_updated,update_count,description,name) VALUES ((select max(id) from permissions)+1, now(),0,'Access to delete supervisor permission action','DeleteSupervisorPermissionAction');

INSERT INTO roles_permissions (roles_id,permissions_id) VALUES ((select id from roles where name='Admin'),(select id from permissions where name='CreateSupervisorPermissionAction'));
INSERT INTO roles_permissions (roles_id,permissions_id) VALUES ((select id from roles where name='Admin'),(select id from permissions where name='ReadSupervisorPermissionAction'));
INSERT INTO roles_permissions (roles_id,permissions_id) VALUES ((select id from roles where name='Admin'),(select id from permissions where name='UpdateSupervisorPermissionAction'));
INSERT INTO roles_permissions (roles_id,permissions_id) VALUES ((select id from roles where name='Admin'),(select id from permissions where name='DeleteSupervisorPermissionAction'));
