package com.digigram.digigram_backend.services;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CitizenService {

    private static final String COLLECTION = "citizenProfiles"; // 🔥 IMPORTANT

    public Map<String, Object> getProfile(String uid) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot doc = db.collection(COLLECTION)
                .document(uid)
                .get()
                .get();

        if (!doc.exists()) return null;

        return doc.getData();
    }
}