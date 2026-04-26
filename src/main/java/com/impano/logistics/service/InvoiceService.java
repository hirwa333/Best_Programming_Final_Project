package com.impano.logistics.service;

import com.impano.logistics.model.Invoice;
import com.impano.logistics.repository.InvoiceRepository;

import java.util.List;
import java.util.Optional;

public class InvoiceService {
    private final InvoiceRepository invoiceRepo;

    public InvoiceService(InvoiceRepository invoiceRepo) {
        this.invoiceRepo = invoiceRepo;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    public boolean payInvoice(String invoiceId) {
        Optional<Invoice> opt = invoiceRepo.findById(invoiceId);
        if (opt.isEmpty()) return false;
        opt.get().markAsPaid();
        return true;
    }

    public Optional<Invoice> findByShipmentId(String shipmentId) {
        return invoiceRepo.findByShipmentId(shipmentId);
    }
}
