package http;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import appcomposer.IAppTranslator;

/**
 * The TranslatorApplication is a Restlet application that provides
 * the path ( /serve) and shares the translator object among all the
 * instances of the resources created
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class TranslatorApplication extends Application {
	
	private final IAppTranslator translator;
	
	public TranslatorApplication(IAppTranslator translator) {
		this.translator = translator;
	}
	
	public synchronized Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/serve", TranslationResource.class );
		return router;
	}
	
	IAppTranslator getTranslator() {
		return this.translator;
	}
}
