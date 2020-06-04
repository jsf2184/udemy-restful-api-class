package com.jefff.app.restful.web.ui.controller;

import com.jefff.app.restful.web.exceptions.UserServiceException;
import com.jefff.app.restful.web.service.UserService;
import com.jefff.app.restful.web.shared.dto.UserDto;
import com.jefff.app.restful.web.ui.model.request.UserDetailsRequestModel;
import com.jefff.app.restful.web.ui.model.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    public static Logger _log = LoggerFactory.getLogger(UserController.class) ;

    // the returns from these methods will be sent back to the http client.
    UserService userService;

    public UserController(UserService userService) {
        _log.info("UserController entry at info level");
        this.userService = userService;
    }


    @GetMapping(
            path="/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            )
    public UserRest getUser(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        _log.info(String.format("UserController.getUser(): id = %s, principal = %s", id, principal));

        UserRest res = new UserRest();
        UserDto userDto =  userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, res);
        return res;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "0") int page,
                                   @RequestParam(value="limit", defaultValue = "25") int limit)
    {
        List<UserDto> userDtos =  userService.getUsers(page, limit);
        List<UserRest> res = userDtos.stream().map(u -> {
            UserRest userRest = new UserRest();
            BeanUtils.copyProperties(u, userRest);
            return userRest;
        }).collect(Collectors.toList());

        return res;
    }


    // Indicate that Post can take or output json or xml
    @PostMapping(
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        _log.info("UserController.createUser(): entry");
        String missingField = checkMissingFields(userDetails);
        if (missingField != null) {
            _log.warn(String.format("UserController.createUser(): throw exception due to missing field: %s", missingField));
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD);
        }
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto createdDto = userService.createUser(userDto);
        BeanUtils.copyProperties(createdDto, returnValue);
        return returnValue;

    }

    public String checkMissingFields(UserDetailsRequestModel userDetails) {
        if (StringUtils.isEmpty(userDetails.getEmail())) {
            return "email";
        }
        if (StringUtils.isEmpty(userDetails.getFirstName())) {
            return "firstName";
        }
        if (StringUtils.isEmpty(userDetails.getLastName())) {
            return "lastName";
        }
        if (StringUtils.isEmpty(userDetails.getPassword())) {
            return "password";
        }
        // no missing fields
        return null;

    }
    // Indicate that Put can take or output json or xml
    @PutMapping(path="/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails,
                               @PathVariable String id)
    {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);
        return returnValue;
    }

    @DeleteMapping(
            path="/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String id) {
        _log.info(String.format("UserController.deleteUser(): id = %s", id));
        userService.deleteUser(id);
        OperationStatusModel res = new OperationStatusModel(RequestOperation.DELETE,
                                                            RequestOperationStatus.Success);
        return res;
    }


}
