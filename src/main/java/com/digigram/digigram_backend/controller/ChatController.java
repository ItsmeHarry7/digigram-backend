package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.services.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {

        String message = body.getOrDefault("message", "");

        String reply = geminiService.chat(message);

        return Map.of("reply", reply);
    }
}