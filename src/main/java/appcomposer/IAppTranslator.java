package appcomposer;

import java.util.Map;

/**
 * Abstraction layer for the App Translator
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public interface IAppTranslator {
	
	/**
	 * Translate uses a local (or cloud) database to efficiently obtain a 
	 * translation of a particular gadget. It obtains the translation of each concept
	 * or returns null if the application is not available.
	 * 
	 * @param url The app to be translated.
     * @param translationUrl The default translation URL
	 * @param lang Target language (e.g., en_ES, en_ALL...)
	 * @param targetAge The target age of the translation
	 * @return A map with the translations or null if the translation is not found.
	 */
	Map<String, String> translate(String url, String translationUrl, String lang, String targetAge) throws AppComposerException;
	
	/**
	 * Wrapper of the {@link #translate(String, String, String, String)} method, where bundle
	 * is lang + "_" + targetAge.
	 * 
	 * @param url The app to be translated.
     * @param translationUrl The default translation URL
	 * @param bundle Target language + target age in the format lang + "_" + targetAge
	 * @return A map with the translations or null if the translation is not found.
	 */
	Map<String, String> translate(String url, String translationUrl, String bundle) throws AppComposerException;

	/**
	 * Connect to the data source.
	 * 
	 * @throws AppComposerException
	 */
	void connect() throws AppComposerException;

	/**
	 * Is connected to the data source?
	 * 
	 * @throws AppComposerException
	 */
	boolean isConnected();

	/**
	 * Disconnect from the data source.
	 * 
	 * @throws AppComposerException
	 */
	void disconnect() throws AppComposerException;
}
