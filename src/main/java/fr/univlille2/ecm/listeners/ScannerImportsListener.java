package fr.univlille2.ecm.listeners;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_UPDATED;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/** @author acordier */
public class ScannerImportsListener implements EventListener {

	private final static Log LOGGER = LogFactory.getLog(ScannerImportsListener.class);
	/* TODO /default-domain/dsi/Workspaces/Pole-Urbanisation-Modern/GED/scans */
 	private final static String INCOMING_FOLDER = Framework.getProperty("nxpush.incoming");
	private final static String UNTITLED_DOC_NAME = "sans-titre";
	/* most likely expected extensions  */
	private final static 	List<String> EXPECTED_EXTENSIONS = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
	        add(".pdf");
	        add(".tiff");
	        add(".jpg");
	        add(".jpeg");
	        add(".gif");
	        add(".png");
	    }
	};	
	private String prefix;
	
	public void handleEvent(Event event) throws ClientException {
		/*Check list */
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
		DocumentModel sourceDocument = ((DocumentEventContext)context).getSourceDocument();
		if(sourceDocument == null || !(sourceDocument.getPathAsString().startsWith(INCOMING_FOLDER))){
			return;
		}
		if(sourceDocument.getTitle().length()>=6){
			prefix = sourceDocument.getTitle().substring(0,6).toUpperCase();
		}else{
			return;
		}
		/*Checked*/

		RepositoryManager repoManager=null;
		try {
			repoManager = Framework.getService(RepositoryManager.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		final DocumentModel routedDocument = sourceDocument;
		String repositoryName = repoManager.getDefaultRepository().getName();
		CoreInstance server = CoreInstance.getInstance();
		CoreSession client = server.open(repositoryName, null);
		/* Getting destination folder */		
		String query = buildQuery();
		DocumentModelList queryResult = client.query(query);
		LOGGER.debug("Documents found for this target id : " + queryResult.size());
		DocumentModel uniqueResult=queryResult.size()==1?queryResult.get(0):null;
		if(uniqueResult!=null){
			final DocumentRef destination = uniqueResult.getRef();

			UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(
					client) {

				@Override
				public void run() throws ClientException {
					try{ /* move document and remove name prefix*/		 
						String name = routedDocument.getName().replaceFirst("[0-9]{4}[A-Za-z]{2}[-_]*", "");
						if(isUntitled(name)){
							name = UNTITLED_DOC_NAME + (name.lastIndexOf('.')>=0? name.substring(name.lastIndexOf('.')): "");
						}
						DocumentModel targetDocument  = session.move(routedDocument.getRef(), destination, name);
						targetDocument.setPropertyValue("dc:title", name);
						session.saveDocument(targetDocument);
					}catch(ClientException e){
						LOGGER.error(String.format("Cannot move document, cause: %s ", e.getMessage()));

					} 
				}
			};

			runner.runUnrestricted();


		}

	}    

	private String buildQuery(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM Document ");
		sb.append("WHERE ecm:mixinType = 'Folderish' ");
		sb.append(String.format("AND scan:targetId = '%s' ", prefix));
		sb.append("AND ecm:currentLifeCycleState <> 'deleted'");  	
		return sb.toString();
	}
	
	private static boolean isUntitled(String name){
		if("".equals(name)){
			return true;
		}
		for(String extension: EXPECTED_EXTENSIONS){
			if(extension.equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
}
