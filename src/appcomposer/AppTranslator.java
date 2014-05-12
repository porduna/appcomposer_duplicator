package appcomposer;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

/**
 * Core implementation of the AppComposerTranslator.
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class AppTranslator implements IAppTranslator {

	public static final String DB_NAME = "appcomposerdb";
	public static final String COLLECTION = "bundles";
	
	private final MongoClientURI clientUri;
	private MongoClient mongoClient = null;
	private DBCollection bundles = null;
	
	public AppTranslator() {
		this("localhost", 27017, null, null);
	}
	
	public AppTranslator(String host, int port, String username, String password) {
		if (username != null && password != null) {
			this.clientUri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/"); 
		} else {
			this.clientUri = new MongoClientURI("mongodb://" + host + ":" + port + "/");
		}
	}
	
	@Override
	public void connect() throws AppComposerException {
		 try {
			this.mongoClient = new MongoClient( this.clientUri );
			final DB db  = mongoClient.getDB( AppTranslator.DB_NAME );
			this.bundles = db.getCollection(AppTranslator.COLLECTION);
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
	public Map<String, String> translate(String url, String lang, String targetAge) throws AppComposerException {
		return translate(url, lang + "_" + targetAge);
	}

	@Override
	public Map<String, String> translate(String url, String bundle) throws AppComposerException {
		if (!isConnected())
			throw new AppComposerException("AppTranslator not connected. Did you call connect?");
		
		final BasicDBObject query = new BasicDBObject("spec", url).append("bundle", bundle);
		final DBObject obj = this.bundles.findOne(query);
		if (obj == null)
			return null;
		
		final Object data = obj.get("data");
		if (!(data instanceof String)) 
			throw new CorruptRecordException("MongoDB 'data' for url=" + url + " and bundle=" + bundle + " expected to be a String. Found: " + data);
		
		try {
			final String stringDictionary = (String)data;
			final DBObject dictionary = (DBObject)JSON.parse(stringDictionary);
			final Set<String> keys = dictionary.keySet();
			final Map<String, String> translations = new HashMap<String, String>(keys.size());
			for(String key : keys)
				translations.put(key, dictionary.get(key).toString());
			
			return translations;
		} catch (Exception e) {
			throw new AppComposerException("Error parsing AppComposer data", e);
		}
	}
}
