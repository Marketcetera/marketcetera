insert into ors_users (id,updateCount,lastUpdated,name,description,hashedPassword)
values (1,0,now(),"admin",null,"6anqbgybi82pveayzrkt3egjkwfwdg5") on duplicate key
update hashedPassword = "6anqbgybi82pveayzrkt3egjkwfwdg5";