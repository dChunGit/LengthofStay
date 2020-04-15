function getEvents(selected_id) {
    $.get(window.location.origin + "/event", function(data, status) {
        console.log(data)
        injectReportData(data, selected_id);
    });
}

function injectReportData(data, selected_id) {
    data.forEach(element => {
        console.log(element);
        var template = "<option value=\"{{id}}\">{{title}}</option>"
        var selected_template = "<option value=\"{{id}}\" selected>{{title}}</option>"
        console.log(element.id + " " + selected_id);

        if(element.id == selected_id) {
            $("#event_report").append(Mustache.render(selected_template, element));
        } else {
            $("#event_report").append(Mustache.render(template, element));
        }
    });
}