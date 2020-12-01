# MobileMessagingDemo
STOMP/MQTT usage with Android and Web


Idea: Show usage of STOMP and MQTT protocols via Web and with mobile client(based on Android);

## STOMP Demo: Location tracker

Grabs phone location sends it via STOMP to the topic on the broker and then server and web client read it.
Web client shows it on the map using Google Maps API.

## MQTT Demo: Motion Capturer

Grabs motion changes of the phone sends it via MQTT to the topic on the broker and then server and web client read it.
Web client shows a graph of motion changes. It is possible to send an alert message from the web client to the device where it is shown.


Both demos are split into mobile client and server

- [Server side](MessagingServer/README.md)
- [Mobile client](MessagingClient/README.md)
