package org.marketcetera.photon;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.client.ClientManager;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities for translating user ids to user names.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class UserNameService {

	/**
	 * Returns the user name for the given UserID.
	 * 
	 * @param id
	 *            the user id
	 * @return the user name, or id.toString() if the name cannot be determined
	 */
	public static String getUserName(UserID id) {
		try {
            String name = usernamesById.get(id);
            if(name == null) {
                name = ClientManager.getInstance().getUserInfo(id, true).getName();
                usernamesById.put(id,
                                  name);
            }
            return name;
		} catch (Exception e) {
			Messages.USER_NAME_SERVICE_LOOKUP_FAILED.error(UserNameService.class, e, id);
			return id.toString();
		}
	}

	/**
	 * Returns the user name for the given id string.
	 * 
	 * @param id
	 *            the user id string
	 * @return the user name, or id if the name cannot be determined
	 */
	public static String getUserName(String id) {
		try {
			return getUserName(new UserID(Long.valueOf(id)));
		} catch (NumberFormatException e) {
			Messages.USER_NAME_SERVICE_LOOKUP_FAILED.error(UserNameService.class, e, id);
			return id;
		}
	}
    /**
     * caches user names by id
     */
    private static final Map<UserID,String> usernamesById = new HashMap<>();
}
