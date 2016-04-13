package fr.univlille2.ecm.cover;

import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.parser.PdfImageObject;

/**
 * CodingStrategy: Type d'encodage utilisé pour l'id du document
 * (eg QrCode, Code128, EAN ...)
 * @author acordier
 * @date 13 avr. 2016
 */
public interface CodingStrategy {
	
	/**
	 * Fournit une chaîne de caractères à partir d'une image extraite d'un pdf
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public String decode(PdfImageObject image) throws Exception;
	/**
	 * Fournit une représentation codée à partir d'une chaîne de charactères
	 * La représentation codée implémente l'interface {@link Element}
	 * et peut donc être ajoutée à un pdf avec itext
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public Image encode(String source) throws Exception;

}
