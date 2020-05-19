# Web-Push(Chrome/Firefox/Safari)


A Java website which shows how to register for Web Push notifications for major browsers(Chrome, Firefox and Safari). Chrome & Firefox push uses "Service Worker" concept along-with VAPID keys, while Safari uses 
"Push package" and APNS to register for web push notifications.

**Note:** Ensure that you have Jetty's ALPN JAR (OkHttp requires it) in your boot classpath. [See here for more information](http://www.eclipse.org/jetty/documentation/current/alpn-chapter.html).
This is required until Java 9 is released, as Java 8 does not have native support for HTTP/2. This is because we will be using "Clevertap/apns-http2" libraray to push
web notifications to safari once user grants permission.

## Setup Safari Web Push

### Create Safari Web Push certificate(.p12) and push package(.zip)

[See here for more information](https://github.com/puranjay2809/WebPush/blob/master/Safari%20Push%20Notification%20Setup.pdf)

### Create a Tomcat Server

1. Download Tomcat server from [here](https://downloads.apache.org/tomcat/tomcat-8/v8.5.55/bin/apache-tomcat-8.5.55.zip)
and edit server.xml under conf folder 
```
<Connector port=“8443” protocol=“HTTP/1.1" SSLEnabled=“true”
               maxThreads=“150" scheme=“https” secure=“true”
               clientAuth=“false” sslProtocol=“TLS”
         keystoreFile=“{User keystore file path}”
         keystorePass=“{your keystore pass}" />
```
**Note:** use keystoreFile and keystorePass generated in step 2

2. Create a keystore file [See here for more information](https://docs.oracle.com/cd/E19118-01/n1.sprovsys51/819-1655/fapsf/index.html) and update the info in pom.xml
```
<configuration>
    <!--Keystore file path for https service-->
    <httpsPort>8443</httpsPort>
    <keystoreFile>{User keystore file path}</keystoreFile>
    <keystorePass>{your keystore pass}</keystorePass>
</configuration>
```
This is required because Safari backend service to handle the push package should be hosted on https. The same https url is mentioned in Push Package's website.json
```
{
    "websiteName": "Safari Push Demo",
    "websitePushID": "{web push ID generated}",
    "allowedDomains": ["http://localhost", "http://localhost:2500", "http://127.0.0.1:2500", "https://localhost:8443", "http://127.0.0.1", "https://localhost"],
    "urlFormatString": "http://%@",
    "authenticationToken": "19f8d7a6e9fb8a7f6d9330dabe",
    "webServiceURL": "https://localhost:8443/Push_notification_war_exploded"
}
```
3. If you are using intellij then while setting up local Tomcat server for the website, following configurations needs to be setup:
```
Add the Tomcate server downloaded earlier
URL: http://localhost:2500/Push_notification_war_exploded/
VM Options: -Xbootclasspath/p:/{path to}/lib/alpn-boot-8.1.13.v20181017.jar
HTTP Port: 2500
HTTPS port: 8443
JMX port: 1090
Add the artifact(with name "Push-notification:war exploded") under Deployment tab and "Application Context" as "/Push_notification_war_exploded"
```
4. Run the server

### Build your notification
The notification builder of clevertap/apns-http2 supports several other features (such as badge, category, etc) for ios apps.
The minimal is shown below which is supported in Safari Web Push:

```
Notification n = new Notification.Builder(token)
                    .alertBody("This is a Safari Notification!").alertTitle("Eureka").urlArgs(new String[]{"www.google.com"}).build();

```

### Send the notification

```
FileInputStream cert = new FileInputStream({path to your certificate.p12});
  //Using Clevertap http2 APNS
  final ApnsClient client = new ApnsClientBuilder()
      .inSynchronousMode()
      .withProductionGateway(true)
      .withCertificate(cert)
      .withPassword("tap1234")
      .withDefaultTopic("")
      .withPort(2197)
      .build();
            
```
