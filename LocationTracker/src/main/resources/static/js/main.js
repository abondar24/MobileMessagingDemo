$(document).ready(function () {
    let url = "ws://localhost:61614/";
    let topic = "stomp.topic";
    let client;

    let map, trackers = {};

    $("#connect_button").click(function () {
        connect(url);
        return false;
    });

    $("#disconnect_button").click(function () {
        disconnect();
        return false;
    });

    function connect(url) {

        client = Stomp.client(url);
        client.connect("", "", function (frame) {
            client.debug("Connection successful");

            $("#connect_button").prop("disabled", true);
            $("#disconnect_button").prop("disabled", false);

            client.subscribe(topic, function (message) {
                let data = JSON.parse(message.body);
                let deviceID = data.id;

                if (!$("#deviceID option[value='" + deviceID + "']").length) {
                    $('#deviceID').append($('<option>', {value: deviceID}).text(deviceID));
                }

                show(deviceID, data.lat, data.lon, data.alt);
            });
        });
    }

    function initMap() {
        let mapOptions = {
            zoom: 2,
            center: new google.maps.LatLng(49, 8),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map($("#map-canvas").get(0), mapOptions)


    }

    google.maps.event.addDomListener(window, 'load', initMap);

    function show(deviceId, lat, lon, alt) {
        let pos = new google.maps.LatLng(lat, lon);

        if (trackers[deviceId]) {
            trackers[deviceId].marker.setPosition(pos);
        } else {
            let marker = new google.maps.Marker({
                position: pos,
                map: map,
                title: deviceId
            });

            let infoWindow = new google.maps.InfoWindow({content: deviceId});

            trackers[deviceId] = {marker: marker};
            google.maps.event.addDomListener(marker,'click',function () {
                infoWindow.open(map,marker)
            });

        }
    }

    function disconnect() {
        client.disconnect(function () {
            for (let tk in trackers){
                trackers[tk].marker.setMap(null);
            }
            trackers = {};
        });

        $("#deviceID").empty();
        $("#connect_button").prop("disabled",false);
        $("#disconnect_button").prop("disabled",true);
    }

});