package fr.univlille2.ecm.model;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

public class UnrestrictedQueryExecutor extends UnrestrictedSessionRunner {

	/** this query will be executed in an unrestricted scope **/
	private String query;
    
    /** the result of the unrestricted query **/
    private DocumentModelList resultSet;
    
	public UnrestrictedQueryExecutor(CoreSession session) {
		super(session);
	}

	@Override
	public void run() throws ClientException {
		if(query!=null){
			resultSet = session.query(query);
		}
	}
	
	public DocumentModelList query(String query){
		DocumentModelList out = run(query);
		clean();
		return out;
	}
	
	private DocumentModelList run(String query){
		this.query = query;
		runUnrestricted();
		return resultSet;
	}
	
	private void clean(){
		this.query = null;
		this.resultSet = null;
	}
	
	

}
