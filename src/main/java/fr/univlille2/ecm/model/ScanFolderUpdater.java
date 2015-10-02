package fr.univlille2.ecm.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.univlille2.ecm.actions.ScanRepoIdGenerator;
import fr.univlille2.ecm.random.PseudoRandomizer;

public class ScanFolderUpdater {
	
	/** logger. **/
    private static final Log logger = LogFactory.getLog(ScanRepoIdGenerator.class); 
    
    private final CoreSession session;
    
    private DocumentModel folder;
    
    public ScanFolderUpdater(DocumentModel folder, CoreSession session){
    	this.folder = folder;
    	this.session = session;
    }
    
    /** Generate id if needed and set document meta data **/
    public void updateDocument() throws DocumentException{
    	if(!folder.isFolder()){
    		throw new DocumentException("Document is not a folder and can not be used as a scan folder");
    	} 
    	String id = folder.getProperty("scan:targetId").toString();
    	if(id !=null && !id.isEmpty()){
    		return;
    	}	
		do{ // let's insure id is unique
			id = generateId();
		} while(exists(id));	
		folder.setPropertyValue("scan:targetId", id);	
    }
    
	/** checks if an other document is flagged with this code. */
	private boolean exists(String code){
		String query = buildQuery(code);
		/* Query is executed unrestricted as we want to fetch among documents that we do not own */
		UnrestrictedQueryExecutor queryExecutor = new UnrestrictedQueryExecutor(session);
		DocumentRef result = queryExecutor.query(query).get(0).getRef();
		if(result!=null){
			logger.debug(String.format("Found document with same repoId : %s", 
					session.getDocument(result).getPathAsString()));
			return true;
		}
		return false;
	}
	
	private String buildQuery(String parameter){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM Document ");
		sb.append("WHERE ecm:mixinType = 'Folderish' ");
		sb.append(String.format("AND scan:targetId = '%s' ", parameter));
		sb.append("AND ecm:currentLifeCycleState <> 'deleted'");  	
		return sb.toString();
	}
	
	/*Code is generated based on current time and 2 random Latin letters */
	private String generateId(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");	
		StringBuilder sb = new StringBuilder();
		sb.append(dateFormat.format(new Date()));
		try {
			sb.append(PseudoRandomizer.getInstance().getRandomString(2).toUpperCase());
		} catch (Exception e) {
			logger.error(String.format("String generation error caused by %s", e.getMessage()));
			return null;
		}
		return sb.toString(); 
	}
}
