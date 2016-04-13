package fr.univlille2.ecm.cover;

import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class Cover extends Document{
	
	private CodingStrategy encoder;
	
	public Cover(CodingStrategy encoder) {
		super();
		this.encoder = encoder;
	}
	
	public Cover addMetadata() {
		this.addAuthor("dsi-admin-ged");
		this.addCreationDate();
		this.addProducer();
		this.addCreator("dsi.univ-lille2.fr");
		this.addTitle("Page d'identification");
		return this;
	}
	
	public Cover addLogo() throws DocumentException, MalformedURLException, IOException{
		Image logo = Image.getInstance("/home/nuxeo/ul2_200.png");
		logo.setAlignment(Image.MIDDLE);
		logo.setSpacingAfter(10);
		this.add(logo);
		LineSeparator ls = new LineSeparator();
		ls.setLineColor(new BaseColor(174, 37, 115));
		ls.setPercentage(33.F);
		ls.setOffset(-10);
		ls.setAlignment(LineSeparator.ALIGN_CENTER);
		this.add(ls);
		return this;
	}
	
	public Cover addTitle() throws DocumentException{
		Font h1 = new Font(FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
		Chunk chunk = new Chunk("Page d'identification", h1);
		Paragraph title = new Paragraph(chunk);
		title.setAlignment(Paragraph.ALIGN_CENTER);
		this.add(title);
		return this;
	}
	
	public Cover addTip() throws DocumentException{
		Font p = new Font(FontFamily.HELVETICA, 12.f, Font.NORMAL, BaseColor.BLACK);
		Chunk chunk = new Chunk(
				"Imprimez cette page d'identification, placez-la au dessus des pages du document à scanner et placez le lot dans le chargeur du scanner",
				p);
		Paragraph description = new Paragraph(chunk);
		description.setAlignment(Paragraph.ALIGN_CENTER);
		this.add(description);
		return this;
	}
	
	public Cover addTargetDescription(String title, String user, String domainName, String path) throws DocumentException{
		float[] t = { 1, 2 };
		Font p = new Font(FontFamily.HELVETICA, 12.f, Font.NORMAL, BaseColor.BLACK);
		PdfPTable table = new PdfPTable(t);
		table.getDefaultCell().setPadding(10);
		table.addCell(new Phrase("Nom de répertoire", p));
		table.addCell(new Phrase(title, p));
		table.addCell(new Phrase("Généré par", p));
		table.addCell(new Phrase(user, p));
		table.addCell(new Phrase("Sous-domaine", p));
		table.addCell(new Phrase(domainName, p));
		table.addCell(new Phrase("Chemin", p));
		table.addCell(new Phrase(path, p));
		table.setWidthPercentage(100);
		this.add(table);
		return this;
	}
	
	public Cover addCode(String sourceString) throws DocumentException, Exception{
        this.add(encoder.encode(sourceString));
		return this;
	}
	
	public Cover addContact() throws DocumentException{
		Font psm = new Font(FontFamily.HELVETICA, 10.f, Font.NORMAL, BaseColor.BLACK);
		Chunk chunk = new Chunk("Pour toute demande d'information sur l'utilisation de ce document merci d'écrire à l'adresse dsi-admin-ged@univ-lille2.fr", psm);
		Paragraph contact = new Paragraph(chunk);
		contact.setAlignment(Paragraph.ALIGN_CENTER);
		this.add(contact);
		return this;
	}
	
	public Cover newLine() throws DocumentException{
		this.add(new Paragraph(Chunk.NEWLINE));
		return this;

	}

}
