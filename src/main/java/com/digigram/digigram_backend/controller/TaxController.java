package com.digigram.digigram_backend.controller;

import org.springframework.web.bind.annotation.*;

import com.digigram.digigram_backend.model.Tax;
import com.digigram.digigram_backend.model.TaxPayment;
import com.digigram.digigram_backend.services.TaxService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin("*")
public class TaxController {

    private final TaxService service;

    public TaxController(TaxService service) {
        this.service = service;
    }

    /* ================= ADMIN ================= */

    @PostMapping("/add")
    public Tax addTax(@RequestBody Tax tax) throws Exception {
        return service.addTax(tax);
    }

    @GetMapping("/all")
    public List<Tax> getAll() throws Exception {
        return service.getAllTaxes();
    }

    @PutMapping("/update/{id}")
    public Tax update(@PathVariable String id, @RequestBody Tax tax) throws Exception {
        return service.updateTax(id, tax);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) throws Exception {
        service.deleteTax(id);
    }

    /* ================= CITIZEN ================= */

    @GetMapping("/active")
    public List<Tax> getActive() throws Exception {
        return service.getActiveTaxes();
    }

    /* ================= PAYMENT ================= */

    // ✅ FIXED (match frontend)
    @PostMapping("/pay")
    public TaxPayment pay(@RequestBody TaxPayment payment) throws Exception {

        if (payment.getCitizenUid() == null || payment.getCitizenUid().isEmpty()) {
            throw new RuntimeException("Citizen UID missing");
        }

        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        return service.submitPayment(payment);
    }

    @GetMapping("/payments")
    public List<TaxPayment> allPayments() throws Exception {
        return service.getAllPayments();
    }

    @GetMapping("/payments/{uid}")
    public List<TaxPayment> myPayments(@PathVariable String uid) throws Exception {
        return service.getPaymentsByCitizen(uid);
    }

    /* ================= APPROVAL ================= */

    @PutMapping("/approve/{id}")
    public TaxPayment approve(@PathVariable String id) throws Exception {
        return service.approvePayment(id);
    }

    // ✅ FIXED (dynamic reason)
    @PutMapping("/reject/{id}")
    public TaxPayment reject(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) throws Exception {

        String reason = body.getOrDefault("reason", "Rejected");

        return service.rejectPayment(id, reason);
    }

    /* ================= ANALYTICS ================= */

    @GetMapping("/analytics")
    public Object analytics() throws Exception {
        return service.getAnalytics();
    }
}