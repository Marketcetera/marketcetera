package org.marketcetera.photon;

import org.marketcetera.client.ClientManager;
import org.marketcetera.trade.UserID;

public class UserNameService {

	public static String getUserName(UserID id) {
		try {
			return ClientManager.getInstance().getUserInfo(id, true).getName();
		} catch (Exception e) {
			// TODO: log 
//			e.printStackTrace();
			return id.toString();
		}
	}
	
	public static String getUserName(String id) {
		try {
			return getUserName(new UserID(Long.valueOf(id)));
		} catch (NumberFormatException e) {
			// TODO: log 
			e.printStackTrace();
			return id;
		}
	}
}
