package com.digigram.digigram_backend.services;


import com.digigram.digigram_backend.model.Tax;
import com.digigram.digigram_backend.model.TaxPayment;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaxService {

    private final Firestore db = FirestoreClient.getFirestore();

    /* ================= ADD TAX ================= */
    public Tax addTax(Tax tax) throws Exception {
        tax.setCreatedAt(new Date());
        tax.setIsActive(true);

        DocumentReference doc = db.collection("taxes").document();
        tax.setId(doc.getId());

        doc.set(tax);
        return tax;
    }

    /* ================= GET ALL TAX ================= */
    public List<Tax> getAllTaxes() throws Exception {
        List<Tax> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = db.collection("taxes").get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            Tax t = doc.toObject(Tax.class);
            list.add(t);
        }

        return list;
    }

    /* ================= UPDATE ================= */
    public Tax updateTax(String id, Tax tax) throws Exception {
        tax.setId(id);
        db.collection("taxes").document(id).set(tax);
        return tax;
    }

    /* ================= DELETE ================= */
    public void deleteTax(String id) throws Exception {
        db.collection("taxes").document(id).delete();
    }

    /* ================= ACTIVE TAX ================= */
    public List<Tax> getActiveTaxes() throws Exception {
        List<Tax> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> future =
                db.collection("taxes")
                        .whereEqualTo("isActive", true)
                        .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(Tax.class));
        }

        return list;
    }

    /* ================= SUBMIT PAYMENT ================= */
    public TaxPayment submitPayment(TaxPayment payment) throws Exception {

        if(payment.getCitizenUid() == null || payment.getCitizenUid().isEmpty()){
            throw new RuntimeException("Citizen UID missing");
        }

        payment.setStatus("pending");
        payment.setSubmittedAt(new Date());

        DocumentReference doc = db.collection("taxPayments").document();
        payment.setId(doc.getId());

        doc.set(payment);

        return payment;
    }
    /* ================= GET PAYMENTS ================= */
    public List<TaxPayment> getAllPayments() throws Exception {
        List<TaxPayment> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> future =
                db.collection("taxPayments").get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(TaxPayment.class));
        }

        return list;
    }

    /* ================= MY PAYMENTS ================= */
    public List<TaxPayment> getPaymentsByCitizen(String uid) throws Exception {
        List<TaxPayment> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> future =
                db.collection("taxPayments")
                        .whereEqualTo("citizenUid", uid)
                        .get();

        for (DocumentSnapshot doc : future.get().getDocuments()) {
            list.add(doc.toObject(TaxPayment.class));
        }

        return list;
    }

    /* ================= APPROVE ================= */
    public TaxPayment approvePayment(String id) throws Exception {
        DocumentReference ref = db.collection("taxPayments").document(id);

        ref.update("status", "approved", "approvedAt", new Date());

        return ref.get().get().toObject(TaxPayment.class);
    }

    /* ================= REJECT ================= */
    public TaxPayment rejectPayment(String id, String reason) throws Exception {
        DocumentReference ref = db.collection("taxPayments").document(id);

        ref.update("status", "rejected", "rejectionReason", reason);

        return ref.get().get().toObject(TaxPayment.class);
    }

    /* ================= ANALYTICS ================= */
    public Map<String, Object> getAnalytics() throws Exception {

        List<TaxPayment> all = getAllPayments();

        long total = all.size();
        long pending = all.stream().filter(p -> "pending".equals(p.getStatus())).count();
        long approved = all.stream().filter(p -> "approved".equals(p.getStatus())).count();
        long rejected = all.stream().filter(p -> "rejected".equals(p.getStatus())).count();

        double totalAmount = all.stream()
                .filter(p -> "approved".equals(p.getStatus()))
                .mapToDouble(TaxPayment::getAmount)
                .sum();

        Map<String, Object> map = new HashMap<>();
        map.put("totalPayments", total);
        map.put("pending", pending);
        map.put("approved", approved);
        map.put("rejected", rejected);
        map.put("totalAmount", totalAmount);

        return map;
    }
}