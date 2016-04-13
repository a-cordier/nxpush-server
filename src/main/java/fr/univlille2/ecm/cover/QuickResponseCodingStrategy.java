package fr.univlille2.ecm.cover;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;	
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.parser.PdfImageObject;

/**
 * QuickResponseCodingStrategy fournit une implémentation
 * de {@link CodingStrategy} permettant l'utilisation d'un 
 * QrCode pour le codage de l'identifiant
 * @author acordier
 * @date 13 avr. 2016
 */
public class QuickResponseCodingStrategy implements CodingStrategy {

	public QuickResponseCodingStrategy() { }

	@Override
	public String decode(PdfImageObject image) throws Exception{
		QRCodeReader qrCodeReader = new QRCodeReader();
		BufferedImage bufferedImage;
		try {
			bufferedImage = image.getBufferedImage();
		} catch (IOException e) {
			throw new Exception(e);
		}
		BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
		Result result;
			try {
				result = qrCodeReader.decode(binaryBitmap);
			} catch (NotFoundException e) {
				return null;
			} catch (ChecksumException e) {
				return null;
			} catch (FormatException e) {
				return null;
			}
			return result.getText();
		
	}

	@Override
	public Image encode(String source) throws Exception {
        BarcodeQRCode qrcode = new BarcodeQRCode(source.trim(), 1, 1, null);
        Image qrcodeImage;
		try {
			qrcodeImage = qrcode.getImage();
	        qrcodeImage.setSpacingBefore(100);
	        qrcodeImage.scaleToFit(PageSize.A6);
	        qrcodeImage.setAlignment(Image.MIDDLE);
			return qrcodeImage;
		} catch (BadElementException e) {
			throw new Exception(e);
		}

	}

}
