if (firebase.apps.length === 0) {
    var firebaseConfig = {
        apiKey: "AIzaSyBTI7tR_VelmxLObp5WAKgHxDt3MHiBGgc",
        authDomain: "whats-up-255316.firebaseapp.com",
        databaseURL: "https://whats-up-255316.firebaseio.com",
        projectId: "whats-up-255316",
        storageBucket: "whats-up-255316.appspot.com",
        messagingSenderId: "772241918644",
        appId: "1:772241918644:web:f81919b112d7b8d45965e1",
        measurementId: "G-25Y1X3K0F5"
    };
    // Initialize Firebase
    firebase.initializeApp(firebaseConfig);
    firebase.analytics();
}
