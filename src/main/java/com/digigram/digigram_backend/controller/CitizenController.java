package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.services.CitizenService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/citizen")
@CrossOrigin("*")
public class CitizenController {

    private final CitizenService citizenService;

    public CitizenController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    /* ================= PROFILE ================= */

    @GetMapping("/profile/{uid}")
    public Map<String, Object> getProfile(@PathVariable String uid) throws Exception {

        Map<String, Object> profile = citizenService.getProfile(uid);

        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }

        return profile;
    }
}