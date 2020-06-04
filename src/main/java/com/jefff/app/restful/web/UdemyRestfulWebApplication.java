package com.jefff.app.restful.web;

import com.jefff.app.restful.web.ui.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class UdemyRestfulWebApplication
    // Keep the 'extends in if we want to run within external Tomcat.
    extends SpringBootServletInitializer
{

	public static Logger _log = LoggerFactory.getLogger(UdemyRestfulWebApplication.class) ;
    ConfigurableApplicationContext _context;


    // Keep configure() method if we want to run within Tomcat
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(UdemyRestfulWebApplication.class);
    }

    public static void main(String[] args) {
        _log.info(String.format("UdemyRestfulWebApplication.main() invoked, arg size = %d", args.length));
        _log.info(UdemyRestfulWebApplication.class.getName());
        _log.info(UdemyRestfulWebApplication.class.getCanonicalName());
        for(int i=0; i< args.length; i++) {
            System.out.printf("args[%d] = %s\n", i, args[i]);
        }
        ConfigurableApplicationContext context = SpringApplication.run(UdemyRestfulWebApplication.class, args);
        UdemyRestfulWebApplication application = new UdemyRestfulWebApplication(context);
        application.run();
    }

    public UdemyRestfulWebApplication(ConfigurableApplicationContext context) {
        _context = context;
    }

    public void run() {
	    Scanner scanner = new Scanner(System.in);
	    while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("q")) {
                break;
            }
            _log.info("UdemyRestfulWebApplication.run(): still running");
        }
        _log.info("UdemyRestfulWebApplication.run(): done running");
    }
    @Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    _log.info("UdemyRestfulWebApplication.bCryptPasswordEncoder() invoked");
		return new BCryptPasswordEncoder();
	}

	@Bean
    public SpringApplicationContext springApplicationContext() {
        _log.info("UdemyRestfulWebApplication.springApplicationContext() invoked");
	    return new SpringApplicationContext();
    }
}
