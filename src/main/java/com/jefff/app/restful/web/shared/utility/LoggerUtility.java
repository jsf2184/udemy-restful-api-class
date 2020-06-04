package com.jefff.app.restful.web.shared.utility;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtility {
    public static Logger getLogger(Class<?> clazz) {
        Logger res =  (Logger) LoggerFactory.getLogger(clazz);
        res.setLevel(Level.INFO);
        return res;
    }
}
