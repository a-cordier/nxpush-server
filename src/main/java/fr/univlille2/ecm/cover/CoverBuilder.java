package fr.univlille2.ecm.cover;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Création d'une page de couverture 
 * contenant la représentation codée
 * de l'identifiant du document référençant 
 * le conteneur courant depuis lequel est déclenché
 * l'appel à CoverBuilder
 * @author acordier
 * @date 13 avr. 2016
 */
public class CoverBuilder {

	private String docTitle, docPath, user, domainName, docId;
	private static final Log logger = LogFactory.getLog(CoverBuilder.class);


	public CoverBuilder(CoreSession session, DocumentModel doc) throws ClientException {
		try {
			DocumentModel parent = session.getParentDocument(doc.getRef());
			while (parent != null && !"SousDomaine".equals(parent.getType())) {
				parent = session.getParentDocument(parent.getRef());
			}
			domainName = parent == null ? "Indéterminé" : parent.getTitle();
			docPath = doc.getPathAsString();
			NuxeoPrincipal nxPrincipal = ((NuxeoPrincipal) session.getPrincipal());
			user = String.format("%s %s", nxPrincipal.getFirstName(), nxPrincipal.getLastName());
			docTitle = doc.getTitle();
			docId = doc.getId();
		} catch (ClientException e) {
			logger.error(e);
		}
	}
	

	public Blob build(CodingStrategy encoder) {
		Cover doc = new Cover(encoder);
		PdfWriter docWriter = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			docWriter = PdfWriter.getInstance(doc, bos);
			doc.addMetadata();
			doc.setPageSize(PageSize.A4);
			doc.open();
			doc.addLogo().newLine();
			doc.addTitle().newLine();
			doc.addTip().newLine().newLine();
	        doc.addCode(docId);
			doc.addTargetDescription(docTitle, user, domainName, docPath).newLine();
			doc.addContact();		
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != doc)
				doc.close();
			if (null != docWriter)
				docWriter.close();
		}
		return new ByteArrayBlob(bos.toByteArray());

	}
	
	private class Cover extends Document{
		
		private CodingStrategy encoder;
		
		Cover(CodingStrategy encoder) {
			super();
			this.encoder = encoder;
		}
		
		Cover addMetadata() {
			this.addAuthor("dsi-admin-ged");
			this.addCreationDate();
			this.addProducer();
			this.addCreator("dsi.univ-lille2.fr");
			this.addTitle("Page d'identification");
			return this;
		}
		
		Cover addLogo() throws DocumentException, MalformedURLException, IOException{
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
		
		Cover addTitle() throws DocumentException{
			Font h1 = new Font(FontFamily.HELVETICA, 25.0f, Font.BOLD, BaseColor.BLACK);
			Chunk chunk = new Chunk("Page d'identification", h1);
			Paragraph title = new Paragraph(chunk);
			title.setAlignment(Paragraph.ALIGN_CENTER);
			this.add(title);
			return this;
		}
		
		Cover addTip() throws DocumentException{
			Font p = new Font(FontFamily.HELVETICA, 12.f, Font.NORMAL, BaseColor.BLACK);
			Chunk chunk = new Chunk(
					"Imprimez cette page d'identification, placez-la au dessus des pages du document à scanner et placez le lot dans le chargeur du scanner",
					p);
			Paragraph description = new Paragraph(chunk);
			description.setAlignment(Paragraph.ALIGN_CENTER);
			this.add(description);
			return this;
		}
		
		Cover addTargetDescription(String title, String user, String domainName, String path) throws DocumentException{
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
		
		Cover addCode(String sourceString) throws DocumentException, Exception{
	        this.add(encoder.encode(sourceString));
			return this;
		}
		
		Cover addContact() throws DocumentException{
			Font psm = new Font(FontFamily.HELVETICA, 9.f, Font.NORMAL, BaseColor.BLACK);
			Chunk chunk = new Chunk("Pour toute demande d'information sur l'utilisation de ce document merci d'écrire à l'adresse dsi-admin-ged@univ-lille2.fr", psm);
			Paragraph contact = new Paragraph(chunk);
			contact.setAlignment(Paragraph.ALIGN_CENTER);
			this.add(contact);
			return this;
		}
		
		Cover newLine() throws DocumentException{
			this.add(new Paragraph(Chunk.NEWLINE));
			return this;

		}

	}
	
	

}
