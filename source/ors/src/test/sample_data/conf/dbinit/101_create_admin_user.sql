insert into ors_users (id,updateCount,lastUpdated,name,description,hashedPassword,active,superuser)
values (1,0,now(),"admin",null,"6anqbgybi82pveayzrkt3egjkwfwdg5",1,1) on duplicate key
update hashedPassword = "6anqbgybi82pveayzrkt3egjkwfwdg5";
