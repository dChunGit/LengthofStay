'use strict';

window.addEventListener('load', function () {
  document.getElementById('sign-out-div').onclick = function () {
    firebase.auth().signOut();
  };

  var uiConfig = {
    signInFlow: 'popup',
    signInSuccessUrl: '/home',
    signInOptions: [
      firebase.auth.GoogleAuthProvider.PROVIDER_ID,
      firebase.auth.EmailAuthProvider.PROVIDER_ID,
    ],
    tosUrl: '</home>'
  };

  firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
      // User is signed in, so display the "sign out" button and login info.
      document.getElementById('sign-out-div').hidden = false;
      console.log(`Signed in as ${user.displayName} (${user.email})`);
      user.getIdToken().then(function (token) {
        document.cookie = "token=" + token;
      });
      sendUser(user);
    } else {
      // Initialize the FirebaseUI Widget using Firebase.
      var ui = new firebaseui.auth.AuthUI(firebase.auth());
      ui.start('#firebaseui-auth-container', uiConfig);
      // Update the login state indicators.
      document.getElementById('sign-out-div').hidden = true;
      // Clear the token cookie.
      document.cookie = "token=";
    }
  }, function (error) {
    console.log(error);
    alert('Unable to log in: ' + error)
  });
});

function sendUser(user) {
  console.log("Sending User")
  $.ajax({
    type:'post',
    url: window.location.origin + "/user",
    data: {
        email: user.email,
        username: user.displayName
    },
    success: function() {
      console.log("SUCCESS")
      window.location.href = window.location.origin + '/home';
    },
    error: function() {
        alert("Oops, couldn't log in. Please try again!");
    }
  });
}