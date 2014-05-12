App Composer Duplicator
=======================

AppComposer (appcomposer.readthedocs.org) database duplicator. Java part (client side).

Instructions:

 1. Install MongoDB
 2. Import the sample data (mongoimport --host localhost --db appcomposerdb --collection bundles --jsonArray < data/bundles)
 3. Compile (settings for Eclipse are provided)
 4. Run appcomposer.Main to see the basic part working (i.e., the connection and the Java API)
 5. Run appcomposer.http.RestletMain to see the web service working (i.e., the HTTP version, a message is shown with an example)
 
If you need to setup credentials or a different host, use the following constructor instead of the default one:

   final IAppTranslator translator = new AppTranslator(host, port, username, password);
