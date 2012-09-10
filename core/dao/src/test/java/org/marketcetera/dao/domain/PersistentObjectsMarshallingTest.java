package org.marketcetera.dao.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionAttribute;
import org.marketcetera.api.security.User;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests marshalling and unmarshalling of persistence objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentObjectsMarshallingTest
{
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void testMarshalling()
            throws Exception
    {
        PersistentUser user1 = generateUser();
        PersistentUser user2 = generateUser();
        PersistentUser user3 = generateUser();
        PersistentPermission permission1 = generatePermission();
        PersistentPermission permission2 = generatePermission();
        PersistentPermission permission3 = generatePermission();
        PersistentRole role1 = generateRole();
        PersistentRole role2 = generateRole();
        PersistentRole role3 = generateRole();
        role1.setPermissions(new HashSet<Permission>(Arrays.asList(permission1,permission2,permission3)));
        role2.setPermissions(new HashSet<Permission>(Arrays.asList(permission1,permission2,permission3)));
        role3.setPermissions(new HashSet<Permission>(Arrays.asList(permission1,permission2,permission3)));
        role1.setUsers(new HashSet<User>(Arrays.asList(user1,user2,user3)));
        role2.setUsers(new HashSet<User>(Arrays.asList(user1,user2,user3)));
        role3.setUsers(new HashSet<User>(Arrays.asList(user1,user2,user3)));
        user1.setPermissions(Arrays.asList(new Permission[] { permission1, permission2, permission3 }));
        user2.setPermissions(Arrays.asList(new Permission[] { permission1, permission2, permission3 }));
        user3.setPermissions(Arrays.asList(new Permission[] { permission1, permission2, permission3 }));
        JAXBContext context = JAXBContext.newInstance(PersistentUser.class,PersistentRole.class,PersistentPermission.class,SystemObjectList.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        SystemObjectList systemObjectList = new SystemObjectList();
        systemObjectList.addObject(role1);
        systemObjectList.addObject(user1);
        systemObjectList.addObject(permission1);
        marshaller.marshal(systemObjectList,
                           writer);
        SLF4JLoggerProxy.debug(this,
                               writer.getBuffer().toString());
        Unmarshaller m = context.createUnmarshaller();
        Object o = m.unmarshal(new InputStreamReader(new ByteArrayInputStream(writer.getBuffer().toString().getBytes())));
        SLF4JLoggerProxy.debug(this,
                               "Umarshalled to: {}",
                               o);
    }
    private PersistentUser generateUser()
    {
        PersistentUser user = new PersistentUser();
        user.setAccountNonExpired(false);
        user.setAccountNonLocked(false);
        user.setCredentialsNonExpired(false);
        user.setEnabled(false);
        user.setId(counter.incrementAndGet());
        user.setPassword("password-" + counter.incrementAndGet());
        user.setUsername("username-" + counter.incrementAndGet());
        user.setVersion(counter.intValue());
        return user;
    }
    private PersistentPermission generatePermission()
    {
        PersistentPermission permission = new PersistentPermission();
        permission.setId(counter.incrementAndGet());
        permission.setName("very lax-" + counter.incrementAndGet());
        permission.setDescription("description-" + counter.incrementAndGet());
        permission.setMethod(EnumSet.of(PermissionAttribute.Create,PermissionAttribute.Update,PermissionAttribute.Read,PermissionAttribute.Delete));
        permission.setVersion(counter.intValue());
        return permission;
    }
    private PersistentRole generateRole()
    {
        PersistentRole role = new PersistentRole();
        role.setId(counter.incrementAndGet());
        role.setName("name-" + counter.incrementAndGet());
        role.setVersion(counter.intValue());
        return role;
    }
    private AtomicLong counter = new AtomicLong(0);
}
