package com.marketcetera.colin.app.security;

import com.marketcetera.colin.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
