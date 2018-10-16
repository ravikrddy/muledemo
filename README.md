# mule4-skeleton-system-api
With micro-services, a dynamic service registry is used to publish micro-service availability to applications that need to use the service. Multiple instances of the same micro-service each register themselves to the service registry as they start. 

Similarly, the micro-service instance registers their unavailability when the instance stops.  

This is a Mule 4.1 example of an API using API Management and dynamic API registration using the Eureka REST API as the interface to a dynamic service registry. 

The REST API definition can be found [here](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations)
 

Note, the scope features of minimal-logging are not displayed correctly by Studio 7.1 or 7.2. Support for displaying SDK connector scopes is expected in Studio 7.3.

## Uses

The project contains examples using:

* The minimal-logging connector, 
* The secure property connector (formally called the secure-property-placeholder),
* Maven deployment using the mule-maven-plugin descriptor,
* Api Manager gateway auto-discovery registration,
* Using the Maven filter "feature" to add Maven properties to code in files stored in the resources-filtered directory...specifically, updating the api.base and log4j2 configurations with the project name.
* Eureka REST API for dynamic service registration,
* Initiating flows on Mule application started and stopping notifications,
* Invoking a flow from java.

## Purpose

This project is an example that can be deployed and will run when properly configured. However, the main usage for this is a starting point for building new APIs.
Developers of an API can copy this project to get all the "boilerplate" elements for an API project. Then begin adding and modifying these elements to create the new API. 

The API registers itself as an instance of an application (appId) when the Mule application starts. When the registration process
finishes, a heartbeat process is started. When the Mule application stops, the heartbeat is stopped and the application unregisters
itself with the service registry.

The flows for these dynamic registration processes are found in the dynamic-service-registration.xml Mule config file. 

### Configuration Properties

The configuration properties are stored in the src/main/resources-filtered/mule4-skeleton-proxy-${mule.env}.yaml file:

* api.id contains the Api Manager instance's autodiscovery api id,
* api.base is the base uri for the API, the default is the project name taken from the pom file,
* api.port is the port number of the Api's HTTP listener,
* my.client-id is the client id this API uses to call other APIs
* my.client-secret is the client secret registered for this API to use for calling other APIs
* register.appId is the application name registered with the registry service,
* register.instanceid is the instance id to register,
* register.ipAddress, register.vipAddress and register.secureVipAddress are the network addresses to register. These are where the API can be reached when being invoked,
* register.homePageUrl, register.healthCheckUrl and register.statusPageUrl and the absolute urls to use for accessing these functions of the API.
* serviceRegistry.host is the network address to use for reaching the service registry,
* serviceRegistry.port is the port to use for accessing the registry,
* serviceRegistry.base is the common url base of the registry urls,
* serviceRegistry.heartbeatMS is the milliseconds between heartbeats,
* serviceRegistry.timeoutMS is the amount of time to wait for the registry to respond to an invocation.

Note that the api.port is not used if a shared domain is configured and used for the HTTP listener configuration.


## Runtime properties

The properties mule.env and mule.key need to be set in the Mule runtime in order for the property configurations to be located. Additionally, if the Mule runtime is not configured to connect to API Manager, the API will be disabled (by default).

For Studio, add the following VM command line values when running the API:

 -Danypoint.platform.gatekeeper=disabled -Dmule.env=local -Dmule.key=Mulesoft12345678

## Maven Settingss

The Mule deployment assumes that certain deployment properties will come from profiles specified when the mvn command is executed. An example-settings.xml is provided as a reference
for creating your own settings.xml file. This is a standard feature of Maven and is described in its online documentation. Once the settings.xml file has been created, export a u and a p shell environment variables containing your Anypoint user name and password. Then use this maven command to deploy the API project:

```
mvn clean install deploy -Denv=xxx -Dinstance.id=xxx -DmuleDeploy
```
Replacing the xxx's with the appropriate values.


