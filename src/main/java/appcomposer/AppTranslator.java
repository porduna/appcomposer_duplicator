package appcomposer;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

/**
 * Core implementation of the AppComposerTranslator.
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class AppTranslator implements IAppTranslator {

	public static final String DB_NAME = "appcomposerdb";
	public static final String COLLECTION = "bundles";

	private final String dbName;
	private final String host;
	private final int port;
	private final MongoCredential credential;
	private MongoClient mongoClient = null;
	private DBCollection bundles = null;
	private DBCollection translationUrls = null;
	
	public AppTranslator() {
		this("localhost", 27017, null, null, AppTranslator.DB_NAME);
	}
	
	public AppTranslator(String host, int port, String username, String password) {
		this(host, port, username, password, AppTranslator.DB_NAME);
	}
	
	public AppTranslator(String host, int port, String username, String password, String dbName) {
		this.dbName = dbName;
		this.host = host;
		this.port = port;
		if (username != null && password != null)
			this.credential = MongoCredential.createCredential(username, dbName, password.toCharArray());
		else
			this.credential = null;
	}
	
	@Override
	public void connect() throws AppComposerException {
		try {
			if (this.credential != null)
				this.mongoClient = new MongoClient( new ServerAddress(this.host, this.port), Arrays.asList(this.credential));
			else
				this.mongoClient = new MongoClient( this.host, this.port );
		
			final DB db  = mongoClient.getDB( this.dbName );
			this.bundles = db.getCollection(AppTranslator.COLLECTION);
			this.translationUrls = db.getCollection("translation_urls");
		} catch (UnknownHostException e) {
			throw new AppComposerException("Error connecting to MongoDB", e);
		}
	}
	
	@Override
	public boolean isConnected() {
		return this.mongoClient != null;
	}
	
	@Override
	public void disconnect() throws AppComposerException {
		this.mongoClient.close();
	}
	
	@Override
	public Map<String, String> translate(String url, String translationUrl, String lang, String targetAge) throws AppComposerException {
		if (lang.length() == 2) {
			lang = lang + "_ALL";
		}
		return translate(url, translationUrl, lang + "_" + targetAge);
	}

	@Override
	public Map<String, String> translate(String url, String translationUrl, String bundle) throws AppComposerException {
		if (bundle.split("_").length == 2) {
			// e.g., en_16years converts into en_ALL_16years 
			bundle = bundle.replace("_", "_ALL_");
		}
		if (!isConnected())
			throw new AppComposerException("AppTranslator not connected. Did you call connect?");
		
		BasicDBObject query = new BasicDBObject("spec", url).append("bundle", bundle);
		DBObject obj = this.bundles.findOne(query);
		if (obj == null) {
			// Searching by app did not work. Let's try by translation url
			query = new BasicDBObject("url", translationUrl).append("bundle", bundle);
			obj = this.translationUrls.findOne(query);
			if (obj == null) {
				// It seems that we do not have anything. Let's check without the extra information after the # if it is there

				// First without the # in the URL
				if (url.contains("#")) {
					final String urlWithoutHash = url.split("#")[0];
					return translate(urlWithoutHash, translationUrl, bundle);
				}

				// Then without the # in the translationUrl
				if (translationUrl.contains("#")) {
					final String translationUrlWithoutHash = translationUrl.split("#")[0];
					return translate(url, translationUrlWithoutHash, bundle);
				}

				if (bundle.indexOf("_ALL_") < 0) {
					final String [] parts = bundle.split("_");
					final String language = parts[0];
					final String target = parts[2];
					final String newBundle = language + "_ALL_" + target;
					return translate(url, translationUrl, newBundle);
				}

                if (url.startsWith("http://")) {
                    final String httpsUrl = url.replaceFirst("http://", "https://");
                    return translate(httpsUrl, translationUrl, bundle);
                }

                if (translationUrl.startsWith("http://")) {
                    final String httpsTranslationUrl = translationUrl.replaceFirst("http://", "https://");
                    return translate(url, httpsTranslationUrl, bundle);
                }

				// Here all the combinations have been tried
				return null;
			}
		} 
		
		final Object data = obj.get("data");
		if (!(data instanceof String)) 
			throw new CorruptRecordException("MongoDB 'data' for url=" + url + " and bundle=" + bundle + " expected to be a String. Found: " + data);
		
		try {
			final String stringDictionary = (String)data;
			final DBObject dictionary = (DBObject)JSON.parse(stringDictionary);
			final Set<String> keys = dictionary.keySet();
			final Map<String, String> translations = new HashMap<String, String>(keys.size());
			if (dictionary != null && keys != null) {
				for(String key : keys) 
					if (dictionary.containsField(key) && dictionary.get(key) != null)
						translations.put(key, dictionary.get(key).toString());
			}
			
			return translations;
		} catch (Exception e) {
			throw new AppComposerException("Error parsing AppComposer data", e);
		}
	}
}
