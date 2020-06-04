package com.jefff.app.restful.web.shared.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Autowired
    private Environment _environment;

    public String getTokenSecret() {
        String res = _environment.getProperty("tokenSecret");
        return res;
    }
}
