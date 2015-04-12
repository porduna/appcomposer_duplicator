package http;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import appcomposer.AppComposerException;
import appcomposer.IAppTranslator;

/**
 * The TranslationResource is a Restlet server resource that is 
 * created by each request.
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class TranslationResource extends ServerResource {
	
	@Get
	public Representation toXml() {
		
		// Validate the parameters
		final String url = getQueryValue("app_url");
		final String translationUrl = getQueryValue("translation_url");
		final String lang = getQueryValue("lang");
		final String targetAge = getQueryValue("target");
		
		if (url == null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing app_url parameter");
		if (translationUrl == null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing translation_url parameter");
		if (lang == null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing lang parameter");
		if (targetAge  == null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing target parameter");

		// Obtain a translation
		final IAppTranslator translator = ((TranslatorApplication)getApplication()).getTranslator();
		
		final Map<String, String> translations;
		try {
			translations = translator.translate(url, translationUrl, lang, targetAge);
		} catch (AppComposerException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Error translating the application");
		}
		
		// Return 404 if the translation is not there
		if (translations == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "The requested translation is not available");
		
		// Return the translation otherwise
		final Document doc = serializeTranslations(translations);
		return new DomRepresentation(MediaType.APPLICATION_XML, doc);
	}
	
	// Pure Java instead of XStream or whatever for avoiding dependences
	private Document serializeTranslations(Map<String, String> translations) {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			final Document doc = docBuilder.newDocument();
			final Element rootElement = doc.createElement("messagebundle");
			doc.appendChild(rootElement);
			for(String key : translations.keySet()) {
				final String translation = translations.get(key);
				final Element messageElement = doc.createElement("msg");
				final Attr name = doc.createAttribute("name");
				name.setValue(key);
				messageElement.setAttributeNode(name);
				messageElement.appendChild(doc.createTextNode(translation));
				rootElement.appendChild(messageElement);
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Error serializing the translations");
		}
	}
}
