package fr.univlille2.ecm.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.univlille2.ecm.model.UnrestrictedQueryExecutor;
import fr.univlille2.ecm.random.PseudoRandomizer;

/**
 * Will manage a simple action.
 * @author acordier
 * @see OSGI-INF/contribs/actions-contribs.xml
 */
@Name("scanRepoIdGenerator")
@Scope(ScopeType.CONVERSATION)
public class ScanRepoIdGenerator {
	

    
	@In(create = true, required = false)
	protected transient CoreSession session;
	
	@In(create = true)
	protected NavigationContext navigationContext;
	
	private DocumentModel doc;
	
	public void doGet(){
			
	}

	
	/**
	 * Action filter.
	 * @return
	 */
	public boolean accept() {
		return true;
	}
	
	public DocumentModel getCurrentDocument() {
		if (doc == null) {
			doc = navigationContext.getCurrentDocument();
		}
		return doc;
	}
	
	
}

