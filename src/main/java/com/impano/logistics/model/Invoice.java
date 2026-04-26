package com.impano.logistics.model;

import java.time.LocalDateTime;

public class Invoice {
    private String invoiceId;
    private Shipment shipment;
    private double amount;
    private boolean paid;
    private LocalDateTime issuedAt;

    public Invoice(String invoiceId, Shipment shipment, double amount) {
        this.invoiceId = invoiceId;
        this.shipment = shipment;
        this.amount = amount;
        this.paid = false;
        this.issuedAt = LocalDateTime.now();
    }

    public String getInvoiceId() { return invoiceId; }
    public Shipment getShipment() { return shipment; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }
    public LocalDateTime getIssuedAt() { return issuedAt; }

    public void markAsPaid() { this.paid = true; }

    @Override
    public String toString() {
        return String.format("Invoice{id=%s, shipment=%s, amount=%.2f RWF, paid=%s, issued=%s}",
                invoiceId, shipment.getShipmentId(), amount, paid, issuedAt);
    }
}
