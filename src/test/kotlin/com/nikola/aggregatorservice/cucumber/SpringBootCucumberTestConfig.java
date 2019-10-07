package com.nikola.aggregatorservice.cucumber;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nikola.aggregatorservice.config"})
public class SpringBootCucumberTestConfig {

}
