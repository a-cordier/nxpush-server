package fr.univlille2.ecm.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.univlille2.ecm.mail.ScanFolderUpdateNotifier;
import fr.univlille2.ecm.model.ScanFolderUpdater;

/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 */
@Name("scanRepoIdGenerator")
@Scope(ScopeType.CONVERSATION)
public class ScanRepoIdGenerator {

	@In(create = true, required = false)
	protected transient CoreSession documentManager;

	@In(create = true)
	protected NavigationContext navigationContext;

	/** logger. **/
	private static final Log logger = LogFactory
			.getLog(ScanRepoIdGenerator.class);

	public void doGet() throws Exception {
		logger.debug("Action triggered");
		ScanFolderUpdater updater = new ScanFolderUpdater(doc(), documentManager);
		ScanFolderUpdateNotifier notifier = new ScanFolderUpdateNotifier(doc(),
				documentManager);
		try {
			logger.debug("Updating document");
			updater.updateDocument();
			notifier.sendMail();
		} catch (DocumentException e) {
			logger.error(e);
			throw e;
		} catch (PropertyException e) {
			logger.error(e);
			throw e;
		} catch (ClientException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * Affiché uniquement pour le type "Folder"
	 * 
	 * @return
	 */
	public boolean accept() {
		return doc().getType().equals("Folder");
	}

	public DocumentModel doc() {
		return navigationContext.getCurrentDocument();
	}
	


}
