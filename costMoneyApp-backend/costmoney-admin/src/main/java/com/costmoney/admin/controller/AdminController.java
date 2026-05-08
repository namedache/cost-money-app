package com.costmoney.admin.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import java.util.*;

@RestController
@RequestMapping("/admin/api")
public class AdminController {
    private final RestClient authClient;
    private final RestClient ledgerClient;

    public AdminController() {
        this.authClient = RestClient.builder().baseUrl("http://localhost:8081").build();
        this.ledgerClient = RestClient.builder().baseUrl("http://localhost:8082").build();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            List users = authClient.get().uri("/internal/users").retrieve().body(List.class);
            result.put("totalUsers", users != null ? users.size() : 0);
        } catch (Exception e) { result.put("totalUsers", "unavailable"); }
        return result;
    }

    @GetMapping("/users")
    public List users() {
        return authClient.get().uri("/internal/users").retrieve().body(List.class);
    }

    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        authClient.delete().uri("/internal/users/{id}", id).retrieve();
        return Map.of("message", "删除成功");
    }

    @GetMapping("/services")
    public Map<String, String> services() {
        Map<String, String> status = new LinkedHashMap<>();
        for (String[] svc : new String[][]{{"auth", "8081"}, {"ledger", "8082"}, {"reference", "8083"}, {"budget", "8084"}}) {
            try {
                RestClient.builder().baseUrl("http://localhost:" + svc[1]).build().get().uri("/actuator/health").retrieve().toEntity(String.class);
                status.put(svc[0], "UP");
            } catch (Exception e) { status.put(svc[0], "DOWN"); }
        }
        return status;
    }
}
