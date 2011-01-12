INSERT INTO ors_users (id,updateCount,lastUpdated,name,description,hashedPassword,active,superuser)
VALUES (1,0,now(),"admin",NULL,"6anqbgybi82pveayzrkt3egjkwfwdg5",1,1) ON DUPLICATE KEY
UPDATE hashedPassword = "6anqbgybi82pveayzrkt3egjkwfwdg5";
