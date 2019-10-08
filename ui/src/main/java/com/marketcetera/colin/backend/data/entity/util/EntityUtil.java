package com.marketcetera.colin.backend.data.entity.util;

import com.marketcetera.colin.backend.data.entity.AbstractEntity;

public final class EntityUtil {

	public static final String getName(Class<? extends AbstractEntity> type) {
		// All main entities have simple one word names, so this is sufficient. Metadata
		// could be added to the class if necessary.
		return type.getSimpleName();
	}
}
