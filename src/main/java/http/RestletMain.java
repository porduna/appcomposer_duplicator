package http;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;

import appcomposer.AppTranslator;
import appcomposer.IAppTranslator;

/**
 * Testing class
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class RestletMain {

	public static void main(String[] args) throws Exception {
		//final IAppTranslator translator = new AppTranslator();
        final AppTranslator translator = new AppTranslator("ds049288.mongolab.com", 49288, "deusto", "t3stingmongolab");
		translator.connect();
		
		final Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8182);

		// Create an application
		final Application application = new TranslatorApplication(translator);

		// Attach the application to the component and start it
		component.getDefaultHost().attach(application);
		component.start();
		
		System.out.println("Go to http://localhost:8182/serve?app_url=http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml&lang=de_ALL&target=ALL");
	}

}
