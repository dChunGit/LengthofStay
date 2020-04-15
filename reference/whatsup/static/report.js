'use strict';

$(document).click(function(event) {
  if (!$(event.target).closest(".reportmodal,.addReportButton,.container").length) {
    document.getElementById("reportmodal").style.display = "none"
  }
});

window.addEventListener('load', function () {
  document.getElementById('sign-out').onclick = function () {
    firebase.auth().signOut();
    console.log("Logged out")
  };

  firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        user.getIdToken().then(function (token) {
          document.cookie = "token=" + token;
        });
      } else {
        // Update the login state indicators.
        document.getElementById('sign-out').textContent = "Signed Out";
        // Clear the token cookie.
        document.cookie = "token=";
        window.location.href = window.location.origin;
      }
    }, function (error) {
    console.log(error);
    alert('Unable to log in: ' + error)
  });
});

window.addEventListener("load", function() {
  document.getElementById('reportform').addEventListener("submit", function(e) {
    e.preventDefault(); // before the code
    /* do what you want with the form */
    validateForm();
    hideForm();

    return false;
  });
});

function validateForm() {
  console.log("Validating")
  var event = document.forms["reportform"]["event"].value;
  var comments = document.forms["reportform"]["comments"].value;
  // if event not selected (will autopopulate later)
  sendNewReport(event, comments);

}

function showForm() {
  // load events
  document.getElementById("reportmodal").style.display = "block"
}

function hideForm() {
  document.getElementById("reportmodal").style.display = "none"
}