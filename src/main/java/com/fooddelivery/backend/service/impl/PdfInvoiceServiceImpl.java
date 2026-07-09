package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Order;
import com.fooddelivery.backend.entity.OrderItem;
import com.fooddelivery.backend.service.PdfInvoiceService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceServiceImpl implements PdfInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(PdfInvoiceServiceImpl.class);

    @Override
    public ByteArrayInputStream generateInvoicePdf(Order order) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Document Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Paragraph title = new Paragraph("DELIVERY INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Invoice Meta details
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Order Details", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("Order Number: " + order.getOrderNumber(), fontBold));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "Now";
            document.add(new Paragraph("Order Date: " + formattedDate, fontRegular));
            document.add(new Paragraph("Restaurant: " + order.getRestaurant().getName(), fontRegular));
            document.add(new Paragraph("Customer Name: " + order.getUser().getName(), fontRegular));
            document.add(new Paragraph("Delivery Address: " + order.getDeliveryAddress(), fontRegular));
            if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                document.add(new Paragraph("Delivery Instructions: " + order.getNotes(), fontRegular));
            }
            
            document.add(new Paragraph(" ")); // Spacing

            // Table of items
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4.0f, 1.5f, 1.0f, 1.5f});

            PdfPCell h1 = new PdfPCell(new Phrase("Food Item", fontBold));
            PdfPCell h2 = new PdfPCell(new Phrase("Price (INR)", fontBold));
            PdfPCell h3 = new PdfPCell(new Phrase("Qty", fontBold));
            PdfPCell h4 = new PdfPCell(new Phrase("Total (INR)", fontBold));

            table.addCell(h1);
            table.addCell(h2);
            table.addCell(h3);
            table.addCell(h4);

            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getFood().getName(), fontRegular));
                table.addCell(new Phrase(String.valueOf(item.getPrice()), fontRegular));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), fontRegular));
                table.addCell(new Phrase(String.valueOf(item.getPrice() * item.getQuantity()), fontRegular));
            }

            document.add(table);
            document.add(new Paragraph(" ")); // Spacing

            // Summary Section
            document.add(new Paragraph("Bill Summary", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Item Subtotal: INR " + order.getSubtotal(), fontRegular));
            document.add(new Paragraph("Taxes & GST (5%): INR " + order.getTaxAmount(), fontRegular));
            document.add(new Paragraph("Delivery Partner Fee: INR " + order.getDeliveryCharge(), fontRegular));
            document.add(new Paragraph("Platform Fee: INR " + order.getPlatformFee(), fontRegular));
            if (order.getDiscountAmount() > 0) {
                document.add(new Paragraph("Promo Discount Applied: -INR " + order.getDiscountAmount(), fontRegular));
            }
            Paragraph totalText = new Paragraph("Grand Total: INR " + order.getTotalAmount(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            totalText.setSpacingBefore(10);
            document.add(totalText);

            document.close();
        } catch (DocumentException ex) {
            logger.error("Error generating invoice PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
