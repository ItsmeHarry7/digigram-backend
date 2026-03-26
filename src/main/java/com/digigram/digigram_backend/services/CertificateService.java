package com.digigram.digigram_backend.services;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.time.Instant;

@Service
public class CertificateService {

    private static final String COLLECTION = "certificates";

    public String createCertificate(Map<String, Object> data) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        data.put("status", "Pending");
        data.put("verificationCode", null);
        data.put("createdAt", new Date());
        data.put("approvedAt", null);

        DocumentReference ref = db.collection(COLLECTION).document();
        ref.set(data);

        return ref.getId();
    }

    public List<Map<String, Object>> getByUser(String uid)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(COLLECTION)
                .whereEqualTo("uid", uid);

        List<QueryDocumentSnapshot> docs =
                query.get().get().getDocuments();

        List<Map<String, Object>> result = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            Map<String, Object> map = doc.getData();
            map.put("id", doc.getId());
            result.add(map);
        }

        return result;
    }

    public void approve(String id) throws Exception {

        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot doc =
                db.collection(COLLECTION).document(id).get().get();

        if (!doc.exists()) return;

        String uid = doc.getString("uid");
        String type = doc.getString("type");
        String reason = doc.getString("reason");

        String raw = uid + type + reason + Instant.now().toString();

        String digitalSignature = DigestUtils.sha256Hex(raw);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Approved");
        updates.put("approvedAt", Instant.now());
        updates.put("verificationCode", digitalSignature);

        db.collection(COLLECTION).document(id).update(updates);
    }
    public void reject(String id) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        db.collection(COLLECTION)
                .document(id)
                .update("status", "Rejected");
    }

    public Map<String, Object> verify(String code)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Query query = db.collection(COLLECTION)
                .whereEqualTo("verificationCode", code);

        List<QueryDocumentSnapshot> docs =
                query.get().get().getDocuments();

        if (docs.isEmpty()) return null;

        Map<String, Object> map = docs.get(0).getData();
        map.put("id", docs.get(0).getId());
        return map;
    }
    public List<Map<String, Object>> getAll()
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        List<QueryDocumentSnapshot> docs =
                db.collection(COLLECTION)
                  .orderBy("createdAt", Query.Direction.DESCENDING)
                  .get().get().getDocuments();

        List<Map<String, Object>> result = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            Map<String, Object> map = doc.getData();
            map.put("id", doc.getId());
            result.add(map);
        }

        return result;
    }

	public void rejectWithReason(String id, String reason) throws Exception {

    Firestore db = FirestoreClient.getFirestore();

    db.collection("certificates")
        .document(id)
        .update(
            "status", "Rejected",
            "adminReason", reason
        );
}
}