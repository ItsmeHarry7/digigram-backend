package com.digigram.digigram_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() {
        try {

            InputStream serviceAccount;

            String firebaseConfig = System.getenv("FIREBASE_CONFIG_JSON");

            if (firebaseConfig != null && !firebaseConfig.isEmpty()) {
                // ✅ PRODUCTION (Render)
                serviceAccount = new ByteArrayInputStream(
                        firebaseConfig.getBytes(StandardCharsets.UTF_8)
                );
                System.out.println("🔥 Using ENV Firebase config");
            } else {
                // ✅ LOCAL FALLBACK
                serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                System.out.println("🔥 Using LOCAL Firebase file");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized SUCCESS");
            }

        } catch (Exception e) {
            System.out.println("❌ FIREBASE INIT FAILED");
            e.printStackTrace();
        }
    }
}