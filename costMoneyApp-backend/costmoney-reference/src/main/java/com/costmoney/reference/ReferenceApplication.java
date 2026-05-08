package com.costmoney.reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.costmoney.reference.entity")
@EnableJpaRepositories(basePackages = "com.costmoney.reference.repository")
public class ReferenceApplication {
    public static void main(String[] args) { SpringApplication.run(ReferenceApplication.class, args); }
}
