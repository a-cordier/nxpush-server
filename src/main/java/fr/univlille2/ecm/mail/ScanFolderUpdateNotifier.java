package fr.univlille2.ecm.mail;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.mail.Composer;
import org.nuxeo.ecm.automation.core.mail.Mailer.Message;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ScanFolderUpdateNotifier {
	
	private DocumentModel doc;
	
	private static final Log logger =  LogFactory.getLog(ScanFolderUpdateNotifier.class);
	
	private static final Composer COMPOSER = new Composer();
	
	public ScanFolderUpdateNotifier(DocumentModel folder){
		this.doc= folder;
	}

	/**
	 *  Construction du corps du message 
	 *  TODO >> Get from FTL template */
	protected String getContent(DocumentModel doc) {
		StringBuilder sB = new StringBuilder();
		sB.append("<P>");
		sB.append("Bonjour,");
		sB.append("</P>");
		sB.append("<P>");
		// TODO contenu du message
		sB.append("</P>");
		sB.append("<P>");
		sB.append("L'automate G.E.D. vous souhaite une bonne journ&eacute;e.");
		sB.append("</P>");
		return sB.toString();
	}

	protected Message createMessage(String message, Map<String, Object> map)
			throws Exception {
		return COMPOSER.newHtmlMessage(message, map);
	}

	protected void sendMail(DocumentModel doc) throws Exception {

	
	}


}
