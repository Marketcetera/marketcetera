package org.marketcetera.photon.core;

import java.util.Comparator;

public class GroupIDComparator implements Comparator<MessageHolder> {

	public int compare(MessageHolder mh1, MessageHolder mh2) {
		String id1 = mh1 == null ? null : mh1.getGroupID();
		String id2 = mh2 == null ? null : mh2.getGroupID();
		if (id1 != null && id2 != null){
			return id1.compareTo(id2);
		} else if (id1 == null && id2 == null){
			return 0;
		} else {
			return id1 == null ? -1 : 1;
		}
	}

}
