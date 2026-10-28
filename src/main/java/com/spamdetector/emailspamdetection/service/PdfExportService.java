
package com.spamdetector.emailspamdetection.service;

import com.spamdetector.emailspamdetection.entity.Email;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] exportHistory(List<Email> emails) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Report title
        Font titleFont = new Font(
                Font.FontFamily.HELVETICA,
                18,
                Font.BOLD);

        Font headerFont = new Font(
                Font.FontFamily.HELVETICA,
                11,
                Font.BOLD);

        Paragraph title = new Paragraph(
                "Email Spam Detection Report",
                titleFont);

        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Total Emails: " + emails.size()));
        document.add(new Paragraph(" "));

        // Create the report table
        PdfPTable table = new PdfPTable(4);

        table.setWidthPercentage(100);
        table.setWidths(new float[] { 1, 5, 2, 2 });

        // Table headings
        table.addCell(new Phrase("ID", headerFont));
        table.addCell(new Phrase("Email Content", headerFont));
        table.addCell(new Phrase("Result", headerFont));
        table.addCell(new Phrase("Spam Score", headerFont));

        // Add email records
        for (Email email : emails) {

            // Email ID
            table.addCell(
                    String.valueOf(email.getId()));

            // Email content: handle empty values
            String content = email.getContent();

            if (content == null || content.isBlank()) {
                content = "No email content available";
            }

            table.addCell(content);

            // Detection result
            String result = email.getResult();

            if (result == null || result.isBlank()) {
                result = "Not available";
            }

            table.addCell(result);

            // Spam score
            table.addCell(
                    String.valueOf(email.getSpamScore()));
        }

        document.add(table);

        document.close();

        return outputStream.toByteArray();
    }
}
