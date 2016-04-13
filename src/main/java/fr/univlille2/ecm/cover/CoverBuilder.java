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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

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
	

	public Blob getCover(CodingStrategy encoder) {
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
	
	

}
