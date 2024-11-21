# About

This is an example JSR-352 batch running on Wildfly, which 

1. Loads Data from a File in a Database in Step 1
2. And then unload all Data from the Databasein a second step in parallel.

The example consists of unit tests for the reader, processor and writer 
classes in Step 1 as well as a test script to invoke the batch on wildfly.

The presentation is available at [src/site/resources](src/site/resources)

# Install Wildfly

- Requires Java 11
- Download Wildfly 18 (https://www.wildfly.org/downloads/)
- Unpack ZIP into a local directory
- Set an admin user password

Set a new password for the admin user:

```powershell
cd $WILDFLY_HOME\bin
./add-user.ps1 -m -u admin -p admin
```

Add a datasource, our batch can use for reading and writing:

```powershell
cd $WILDFLY_HOME\bin
.\jboss-cli.ps1 --connect `
    --file="...\src\main\resources\wildfly-datasources.cfg"
```

## Optional: Enable remote debugging 

```
# standalone.conf, standalone.conf.ps1 
agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n
```

# Compile

```powershell
mvn clean install
```

# Local test

1. Deploy Application on a local Wildfly server
2. Run testcase `BatchIntegrationTest`

Deploy the application using maven:

```powershell
# deploy to local server listening on port 9990
mvn wildfly:deploy
```
