let subscriptionInfo;
let safariToken;
let subscriptionInfoSafari;

const check = function () {
    if (!('serviceWorker' in navigator)) {
        throw new Error('No Service Worker support!')
    }
    if (!('PushManager' in window)) {
        throw new Error('No Push API Support!')
    }
};

//Encode the URL
function urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
        .replace(/-/g, '+')
        .replace(/_/g, '/');

    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}


//Just to print the token or subscription info
const sending = function (subscription) {

    var xhttp = new XMLHttpRequest();
    var parameter;

    xhttp.open("POST", "http://localhost:2500/Push_notification_war_exploded/save");
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            document.getElementById("surprise").innerHTML = this.responseText;
        }
    };

    if ('safari' in window && 'pushNotification' in window.safari) {
        const permissionData =
            window.safari.pushNotification.permission('web.pjay.push');
        if(permissionData.permission !== 'granted') {
            throw new Error('Permission not granted for Notification in Safari. Cannot send one!');
        }
        parameter = encodeURI(JSON.stringify({token: safariToken}));
    }else{
        console.log(subscription.toJSON());
        parameter = encodeURI(JSON.stringify(subscription));
    }

    xhttp.send("data=" + parameter);
};



//Sending test push notifications to respective browsers
const pushNotification = function () {

    const xhttp = new XMLHttpRequest();
    let parameter;

    if ('safari' in window && 'pushNotification' in window.safari) {
        const permissionData =
            window.safari.pushNotification.permission('web.pjay.push');
        if(permissionData.permission !== 'granted') {
            throw new Error('Permission not granted for Notification in Safari. Cannot send one!');
        }
        xhttp.open("POST", "http://localhost:2500/Push_notification_war_exploded/push");
        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        parameter = encodeURI(safariToken);

    }else{
        //For Chrome & Firefox
        if (Notification.permission !== 'granted') {
            throw new Error('Permission not granted for Notification. Cannot send one!');
        }
        xhttp.open("POST", "http://localhost:2500/Push_notification_war_exploded/send");
        xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        // Get public key and user auth from the subscription object
        parameter = encodeURI(JSON.stringify({
            endpoint: subscriptionInfo.endpoint,
            key: subscriptionInfo.toJSON().keys.p256dh,
            // key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
            auth: subscriptionInfo.toJSON().keys.auth,
            // auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
        }));
    }

    xhttp.send("data=" + parameter);
};


//For Chrome & Firefox push registration
function registerServiceWorker() {

    //Public key in the VAPID keys the application key
    var application_server_key = urlBase64ToUint8Array('BFygpPBmFuCSAXq1UDxA-LNBM2gzYHbp6Xld16N0xXp962u7oVu4BMG0qoafzHXFR43aAJi51JpmboG5v8idtbQ');
    var options = {applicationServerKey: application_server_key, userVisibleOnly: true};

    var swRegistration = navigator.serviceWorker.register('service_new.js')//notice the file name
        .then(
            function (swRegistration) {
                swRegistration.pushManager.subscribe(options)
                    .then(
                        function (subscription) {

                            console.log(subscription.toJSON().keys.auth);
                            console.log(subscription.toJSON().keys.p256dh);
                            console.log(subscription.endpoint);

                            sending(subscription);
                            subscriptionInfo = subscription;
                        }
                    )
            });

    if (Notification.permission !== 'granted') {
        throw new Error('Permission not granted for Notification');
    }

    return swRegistration;
}

//For Safari push registration
function registerSafariPush() {
    if ('safari' in window && 'pushNotification' in window.safari) {

        var permissionData =
            window.safari.pushNotification.permission('web.pjay.push');
        window.safari.pushNotification.requestPermission(
            'https://localhost:8443/Push_notification_war_exploded',
            'web.pjay.push', {}, function(subscription) {
                console.log(subscription);
                if(subscription.permission === 'granted') {
                    subscriptionInfoSafari = subscription;
                    safariToken = subscription.deviceToken;
                    sending(safariToken);
                    // updateSubscriptionOnServer(subscription);
                }
                else if(subscription.permission === 'denied') {
                    // Nothing
                }
            });

    } else {
        alert("Push notifications not supported.");
    }
}


//Identify the browser and call for the appropriate request
function callPushRequest() {
    if ('safari' in window && 'pushNotification' in window.safari) {
        //For Safari
        registerSafariPush();
    }else{
        //For chrome and firefox
        registerServiceWorker();
    }
}


const main = function () {
    if (!'safari' in window) {
        check();
    }
};


main();