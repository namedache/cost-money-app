package com.example.costmoneyapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void registerAndGetToken() throws Exception {
        String username = "rectest_" + System.currentTimeMillis();
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username, "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void createRecordSuccess() throws Exception {
        mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "amount", 25.5,
                                "type", "expense",
                                "date", LocalDate.now().toString(),
                                "note", "午餐"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(25.5))
                .andExpect(jsonPath("$.type").value("expense"))
                .andExpect(jsonPath("$.note").value("午餐"));
    }

    @Test
    void createRecordInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "amount", -10,
                                "type", "expense",
                                "date", LocalDate.now().toString()
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRecordInvalidType() throws Exception {
        mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "amount", 10,
                                "type", "invalid",
                                "date", LocalDate.now().toString()
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listRecords() throws Exception {
        // Create a record first
        mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "amount", 30,
                                "type", "expense",
                                "date", LocalDate.now().toString()
                        ))))
                .andExpect(status().isOk());

        // List records
        mockMvc.perform(get("/api/records")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void accessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/records"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteRecord() throws Exception {
        // Create a record
        MvcResult result = mockMvc.perform(post("/api/records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "amount", 50,
                                "type", "income",
                                "date", LocalDate.now().toString()
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Long recordId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // Delete it
        mockMvc.perform(delete("/api/records/" + recordId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Verify list is empty
        mockMvc.perform(get("/api/records")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
