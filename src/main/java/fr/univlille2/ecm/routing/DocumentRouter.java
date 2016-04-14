
package fr.univlille2.ecm.routing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import fr.univlille2.ecm.actions.CoverGenerator;
import fr.univlille2.ecm.cover.Constants;
import fr.univlille2.ecm.cover.CoverParser;

/**
 * Routage d'un document numérisé:
 *  première tentative basée sur la page de couverture
 *  seconde tentative basée sur un code de préfix 
 *   @see CoverGenerator ScanRepoIdGenerator
 * @author acordier
 * @date 13 avr. 2016
 */
public class DocumentRouter {

	private DocumentModel sourceDocument;
	private CoreSession session;
	private DocumentModel target;
	
	private final static List<String> EXPECTED_EXTENSIONS = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(".pdf");
			add(".tiff");
			add(".tif");
			add(".jpg");
			add(".jpeg");
			add(".gif");
			add(".png");
		}
	};
	
	private final static String UNTITLED_DOC_NAME = "sans-titre";

	/** logger. **/
	private static final Log logger = LogFactory.getLog(DocumentRouter.class);

	public DocumentRouter(DocumentModel sourceDocument, CoreSession session) {
		this.sourceDocument = sourceDocument;
		this.session = session;
	}
	
	
	public void route() throws Exception {
		if(!hasCover())
			if(!hasPrefix())
				throw new Exception("Unroutable document");
		move(sourceDocument, target.getRef());	
	}
	
	private void move(final DocumentModel doc, final DocumentRef target) throws ClientException{
		UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(
				session) {
			@Override
			public void run() throws ClientException {
				try { /* move document and remove name prefix */
					String name = doc.getName().replaceFirst(
							"[0-9]{4}[A-Za-z]{2}[-_]*", "");
					if (isUntitled(name)) {
						String ext = "";
						if(name.lastIndexOf('.') >= 0){
							ext = name.substring(name.lastIndexOf('.'));
						}
						name = UNTITLED_DOC_NAME + ext;				
					}
					DocumentModel targetDocument = session.move(
							doc.getRef(), target, name);
					targetDocument.setPropertyValue("dc:title", name);
					session.saveDocument(targetDocument);
				} catch (ClientException e) {
					logger.error(String.format(
							"Cannot move document, cause: %s ",
							e.getMessage()));
				}
			}
		};
		runner.runUnrestricted();
	}

	private boolean hasCover() {
		BlobHolder blobHolder = sourceDocument.getAdapter(BlobHolder.class);
		try {
			Blob blob = blobHolder.getBlob();
			byte[] content = blob.getByteArray();
			CoverParser parser = new CoverParser(Constants.CODING_STRATEGY.newInstance());
			String decodedString = null;
			try {
				decodedString = parser.decode(content);
			} catch (Exception e) {
				logger.error(e);
			}
			if (decodedString != null) {
				IdRef ref = new IdRef(decodedString);
				DocumentModel target = session.getDocument(ref);
				if (target != null) {
					this.target = target;
					byte[] cleanContent = removeFirstPage(content);
					blob = new ByteArrayBlob(cleanContent);
					DocumentHelper.addBlob(sourceDocument.getProperty("file:content"), blob);
					sourceDocument = session.saveDocument(sourceDocument); 
					return true;
				}
			}
		} catch (ClientException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (DocumentException e) {
			logger.error(e);
		}
		return false;
	}

	private boolean hasPrefix() {
		String prefix;
		try {
			if (sourceDocument.getTitle().length() >= 6) {
				prefix = sourceDocument.getTitle().substring(0, 6).toUpperCase();
				String query = buildQuery(prefix);
				DocumentModelList queryResult = session.query(query);
				if (queryResult.size() == 1) {
					target = queryResult.get(0);
					return true;
				}
			}
		} catch (ClientException e) {
			logger.error(e);
		}
		return false;
	}

	private String buildQuery(String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM Document ");
		sb.append("WHERE ecm:mixinType = 'Folderish' ");
		sb.append(String.format("AND scan:targetId = '%s' ", prefix));
		sb.append("AND ecm:currentLifeCycleState <> 'deleted'");
		return sb.toString();
	}

	private boolean isUntitled(String name) {
		if ("".equals(name)) {
			return true;
		}
		for (String extension : EXPECTED_EXTENSIONS) {
			if (extension.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	private byte[] removeFirstPage(byte[]input) throws DocumentException, IOException {
	    PdfReader reader = new PdfReader(input);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    int pages_number = reader.getNumberOfPages();
	    if(pages_number > 0) {
	        Document document = new Document(reader.getPageSizeWithRotation(1));
	        PdfCopy copy = new PdfCopy(document, bos);
	        document.open();
	    for(int i=2; i <= pages_number;i++) {
	            PdfImportedPage page = copy.getImportedPage(reader, i);
	        copy.addPage(page);
	    }
	        document.close();        
	    }
	    return bos.toByteArray();
	}


}
