package com.nx.config.security;

import com.nx.domain.security.User;
import com.nx.repositories.UserRepository;
import com.nx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by neal.xu on 2014/10/10.
 */
public class CustomSecurityRealm extends AuthorizingRealm {
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        Set roles = new HashSet<>();
        Set permissions = new HashSet<>();
        roles.add("admin");
        permissions.add("/message/*");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setObjectPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        User user = null;
        try {
            user = userService.findByName(token.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user != null && user.getPassword().equals(new String(token.getPassword()))) {
            return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
        } else {
            throw new AuthenticationException("Invalid username/password combination!");
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}