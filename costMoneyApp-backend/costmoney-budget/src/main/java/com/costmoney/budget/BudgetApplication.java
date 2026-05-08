package com.costmoney.budget;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.costmoney.budget.entity")
@EnableJpaRepositories(basePackages = "com.costmoney.budget.repository")
public class BudgetApplication {
    public static void main(String[] args) { SpringApplication.run(BudgetApplication.class, args); }
}
