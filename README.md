# MobileMessagingDemo
STOMP/MQTT usage with Android and Web


Idea: Show usage of STOMP and MQTT protocols via Web and with mobile client(based on Android);

## STOMP Demo: Location tracker

Grabs phone location sends it via STOMP to the topic on the broker and then server and web client read it. 
Web client shows it on the map using Google Maps API.

## MQTT Demo: Motion Capturer

Grabs motion changes of the phone sends it via MQTT to the topic on the broker and then server and web client read it. 
Web client shows a graph of motion changes. It is possible to send an alert message from the web client to the device where it is shown.


## Build and run

Deploy mobile app on the device.

Server can be run

```yaml

mvn clean install
java -jar target/MessagingServer-<version>.jar

or

mvn clean install spring-boot:run
```
