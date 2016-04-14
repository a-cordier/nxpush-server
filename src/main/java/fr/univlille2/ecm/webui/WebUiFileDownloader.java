package fr.univlille2.ecm.webui;

import java.io.IOException;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants;
import org.nuxeo.ecm.platform.ui.web.tag.fn.Functions;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;

import fr.univlille2.ecm.cover.CoverBuilder;
/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 * Déclenche le téléchargement de la page de courverture dans l'interface utilisateur
 */
public class WebUiFileDownloader {
	
	private static final Log logger = LogFactory.getLog(CoverBuilder.class);

	public static void downloadFile(Blob blob) throws IOException{
		
		 if (blob == null) {
	            throw new RuntimeException("there is no file content available");
	        }
		 
	        FacesContext faces = FacesContext.getCurrentInstance();
	        String filename = "couverture.pdf";
	        if (blob.getLength() > Functions.getBigFileSizeLimit()) {

	            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	            
	            if (!blob.isPersistent()) {
	                blob.persist();
	            }

	            String sid = UUID.randomUUID().toString();
	            request.getSession(true).setAttribute(sid, blob);

	            String bigDownloadURL = BaseURL.getBaseURL(request);
	            bigDownloadURL += "nxbigblob" + "/" + sid;
	            
	            try {
	            	
	                request.setAttribute(
	                        NXAuthConstants.DISABLE_REDIRECT_REQUEST_KEY,
	                        new Boolean(true));

	                response.sendRedirect(bigDownloadURL);

	                response.flushBuffer();
	                FacesContext.getCurrentInstance().responseComplete();
	            } catch (IOException e) {
	                logger.error("Error while redirecting for big blob downloader", e);
	            }
	        } else {
	            ComponentUtils.download(faces, blob, filename);
	        }
	        
	}

}
