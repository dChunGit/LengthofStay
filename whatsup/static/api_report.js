$(document).ready(function() {
    $.ajaxSetup({cache: false});
    getReports();
})

function getReports() {
    $.get(window.location.origin + "/report", function(data, status) {
        var json_response = JSON.parse(data);
        // console.log(json_response);

        injectReportData(json_response);
    });
}

function getUserReports(userid) {
    $.get(window.location.origin + "/report/" + userid, function(data, status) {
        var json_response = JSON.parse(data);
        // console.log(json_response);

        injectReportData(json_response);
    });
}

function searchReports() {
    var search = $("#search-reports").val();
    console.log(search);
}

function postReport(userid, event, text) {
    $.ajax({
        type:'post',
        url: window.location.origin + "/report/" + userid,
        data: {
            eventid: event,
            comment: text
        },
        success: function() {
            getReports();
        },
        error: function() {
            alert("Oops, couldn't save your report. Please try again!");
        }
    })
}

function injectReportData(data) {
    $.get("static/reportcard.html", function(template) {
        $("#reports tr").remove();
        data.forEach(element => {
            console.log(element);
            $("#reports").append(Mustache.render(template, element));
        });
    });
}