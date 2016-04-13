package fr.univlille2.ecm.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.mail.Composer;
import org.nuxeo.ecm.automation.core.mail.Mailer.Message;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 */
public abstract class DocumentNotifier {
	
	private static final Log logger =  LogFactory.getLog(DocumentNotifier.class);
	private static final Composer COMPOSER = new Composer();
	protected final CoreSession session;	
	protected UserManager userManager;
	protected DocumentModel doc;
	
	public DocumentNotifier(DocumentModel doc, CoreSession session) {
		this.doc = doc;
		this.session = session;
		try {
			this.userManager = Framework.getService(UserManager.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected Map<String, Object> getContext() throws ClientException {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("docURL",Framework.getProperty("nuxeo.url")+"/nxdoc/default/"+doc.getId()+"/view_documents");
		context.put("docTitle", doc.getTitle());
		context.put("docRepoID", doc.getProperty("scan:targetId").getValue());
		for(String s: doc.getSchemas()){
			logger.debug("Schema: " + s);
		}
		return context;
	}

	protected Message createMessage() throws ClientException, Exception {
		return COMPOSER.newHtmlMessage(getContent(), getContext());
	}

	public void sendMail() throws Exception {
		Message msg = createMessage();
		msg.setSubject(getObject());
		msg.setFrom(Framework.getProperty("mail.from"));
		for(String address : getRecipientsAddresses()){
			msg.addTo(address);
		}
		msg.send();	
	}
	
	protected abstract String getContent();
	protected abstract String getObject() throws ClientException; 
	protected abstract List<String> getRecipientsAddresses();
	

}
