package com.jefff.app.restful.web.service.impl;

import com.jefff.app.restful.web.exceptions.UserServiceException;
import com.jefff.app.restful.web.io.repositories.UserRepository;
import com.jefff.app.restful.web.io.entity.UserEntity;
import com.jefff.app.restful.web.security.AuthenticationFilter;
import com.jefff.app.restful.web.service.UserService;
import com.jefff.app.restful.web.shared.Utils;
import com.jefff.app.restful.web.shared.dto.UserDto;
import com.jefff.app.restful.web.ui.model.response.ErrorMessage;
import com.jefff.app.restful.web.ui.model.response.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Annotate this class as a @Service so that it will be known as a Bean of type UserService
@Service
public class UserServiceImpl implements UserService {

    public static Logger _log = LoggerFactory.getLogger(UserServiceImpl.class) ;

    //    @Autowired
    UserRepository userRepository;
//    @Autowired
    Utils utils;
//    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           Utils utils,
                           BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto src) {

        String email = src.getEmail();
        UserEntity existingEntry = userRepository.findByEmail(email);
        if (existingEntry != null) {
            throw new UserServiceException("Record with email '" + email + "' already exists" );
        }
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(src, userEntity);

        // Need to supply some details which can't be obtained from 'src'
        String encryptedPassword = bCryptPasswordEncoder.encode(src.getPassword());
        userEntity.setEncryptedPassword(encryptedPassword);
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);

        UserEntity storedDetails = userRepository.save(userEntity);
        UserDto res = new UserDto();
        BeanUtils.copyProperties(storedDetails, res);

        return res;
    }


    @Override
    public UserDto getUserByUserId(String id) {
        UserDto res = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        BeanUtils.copyProperties(userEntity, res);
        return res;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        UserDto res = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        
        // Certainly this is debateable but for our updateUser lets say that we only update
        // firstName and lastName.

        String firstName = userDto.getFirstName();
        if (!StringUtils.isEmpty(firstName)) {
            userEntity.setFirstName(firstName);
        }

        String lastName = userDto.getLastName();
        if (!StringUtils.isEmpty(lastName)) {
            userEntity.setLastName(lastName);
        }
        UserEntity updatedEntity =  userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedEntity, res);
        return res;

    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userRepository.delete(userEntity);

    }

    @Override
    public UserDto getUser(String emailAddr) {
        UserEntity userEntity = userRepository.findByEmail(emailAddr);
        if (userEntity == null) {
            throw new UsernameNotFoundException(emailAddr);
        }
        UserDto res = new UserDto();
        BeanUtils.copyProperties(userEntity, res);
        return res;
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<UserEntity> pageData = userRepository.findAll(pageableRequest);

        List<UserDto> res = pageData.stream().map(u -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(u, userDto);
            return userDto;
        }).collect(Collectors.toList());
        return res;
    }

    // Overrides method in SpringBoot's UserDetailsService -
    @Override
    public UserDetails loadUserByUsername(String emailAddr) throws UsernameNotFoundException {
        // This method is going to be used behind the scenes by SpringSecurity. We told Spring to do this in
        // WebSecurity.configure(). In this method, we are going to fetch an encrypted password for an
        // email address provided by a user request. That encrypted password will be compared to the
        // user supplied password to see if the request has the proper credentials.
        //

        UserEntity userEntity = userRepository.findByEmail(emailAddr);
        if (userEntity == null) {
            throw new UsernameNotFoundException(emailAddr);
        }
        String email = userEntity.getEmail();

        User user = new User(email,
                             userEntity.getEncryptedPassword(),
                             new ArrayList<>());

        _log.info("UserServiceImpl.loadUserByUsername() returning User w/email = '{}' and encryptedPw={}",
                  user.getUsername(), user.getPassword());

        return user;

    }
}
