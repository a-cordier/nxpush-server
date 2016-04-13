package fr.univlille2.ecm.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 */
public class UnrestrictedQueryExecutor extends UnrestrictedSessionRunner {

	/** this query will be executed in an unrestricted scope **/
	private String query;
    
    /** the result of the unrestricted query **/
    private DocumentModelList resultSet;
    
	/** logger. **/
    private static final Log logger = LogFactory.getLog(UnrestrictedQueryExecutor.class); 
    
	public UnrestrictedQueryExecutor(CoreSession session) {
		super(session);
	}

	@Override
	public void run() throws ClientException {
		if(query!=null){
			logger.debug("running query unrestricted");
			resultSet = session.query(query);
		}
	}
	
	public DocumentModelList query(String query){
		return run(query);
	}
	
	private DocumentModelList run(String query){
		this.query = query;
		try {
			runUnrestricted();
		} catch (ClientException e) {
			logger.error("Error runing unrestricted query: " + e.getMessage());
		}
		logger.debug("returning result set - size " + resultSet.size() );
		return resultSet;
	}
}
