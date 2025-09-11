package com.github.vakho10;

import com.github.vakho10.jcef.MainFrame;
import com.github.vakho10.util.AppPortFinder;
import lombok.extern.slf4j.Slf4j;
import org.cef.CefApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class JlectronApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        // ensure AWT is allowed to use a display
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(JlectronApplication.class, args);
    }

    @Bean
    CommandLineRunner prodRunner(AppPortFinder portFinder) {
        return args -> {
            // Perform startup initialization on platforms that require it.
            if (!CefApp.startup(args)) {
                log.error("Startup initialization failed!");
                return;
            }
            new MainFrame(portFinder.getPort(), env.matchesProfiles("prod"));
        };
    }
}
