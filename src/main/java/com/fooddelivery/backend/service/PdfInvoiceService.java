package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Order;
import java.io.ByteArrayInputStream;

public interface PdfInvoiceService {
    ByteArrayInputStream generateInvoicePdf(Order order);
}
