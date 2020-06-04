package com.jefff.app.restful.web;


import com.jefff.app.restful.web.shared.dto.UserDto;
import com.jefff.app.restful.web.shared.utility.LoggerUtility;
import com.jefff.app.restful.web.ui.model.request.UserDetailsRequestModel;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.stream.IntStream;

public class DummyTest {


    public static Logger logger = LoggerFactory.getLogger(DummyTest.class) ;
//    public static Logger logger = LoggerUtility.getLogger(DummyTest.class) ;


    @Test
    public void shouldPass() {
        Class<DummyTest> dummyTestClass = DummyTest.class;
        logger.info("hello world info");
        logger.debug("hello world debug");
    }
    
    public static UserDetailsRequestModel createRequest() {
        UserDetailsRequestModel res = new UserDetailsRequestModel();
        res.setEmail("henrydingbat@wideopenwest.com");
        res.setFirstName("henry");
        res.setLastName("dingbat");
        res.setPassword("dingbatitus");
        return res;
    }
    
    public static UserDto toDto(UserDetailsRequestModel request) {
        UserDto res = new UserDto();
        res.setEmail(request.getEmail());
        res.setFirstName(request.getFirstName());
        res.setLastName(request.getLastName());
        res.setPassword(request.getPassword());
        return res;
    }
    
    @Test
    public void compareSpeecs() {
        int n = 100000;
        UserDetailsRequestModel request = createRequest();
        DateTime manualStart = DateTime.now();
        IntStream.range(1, n).forEach((i) -> toDto(request));
        DateTime manualFinish = DateTime.now();

        DateTime bootStart = DateTime.now();
        IntStream.range(1, n).forEach((i) -> {
            UserDto res = new UserDto();
            BeanUtils.copyProperties(request, res);
        });
        DateTime bootFinish = DateTime.now();
        
        logger.info(String.format("Manual start - finish = %d", manualFinish.getMillis() - manualStart.getMillis()));
        logger.info(String.format("Boot start - finish = %d", bootFinish.getMillis() - bootStart.getMillis()));
        Class<? extends Logger> loggerClass = logger.getClass();
        logger.info(String.format("logger canoncial name: %s", loggerClass.getCanonicalName()));
        logger.info(String.format("logger simple name: %s", loggerClass.getSimpleName()));
        logger.info(String.format("logger type name: %s", loggerClass.getTypeName()));

    }
    
    
}
