package com.nx.service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.nx.domain.security.User;
import com.nx.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Neal on 10/12 012.
 */
@Service
public class UserService {
    private UserRepository userRepository;

    @Cacheable(cacheName = "userCache",keyGenerator =@KeyGenerator(name = "#{name}") )
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
