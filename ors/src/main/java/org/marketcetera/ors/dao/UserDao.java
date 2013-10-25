package org.marketcetera.ors.dao;

import org.marketcetera.ors.security.SimpleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDao
        extends JpaRepository<SimpleUser,Long>
{

	SimpleUser findByName(String user);

	@Modifying	
	@Query("update user u set u.userData=?2 where u.name = ?1")
	void updateUserByName(String name, String userData);

	@Modifying	
	@Query("update user u set u.active=?2 where u.name = ?1")
	SimpleUser updateUserActiveStatus(String opUser, boolean b);

	@Modifying	
	@Query("update user u set u.superUser=?2 where u.name = ?1")
	void updateSuperUser(String opUser, Boolean superuser);

}
