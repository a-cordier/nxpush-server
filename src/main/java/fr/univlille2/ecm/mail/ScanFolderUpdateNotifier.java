package fr.univlille2.ecm.mail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.univlille2.ecm.actions.ScanRepoIdGenerator;
/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 * Ce notifieur est utilisé pour expédier un mail contenant le code court lors de la génération de ce dernier
 * dans un conteneur 
 * @see ScanRepoIdGenerator
 */
public class ScanFolderUpdateNotifier extends DocumentNotifier {

	private static final String TEMPLATE = "<p>Bonjour,</p>"+
	"<p>Vous avez généré un code de numérisation pour le répertoire <a href=\"${docURL}\">${docTitle}</a></p>"+
	"<p>Le code correspondant est le <b>${docRepoID}</b></p>"+
	"<p>Pour numériser un document vers ce dépôt, utilisez la fonction de nommage du copieur et préfixez par ce code le nom de votre document.</p>"+
	"<p>L'automate GED vous souhaite une bonne journée.</p>";
	private static final Log logger =  LogFactory.getLog(ScanFolderUpdateNotifier.class);

	
	public ScanFolderUpdateNotifier(DocumentModel doc, CoreSession session) {
		super(doc, session);
	}
	
	@Override
	protected String getContent(){
		return TEMPLATE;
	}
	
	@Override
	protected String getObject() throws ClientException{
		return "Numériser dans le répertoire " + doc.getTitle();
	}
	
	@Override
	protected List<String> getRecipientsAddresses() {
		List<String> recipients = new ArrayList<String>();
		try {
			recipients.add(userManager.getPrincipal(session.getPrincipal().getName()).getEmail());
		} catch (ClientException e) {
			logger.error("Error adding recipient address for user " + session.getPrincipal().getName());
		}
		return recipients;
		
	}


}
