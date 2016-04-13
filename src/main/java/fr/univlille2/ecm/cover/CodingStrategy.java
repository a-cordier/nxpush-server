package fr.univlille2.ecm.cover;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.parser.PdfImageObject;

public interface CodingStrategy {

	public String decode(PdfImageObject image) throws Exception;

	public Image encode(String source) throws Exception;

}
