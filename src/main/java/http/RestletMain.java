package http;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.engine.Engine;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.HttpServerHelper;

import appcomposer.AppTranslator;
import appcomposer.IAppTranslator;

/**
 * Testing class
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class RestletMain {
    
    public static final boolean USE_JETTY = true;


    public static final String MONGO_USERNAME = "deusto";
    public static final String MONGO_PASSWORD = "t3stingmongolab";
    public static final String MONGO_HOST = "ds049288.mongolab.com";
    public static final int MONGO_PORT = 49288;

    /*
    public static final String MONGO_USERNAME = null;
    public static final String MONGO_PASSWORD = null;
    public static final String MONGO_HOST = "localhost";
    public static final int MONGO_PORT = 27017;
    */

	public static void main(String[] args) throws Exception {
		//final IAppTranslator translator = new AppTranslator();
        final AppTranslator translator = new AppTranslator(MONGO_HOST, MONGO_PORT, MONGO_USERNAME, MONGO_PASSWORD);
		translator.connect();
		
		final Component component = new Component();
        final int port = 8182;

        if (USE_JETTY) {
            // This uses the Jetty connector
            Engine.getInstance().getRegisteredServers().clear() ;
            HttpServerHelper helper = new HttpServerHelper(new Server(Protocol.HTTP, port, component));
            helper.start();
        } else {
            // This uses the Internal connector
	    	component.getServers().add(Protocol.HTTP, port);
        }

		// Create an application
		final Application application = new TranslatorApplication(translator);

		// Attach the application to the component and start it
		component.getDefaultHost().attach(application);
		component.start();
		
		System.out.println("Go to http://localhost:8182/serve?app_url=http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml&lang=de_ALL&target=ALL");
	}

}
