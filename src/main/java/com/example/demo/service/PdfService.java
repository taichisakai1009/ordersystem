package com.example.demo.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

@Service
public class PdfService {

	// PDFバイト配列を返す
	public byte[] sendByteAsPdf(byte[] fileBytes) {
	    // PDFの生成
	    ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(pdfOutputStream);
	    com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);
	    Document document = new Document(pdfDocument);

	    // QRコード画像をPDFに追加
	    ImageData imageData = ImageDataFactory.create(fileBytes);
	    Image qrCodeImage = new Image(imageData);
	    qrCodeImage.setWidth(200); // サイズを調整
	    qrCodeImage.setHeight(200);
	    document.add(qrCodeImage);
	    document.close();
	    
	    // PDFバイト配列を取得
	    byte[] pdfBytes = pdfOutputStream.toByteArray();
	    
	    return pdfBytes;
	}
}
