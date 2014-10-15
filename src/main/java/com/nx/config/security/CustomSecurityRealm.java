package com.nx.config.security;

import com.nx.domain.security.User;
import com.nx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
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
        Set<String> permissions = new HashSet<>();
        roles.add("admin");
        permissions.add("/message/*");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user;
        try {
            user = userService.findByName(token.getUsername());
            if (user == null) {
                throw new UnknownAccountException();
            }
        } catch (Exception e) {
            throw new UnknownAccountException();
        }
        if (user != null && user.getPassword().equals(new String(token.getPassword()))) {
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user.getName(), //用户名
                    user.getPassword(), //密码
                    ByteSource.Util.bytes(user.getSalt()),//salt=username+salt
                    getName()  //realm name
            );
            return authenticationInfo;
        } else {
            throw new AuthenticationException("Invalid username/password combination!");
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}