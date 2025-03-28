package com.example.Assurance.Service;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
@Service
public class QRCodeGenerator {

  // Method to generate the QR Code URL
  public String generateQRCodeURL(String text) throws IOException {
    int width = 300;  // Set the width of the QR code
    int height = 300; // Set the height of the QR code

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    try {
      BitMatrix bitMatrix = qrCodeWriter.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
      // You can save it to a file or return a URL to the QR Code
      // Saving to file here (you can modify this to return an image URL)
      String filePath = "qr-codes";  // Répertoire dans src/main/resources

      // Using FileSystems to get the path to save the file
      Path path = FileSystems.getDefault().getPath(filePath);
      MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

      return filePath; // Return file path or URL for the image
    } catch (WriterException e) {
      e.printStackTrace();
      throw new IOException("Failed to generate QR Code", e);
    }
  }
}
