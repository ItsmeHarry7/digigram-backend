package com.digigram.digigram_backend.controller;

import com.digigram.digigram_backend.dto.ComplaintDTO;
import com.digigram.digigram_backend.model.Complaint;
import com.digigram.digigram_backend.services.CitizenService;
import com.digigram.digigram_backend.services.ComplaintService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:5173")
public class ComplaintController {

	private final ComplaintService complaintService;
	private final CitizenService citizenService;

	public ComplaintController(ComplaintService complaintService,
	                           CitizenService citizenService) {
	    this.complaintService = complaintService;
	    this.citizenService = citizenService;
	}

    // ================= CREATE =================
//    @PostMapping
//    public ResponseEntity<?> createComplaint(
//            @RequestBody ComplaintDTO dto,
//            jakarta.servlet.http.HttpServletRequest request) {
//
//        String uid = (String) request.getAttribute("uid");
//
//        if (uid == null) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        Complaint complaint = new Complaint();
//
//        // USER
//     // 👤 USER
//        complaint.setCitizenId(uid);
//
//        // ✅ USE DATA FROM FRONTEND
//        complaint.setCitizenName(
//            dto.citizenName != null ? dto.citizenName : "Citizen"
//        );
//
//        complaint.setCitizenPhone(
//            dto.citizenPhone != null ? dto.citizenPhone : "Not Provided"
//        );
//
//        // BASIC
//        complaint.setTitle(dto.title);
//        complaint.setDescription(dto.description);
//
//        // LOCATION (FIXED)
//        if (dto.lat == null || dto.lng == null) {
//            return ResponseEntity.badRequest().body("Location required");
//        }
//
//        complaint.setLatitude(dto.lat);
//        complaint.setLongitude(dto.lng);
//
//        // IMAGE (FIXED ✅)
//        if (dto.images != null && !dto.images.isEmpty()) {
//            complaint.setImageUrl(dto.images); // ✔ FULL LIST
//        }
//
//        // DEFAULT
//        complaint.setStatus("Pending");
//        complaint.setPriority("Medium");
//        complaint.setTimestamp(System.currentTimeMillis());
//
//        Complaint saved = complaintService.createComplaint(complaint);
//
//        return ResponseEntity.ok(saved);
//    }
    @PostMapping
    public ResponseEntity<?> createComplaint(
            @RequestBody ComplaintDTO dto,
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Complaint complaint = new Complaint();

        // ================= USER =================
        complaint.setCitizenId(uid);

        try {
        	Map<String, Object> profile = citizenService.getProfile(uid);

        	if (profile != null) {

        	    Object nameObj = profile.get("name");
        	    Object phoneObj = profile.get("phone");

        	    complaint.setCitizenName(
        	        nameObj != null ? nameObj.toString() : "Citizen"
        	    );

        	    complaint.setCitizenPhone(
        	        phoneObj != null ? phoneObj.toString() : "Not Provided"
        	    );

        	} else {
        	    complaint.setCitizenName("Citizen");
        	    complaint.setCitizenPhone("Not Provided");
        	}
        } catch (Exception e) {
            complaint.setCitizenName("Citizen");
            complaint.setCitizenPhone("Not Provided");
        }

        // ================= BASIC =================
        complaint.setTitle(dto.title);
        complaint.setDescription(dto.description);

        // ================= LOCATION =================
        if (dto.lat == null || dto.lng == null) {
            return ResponseEntity.badRequest().body("Location required");
        }

        complaint.setLatitude(dto.lat);
        complaint.setLongitude(dto.lng);

        // ================= IMAGE =================
        if (dto.images != null && !dto.images.isEmpty()) {
            complaint.setImageUrl(dto.images);
        }

        // ================= AUDIO (NEW ✅) =================
        if (dto.audioUrl != null) {
            complaint.setAudioUrl(dto.audioUrl);
        }

        // ================= DEFAULT =================
        complaint.setStatus("Pending");
        complaint.setTimestamp(System.currentTimeMillis());

        Complaint saved = complaintService.createComplaint(complaint);

        return ResponseEntity.ok(saved);
    }
    
    // ================= GET MY COMPLAINTS =================
    @GetMapping("/my")
    public ResponseEntity<?> getMyComplaints(
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(
                complaintService.getByUserId(uid)
        );
    }

    // ================= ADMIN =================
    @GetMapping("/admin")
    public ResponseEntity<?> getAllComplaints(
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(
                complaintService.getAllSortedByAi()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(
            @PathVariable String id) {

        Complaint complaint = complaintService.getById(id);

        if (complaint == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(complaint);
    }

    // ================= UPDATE STATUS =================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable String id,
            @RequestParam String status,
            jakarta.servlet.http.HttpServletRequest request) {

        String uid = (String) request.getAttribute("uid");

        if (uid == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Complaint updated = complaintService.updateStatus(id, status);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }
}