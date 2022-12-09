package org.marketcetera.webui.security;

import java.util.Optional;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.admin.dao.UserDao;
import org.marketcetera.admin.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userRepository;
    private final AuthorizationService authorizationService;

    @Autowired
    public UserDetailsServiceImpl(UserDao userRepository,
                                  AuthorizationService inAuthorizationService) {
        this.userRepository = userRepository;
        authorizationService = inAuthorizationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<? extends User> userOption = Optional.ofNullable(userRepository.findByName(username));
        if (!userOption.isPresent()) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            User user = userOption.get();
            return new org.springframework.security.core.userdetails.User(user.getName(),
                                                                          user.getHashedPassword(),
                                                                          getAuthorities(user));
        }
    }

    private Set<? extends GrantedAuthority> getAuthorities(User user) {
       return authorizationService.findAllPermissionsByUsername(user.getName());
    }

}
