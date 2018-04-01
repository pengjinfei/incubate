package com.pengjinfei.incubate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 4/1/18
 *®®
 * @author Pengjinfei
 */
@SpringBootApplication
@RestController
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @GetMapping("/{id}")
    public String test(@PathVariable("id") String id) {
        log.info("test sleuth");
        return id;
    }
}
