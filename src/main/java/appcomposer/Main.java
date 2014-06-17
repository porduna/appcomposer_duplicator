package appcomposer;

import java.util.Map;

public class Main {

	public static void main(String [] args) throws Exception {
		//final AppTranslator translator = new AppTranslator();
        final AppTranslator translator = new AppTranslator("ds049288.mongolab.com", 49288, "deusto", "t3stingmongolab");
		// Alternatively use:
		// final AppTranslator translator = new AppTranslator(host, port, username, password);
		
		translator.connect();
		try{
			if (translator.translate("", "", "") != null)
				System.err.println("Error: null expected when wrong arguments are provided");
			
			final Map<String, String> translations = translator.translate("http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml", "de_ALL", "ALL");
			if(!translations.get("ut_tools_conceptmapper_yes").equals("Ja")) {
				System.err.println("Error: yes in german should be Ja");
			}
			
			System.out.println("Test finished");
			for (String key : translations.keySet())
				System.out.println(key + ": " + translations.get(key));
		} finally {
			translator.disconnect();
		}
	}

}
