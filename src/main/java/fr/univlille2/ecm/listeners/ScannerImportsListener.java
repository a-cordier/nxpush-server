package fr.univlille2.ecm.listeners;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_UPDATED;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.univlille2.ecm.routing.DocumentRouter;

/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 */
public class ScannerImportsListener implements EventListener {

	private final static Log logger = LogFactory.getLog(ScannerImportsListener.class);
	private final static String INCOMING_FOLDER = Framework.getProperty("nxpush.incoming");

	public void handleEvent(Event event) throws ClientException {
		/* Check list */
		String eventId = event.getName();
		if (!eventId.equals(DOCUMENT_UPDATED)) {
			return;
		}
		DocumentEventContext context;
		if (event.getContext() instanceof DocumentEventContext) {
			context = (DocumentEventContext) event.getContext();
		} else {
			return;
		}
		DocumentModel sourceDocument = ((DocumentEventContext) context).getSourceDocument();
		if (sourceDocument == null || !(sourceDocument.getPathAsString().startsWith(INCOMING_FOLDER))) {
			return;
		}
		RepositoryManager repoManager = null;
		try {
			repoManager = Framework.getService(RepositoryManager.class);
		} catch (Exception e) {
			logger.error(e);
		}
		String repositoryName = repoManager.getDefaultRepository().getName();
		CoreSession session = CoreInstance.getInstance().open(repositoryName, null);
		DocumentRouter router = new DocumentRouter(sourceDocument, session);
		try {
			router.route();
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
