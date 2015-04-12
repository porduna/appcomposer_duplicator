App Composer Duplicator
=======================

The AppComposer Duplicator is a service that connects to the [App Composer](http://appcomposer.readthedocs.org) database to get translations for OpenSocial gadgets that are served by Shindig. The service requires 3 parameters:

1. app_url=http://go-lab.gw.utwente.nl/producp/src/main/webapp/conceptmapper.xml
app_url provides the URL of the OpenSocial gadget to be translated

2. translation_url=http://go-lab.gw.utwente.nl/producp/src/main/webapp/conceptmapper.xml
translation_url provides the URL of the default translation

3. lang=pt_ALL
lang provides the language for which you want to get a language

4. target=ALL
target provides the target group for which you want to get a translation. The target group is an age group that enables a finer grained (targeted) translation

The service (once started) runs on http://localhost:8182/serve

Instructions:
-------------

 1. Install MongoDB
 2. Import the sample data (mongoimport --host localhost --db appcomposerdb --collection bundles --jsonArray < data/bundles)
 3. Create proper index: ( db.bundles.ensureIndex( { 'bundle' : 1, 'spec' : 1 } ); )
 4. Compile and run (See instructions below)

How to compile it?
------------------

To compile you can use the maven pom.xml file that is provided. To clean the previous compile, package and create daemon scripts you can run (in the directory where the pom.xml file is).

    mvn -e clean package appassembler:generate-daemons

Next make a directory 'logs' in 'target/appassembler/jsw/appcompdupe/':

    mkdir target/appassembler/jsw/appcompdupe/logs

This will generate scripts to start and stop the deamon service in:

    target/appassembler/jsw/appcompdupe/bin

In Linux, you might need to grant execution permission to the files in this directory:

    chmod +x target/appassembler/jsw/appcompdupe/bin/*

You can run the service with:

    target/appassembler/jsw/appcompdupe/bin/appcompdupe start
    
and stop with:

    target/appassembler/jsw/appcompdupe/bin/appcompdupe start
    
In case you want to see what happens on the commandline of the service you can start it with 

    target/appassembler/jsw/appcompdupe/bin/appcompdupe console
    
(use ctrl+c to shut it down)

ALTERNATIVELY:

You can use the symbolic link provided in the same directory as the pom.xml file and do:

    appcompdupe start
    appcompdupe stop
    appcompdupe console

You can test the service on the commandline as follows:

    curl -v -L -G -d "app_url=http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml&lang=es_ALL&target=ALL" http://localhost:8182/serve

Credentials
-----------

If you need to setup credentials or a different host, use the following constructor instead of the default one:

    final IAppTranslator translator = new AppTranslator(host, port, username, password);

Potential issues:
-----------------

Maybe the daemon wrapper might be only useful

