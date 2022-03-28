package com.example.application.data.generator;

import com.example.application.data.entity.User;
import com.example.application.data.entity.Role;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.application.data.service.PersonRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PersonRepository personRepository, UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (personRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            User user = new User("user", "u", Role.USER);
            user.setActive(true);
            userRepository.save(user);

            logger.info("Generated demo data");
        };
    }

}