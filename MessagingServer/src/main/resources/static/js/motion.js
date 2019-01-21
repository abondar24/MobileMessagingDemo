$(document).ready(function () {
    let host="localhost";
    let port = 61614;
    let clientID = Math.random().toString(12);

    let devices = {};
    let mqttTopic = "mqtt/topic";
    let mqttAlertTopic = "mqtt/alert/topic";

    let client = new Paho.MQTT.Client(host,Number(port),clientID);

    client.onConnectionLost = function (response) {
        if (response.errorCode !==0){
            alert(response.errorMessage +" "+response.errorCode)
        }
    };

    client.connect({
        onSuccess: function () {
            client.subscribe(mqttTopic);

            client.onMessageArrived = function (message) {

                let data = message.payloadBytes;
                let dataStr = String.fromCharCode.apply(null, new Uint16Array(data));
                let msg = JSON.parse(dataStr);

                let deviceID = msg.id;
                let pitch = msg.pitch;
                let roll = msg.roll;
                let yaw = msg.yaw;

                updateSparkLines(deviceID,pitch,roll,yaw);
            }
        },
        onFailure: function (failure) {
            alert(failure.errorMessage);
        }
    });

    function updateSparkLines(deviceID,pitch,roll,yaw) {
        let values = devices[deviceID];

        if (!values) {
            let item = $('#devices').append(
                $('<li>').attr("id", deviceID).append(
                    $('<label>').text(deviceID),
                    $('<button>').text("Alert!").click(function () {
                        sendAlert(deviceID);
                    }),
                    $('<br>'),
                    $('<div>').attr('class', 'data')
                )
            );

            values = {
                "pitch": [],
                "roll": [],
                "yaw": [],
            };
        }

        values.pitch.push(pitch);
        values.roll.push(roll);
        values.yaw.push(yaw);

        if (values.pitch.length > 50) {
            values.pitch.splice(0, 1);
            values.roll.splice(0, 1);
            values.yaw.splice(0, 1);
        }

        devices[deviceID] = values;

        $('#'+ deviceID + ' .data').sparkline(values.pitch, {
            width: values.pitch.length * 5,
            tooltipPrefix: "pitch:",
            lineColor: 'red',
            fillColor: false,
            chartRangeMin: -3,
            chartRangeMax: 3,
            height: '36px'
        });

        $('#'+ deviceID + ' .data').sparkline(values.roll, {
            tooltipPrefix: "roll:",
            lineColor: 'green',
            composite: true,
            fillColor: false,
            chartRangeMin: -3,
            chartRangeMax: 3
        });

        $('#'+ deviceID + ' .data').sparkline(values.yaw, {
            tooltipPrefix: "yaw:",
            lineColor: 'blue',
            composite: true,
            fillColor: false,
            chartRangeMin: -3,
            chartRangeMax: 3
        });
    }

        function sendAlert() {
            let message = new Paho.MQTT.Message("Device alert");
            message.destinationName =mqttAlertTopic;
            client.send(message);
        }

});