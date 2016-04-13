/**
 * 
 */

package fr.univlille2.ecm.actions;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;

import fr.univlille2.ecm.cover.CoverBuilder;
import fr.univlille2.ecm.cover.QuickResponseCodingStrategy;
import fr.univlille2.ecm.webui.WebUiFileDownloader;

/**
 *
 * Code skeleton for a Seam bean that will manage a simple action.
 * This can be used to :
 *  - do a navigation
 *  - do some modification on the currentDocument (or other docs)
 *  - create new documents
 *   - send/retrive info from an external service
 *   - ...
 */
@Name("coverGenerator")
@Scope(ScopeType.EVENT)
public class CoverGenerator implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log logger = LogFactory.getLog(CoverGenerator.class);

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected NuxeoPrincipal currentNuxeoPrincipal;

    @In(create = true)
    protected DocumentsListsManager documentsListsManager;

    /**
     * Generation de pdf contenant quelques informations 
     * utiles sur le document ainsi qu'une représentation 
     * au format code 128 de l'identifiant unique du document
     * @return
     */
    public String doGet() {
    	try {
			WebUiFileDownloader.downloadFile(new CoverBuilder(documentManager, doc()).getCover(new QuickResponseCodingStrategy()));
		} catch (IOException | ClientException e) {
			logger.error(e);
		}
        return null;
    }

	/**
	 * Action filter.
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
