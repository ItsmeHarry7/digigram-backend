package com.digigram.digigram_backend.model;


import java.util.Date;

public class TaxPayment {

    private String id;
    private String citizenUid;
    private String citizenName;
    private String citizenPhone;
    private String taxId;
    private String taxTitle;
    private String taxType;
    private Double amount;
    private String proofUrl;
    private String transactionId;
    private String status;
    private String remarks;
    private Date paymentDate;
    private Date submittedAt;
    private Date approvedAt;
    private String rejectionReason;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCitizenUid() {
		return citizenUid;
	}
	public void setCitizenUid(String citizenUid) {
		this.citizenUid = citizenUid;
	}
	public String getCitizenPhone() {
		return citizenPhone;
	}
	public void setCitizenPhone(String citizenPhone) {
		this.citizenPhone = citizenPhone;
	}
	public String getCitizenName() {
		return citizenName;
	}
	public void setCitizenName(String citizenName) {
		this.citizenName = citizenName;
	}
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public String getTaxTitle() {
		return taxTitle;
	}
	public void setTaxTitle(String taxTitle) {
		this.taxTitle = taxTitle;
	}
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getProofUrl() {
		return proofUrl;
	}
	public void setProofUrl(String proofUrl) {
		this.proofUrl = proofUrl;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Date getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(Date submittedAt) {
		this.submittedAt = submittedAt;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public Date getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(Date approvedAt) {
		this.approvedAt = approvedAt;
	}
	public String getRejectionReason() {
		return rejectionReason;
	}
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
}