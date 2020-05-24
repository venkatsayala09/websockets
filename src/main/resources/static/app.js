var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#notifications").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/username/topic/notifications', function (notification) {
            console.log('Received WS message: '+ notification.body);
            showNotification(JSON.parse(notification.body).content);
        });
        stompClient.subscribe('/topic/registrations', function (registration) {
            console.log('Received WS message: '+ registration.body);
            showRegistration(JSON.parse(registration.body).name,  JSON.parse(registration.body).url );
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/notify", {}, JSON.stringify({'content': $("#name").val()}));
}

function register() {
    stompClient.send("/app/register", {}, JSON.stringify({'name': $("#serviceName").val(), 'url': $("#serviceURL").val()}));
}

function showNotification(message) {
    $("#notifications").append("<tr><td>" + message + "</td></tr>");
}

function showRegistration(name, url) {
    $("#registrations").append("<tr><td>" + name + "</td><td>" + url + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#register" ).click(function() { register(); });
});

