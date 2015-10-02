/**
 * 
 */

package fr.univlille2.ecm.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.univlille2.ecm.random.PseudoRandomizer;



public class GenerateScanRepoId {

    public static final String ID = "GenerateScanRepoId";
    private Log logger = LogFactory.getLog(GenerateScanRepoId.class);
    
    @Context
   	protected OperationContext context;
    
	@OperationMethod
	public Object run(DocumentModel input) {
		String out = null;		
		do{
			out = generateId();
		}while(exists(out));		
			logger.debug(String.format("Code %s generated for %s", out, input.getPathAsString()));
		return out; 
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
	
	/* check if an other document is flagged with this code*/
	private boolean exists(String code){
		String query = buildQuery(code);
		CoreSession session = context.getCoreSession();
		DocumentRef result;
		try {
			result = session.query(query).get(0).getRef();
			logger.debug("DOC: "+session.getDocument(result).getPathAsString());
			if(result!=null){
				logger.debug(String.format("Found document with same repoId : %s", 
						session.getDocument(result).getPathAsString()));
				return true;
			}
		} catch (Exception e) {
			logger.error(String.format("Query error %s", 
					e.getMessage()));
		}		
		return false;
	}
	
	/* check  query */
	private String buildQuery(String parameter){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM Document ");
		sb.append("WHERE ecm:mixinType = 'Folderish' ");
		sb.append(String.format("AND scan:targetId = '%s' ", parameter));
		sb.append("AND ecm:currentLifeCycleState <> 'deleted'");  	
		return sb.toString();
	}
}
