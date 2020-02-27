import datetime
from flask import Flask, render_template, request, jsonify, Response, redirect, url_for, flash, get_flashed_messages
import json
import re
from base64 import b64encode
from google.auth.transport import requests
import google.oauth2.id_token
from mongoengine import *
from werkzeug.utils import secure_filename
import db.googlecloud as cloud
from db.whatsup_error import WhatsupError
import db.whatsup_mongo as db

ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif', 'webp'])

firebase_request_adapter = requests.Request()
app = Flask(__name__)

app.config['RESULT_STATIC_PATH'] = "static/"
app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0
app.jinja_env.filters['b64d'] = lambda u: b64encode(u).decode()

connect('whatsupv4', host='mongodb+srv://dchun:whatsupadmin@whatsup-rwt91.mongodb.net')


@app.route('/')
def root():
    id_token = request.cookies.get("token")
    error_message = None
    claims = None
    times = None

    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
        except ValueError as exc:
            error_message = str(exc)
            print(error_message)

    return render_template(
        'index.html',
        user_data=claims, error_message=error_message, times=times)


@app.route("/user", methods=["POST"])
def user():
    print("Adding user")
    email = request.form.get('email')
    username = request.form.get('username')
    new_user = db.add_user(email=email, name=username)

    if 'Mobile' in request.headers:
        return app.response_class(
            response=new_user.to_json(),
            status=200,
            mimetype="text/plain"
        )
    else:
        return Response(status=200)


@app.route("/reports")
def reports():
    id_token = request.cookies.get("token")
    claims = None
    error_message = None

    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            email = claims["email"]
            print("Current user is: " + email)
            user = db.get_user(email=email)
            if user is not None:
                claims["userid"] = str(user.id)
                event_id = request.args.get('event_id')
                report_id = request.args.get('report_id')

                event = None
                report = None
                reports = None
                content_type = None
                photo = None
                report_image = None

                if report_id is not None:
                    print("get specific report")
                    report = db.get_reports_by_id(report_id)
                    if report.report_image is not None:
                        report_image = report.id

                if event_id is None and report_id is None:
                    print("event is none")
                    reports = db.get_reports()

                if event_id is not None:
                    print("event is not none")
                    event = db.get_event_by_id(event_id=event_id)
                    reports = db.get_reports(event_id=event_id)
                    if reports is not None:
                        reports = reports.order_by('event_id', 'time')

                if 'Mobile' in request.headers:
                    if report_id is not None:
                        response = app.response_class(
                            response=json.dumps(report.to_json()),
                            status=200,
                            mimetype='application/json'
                        )
                    else:
                        response = app.response_class(
                            response=json.dumps(reports.to_json()),
                            status=200,
                            mimetype='application/json'
                        )
                    return response

                else:
                    kwargs = get_valid_kwargs(event=event, report=report, reports=reports, report_image=report_image)
                    return render_template('report.html', user_data=claims, **kwargs)

            else:
                return redirect(url_for('root'))

        except ValueError as exc:
            error_message = str(exc)
            print(error_message)

    return redirect(url_for('root'))


@app.route("/add_report", methods=["GET", "POST"])
def add_report():
    id_token = request.cookies.get("token")
    claims = None
    error_message = None

    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            if request.method == "GET":
                events = db.get_events()
                selected_event = request.args.get('event_id')
                if selected_event is None:
                    selected_event = ""
                return render_template('add-report.html', user_data=claims, events=events,
                                       selected_event=selected_event)

            if request.method == "POST":
                user = db.get_user(email=claims["email"])
                event = db.get_event_by_id(event_id=request.form['event'])
                image_filename = None
                content_type = None

                print(event.title + " " + request.form['comment'])

                if 'React' in request.headers and len(request.form) == 5:
                    image_filename = request.form['image_name']
                    content_type = request.form['image_type']
                    image_string = request.form['image_data']
                    cloud.create_file(image_filename, content_type, image_string)
                elif 'report_image' in request.files:
                    image = request.files['report_image']

                    if image and re.search("image\/.*", image.content_type):
                        image_filename = secure_filename(image.filename)
                        content_type = image.content_type
                        image_string = b64encode(image.read())
                        cloud.create_file(image_filename, content_type, image_string)

                if 'Mobile' in request.headers:
                    report = user.add_report(event_id=event.id, comments=request.form['comment'],
                                             category=request.form['category'], photo_name=image_filename)
                else:
                    report = user.add_report(event_id=event.id, comments=request.form['comment'],
                                             photo_name=image_filename)

                if report is None:
                    return redirect(url_for('reports'))

                if 'Mobile' in request.headers:
                    print(report.to_json())
                    return app.response_class(
                        response=report.to_json(),
                        status=200,
                        mimetype='application/json'
                    )

                return redirect(url_for('reports', event_id=event.id, report_id=report.id))

        except ValueError as exc:
            error_message = str(exc)
            print(error_message)

    return redirect(url_for('root'))


@app.route("/searchreports", methods=["GET", "POST"])
def searchreports():
    id_token = request.cookies.get("token")
    claims = None
    error_message = None

    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)

            event_id = request.args.get('event_id')
            kwargs = get_valid_kwargs(event_id=event_id)

            if request.method == "GET":
                query = request.args.get('query')
                event_id = request.args.get('event_id')
                if query != "":
                    reports = db.search_reports(query, event_id)
                    if reports is not None:
                        reports = reports.order_by('event_id', 'time')

                    return render_template('searchreports.html', query=query, reports=reports, **kwargs)
                else:
                    return redirect(url_for('reports', **kwargs))

            if request.method == "POST":
                if 'query' in request.form:
                    return redirect(url_for('searchreports', query=request.form['query'], **kwargs))
                else:
                    return redirect(url_for('reports', **kwargs))

        except ValueError as exc:
            error_message = str(exc)
            print(error_message)

    return redirect(url_for('root'))


@app.route("/report", methods=["GET"])
def allreports():
    event = request.args.get('event')
    image_id = request.args.get('image_id')
    reports = None

    if image_id is not None:
        report = db.get_reports_by_id(image_id)
        if report.report_image is not None:
            report_image = cloud.read_file(report.report_image)
            return app.response_class(
                response=report_image,
                status=200,
                mimetype='text/plain'
            )
        else:
            return get_response_status(False)

    if event is not None:
        reports = db.get_reports(event_id=event)
    else:
        reports = db.get_reports()

    if reports is not None:
        response = app.response_class(
            response=reports.to_json(),
            status=200,
            mimetype='application/json'
        )
        return response
    else:
        return get_response_status(False)


@app.route("/report/<userid>", methods=["GET", "POST"])
def userreports(userid):
    method = request.method

    user = db.get_user(user_id=userid)

    if user is None:
        return Response(status=400)

    if method == "GET":
        event = request.args.get('event')

        reports = None
        if event is not None:
            reports = db.get_reports(event_id=event)
        else:
            reports = db.get_reports_by_id(user)

        response = app.response_class(
            response=json.dumps(reports.to_json()),
            status=200,
            mimetype='application/json'
        )
        return response

    elif method == "POST":
        data = request.form

        if "eventid" in data and "comment" in data:
            user = db.get_user(user_id=userid)
            result = user.add_report(data["eventid"], data["comment"], data["category"])
        else:
            result = False

        return get_response_status(result)

    else:
        return Response(status=405)


@app.route("/report/<userid>/<reportid>", methods=["GET", "POST", "DELETE"])
def report(userid, reportid):
    method = request.method

    user = db.get_user(user_id=userid)
    report = db.get_reports_by_id(report_id=reportid)

    if report is None:
        return Response(status=400)

    if method == "GET":
        response = app.response_class(
            response=json.dumps(report.to_json()),
            status=200,
            mimetype='application/json'
        )
        return response

    elif method == "POST":
        data = request.form

        event = None
        comment = None
        if "eventid" in data:
            event = db.get_event_by_id(data["eventid"])
        if "comment" in data:
            comment = data["comment"]

        result = user.update_report(report, event, comment)
        return get_response_status(result)

    else:
        result = user.delete_report(report)
        return get_response_status(result)


def get_response_status(result):
    if result:
        return Response(status=200)
    else:
        return Response(status=400)


@app.route('/post-event', methods=['GET', 'POST'])
def post_event():
    id_token = request.cookies.get('token')
    if id_token:
        error_message = None
        if request.method == 'POST':
            try:
                claims = google.oauth2.id_token.verify_firebase_token(
                    id_token, firebase_request_adapter)
                user = db.get_user(email=claims['email'])

                image_filename = None
                print(len(request.form))
                if 'React' in request.headers and len(request.form) == 9:
                    image_filename = request.form['image_name']
                    content_type = request.form['image_type']
                    image_string = request.form['image_data']
                    cloud.create_file(image_filename, content_type, image_string)

                elif 'cover_image' in request.files:
                    image = request.files['cover_image']
                    if image and isAllowedFile(image.filename):
                        image_filename = secure_filename(image.filename)
                        content_type = image.content_type
                        image_string = b64encode(image.read())
                        cloud.create_file(image_filename, content_type, image_string)

                if 'Mobile' in request.headers:
                    event = user.add_event(title=request.form['title'], description=request.form['description'],
                                           time=datetime.datetime.strptime(request.form['datetime'],
                                                                           '%Y-%m-%dT%H:%M'),
                                           location=request.form['location'], status=True,
                                           cover_image=image_filename, geo_latitude=request.form['geo_latitude'],
                                           geo_longitude=request.form['geo_longitude'], category=request.form['category'])
                    return app.response_class(
                        response=event.to_json(),
                        status=200,
                        mimetype='application/json'
                    )
                else:
                    event = user.add_event(title=request.form['title'], description=request.form['description'],
                                           time=datetime.datetime.strptime(request.form['datetime'],
                                                                           '%Y-%m-%dT%H:%M'),
                                           location=request.form['location'], status=True,
                                           cover_image=image_filename)

                    return redirect(url_for('view_single_event', event_id=event.id))
            except WhatsupError as we:
                error_message = we.error_message
                print(error_message)

        return render_template('post-event.html', error_message=error_message)
    else:
        return redirect(url_for('root'))  # may change to login if we have login page


@app.route('/event/<event_id>')
def view_single_event(event_id):
    id_token = request.cookies.get('token')
    if id_token:
        event = None
        user = None
        error_message = None

        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])
            event = db.get_event_by_id(event_id)
            cover_image = None

            if event.cover_image is not None:
                cover_image = event.id

            if 'Mobile' in request.headers:
                if event is not None:
                    return app.response_class(
                        response=event.to_json(),
                        status=200,
                        mimetype='application/json'
                    )
                else:
                    return app.response_class(status=404)

            kwargs = get_valid_kwargs(event=event, user=user, error_message=error_message, cover_image=cover_image)
            return render_template('view-single-event.html', **kwargs)

        except WhatsupError as we:
            error_message = we.error_message
            print(error_message)
            return redirect(url_for('root'))

    else:
        return redirect(url_for('root'))


@app.route('/events')
def view_all_events():
    id_token = request.cookies.get('token')
    if id_token:
        event_list = None
        user = None
        error_message = None

        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])
            event_list = db.get_events()

            if 'Mobile' in request.headers:
                return app.response_class(
                    response=event_list.to_json(),
                    status=200,
                    mimetype='application/json'
                )

        except WhatsupError as we:
            error_message = we.error_message
            print(error_message)

        return render_template('view-all-events.html', event_list=event_list, user=user, error_message=error_message)

    else:
        return redirect(url_for('root'))


@app.route('/subscribe-event/<event_id>/<next_url>', methods=['POST'])
def subscribe_event(event_id, next_url):
    id_token = request.cookies.get('token')
    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])
            event = db.get_event_by_id(event_id)

            if request.form['subscribe_status'] == 'Subscribe':
                user.subscribe_event(event)
            elif request.form['subscribe_status'] == 'Unsubscribe':
                user.unsubscribe_event(event)

            print(event_id, next_url)

            if 'Mobile' in request.headers:
                return app.response_class(
                    response=user.to_json(),
                    status=200,
                    mimetype='application/json'
                )

            if next_url == 'view_single_event':
                return redirect(url_for(next_url, event_id=event_id))
            elif next_url == 'view_all_events':
                return redirect(url_for(next_url))
            elif next_url == 'home':
                return redirect(url_for('home'))
            else:
                return redirect(url_for('root'))

        except WhatsupError as we:
            print(we.error_message)

    else:
        return redirect(url_for('root'))


@app.route("/event", methods=["GET"])
def allevents():
    image_id = request.args.get('image_id')
    if image_id is not None:
        event = db.get_event_by_id(image_id)
        cover_image = cloud.read_file(event.cover_image)
        return app.response_class(
            response=cover_image,
            status=200,
            mimetype="text/plain"
        )

    events = None
    if 'event_id' in request.args:
        events = db.get_event_by_id(request.args.get('event_id'))
    else:
        print("getting all")
        events = db.get_events()

    response = app.response_class(
        response=events.to_json(),
        status=200,
        mimetype='application/json'
    )
    return response


@app.route("/users", methods=["GET"])
def getUser():
    id_token = request.cookies.get('token')
    if id_token:
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])
            return app.response_class(
                response=user.to_json(),
                status=200,
                mimetype='application/json'
            )

        except WhatsupError as we:
            error_message = we.error_message
            print(error_message)

    if 'user_id' in request.args:
        user = db.get_user(user_id=request.args.get('user_id'))
        return app.response_class(
            response=user.to_json(),
            status=200,
            mimetype='application/json'
        )
    else:
        users = db.get_user()
        return app.response_class(
            response=users.to_json(),
            status=200,
            mimetype='application/json'
        )


def isAllowedFile(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route("/event-category", methods=["GET"])
def get_event_category():
    event_cat_list = db.get_event_category()
    str_list = [event_cat.name for event_cat in event_cat_list]
    return app.response_class(
        response=json.dumps(str_list),
        status=200,
        mimetype='application/json'
    )


@app.route("/report-category", methods=["GET"])
def get_report_category():
    report_cat_list = db.get_report_category()
    str_list = [report_cat.name for report_cat in report_cat_list]
    return app.response_class(
        response=json.dumps(str_list),
        status=200,
        mimetype='application/json'
    )


# Effect: Remove arguments with value None.
def get_valid_kwargs(**kwargs):
    lst = []
    for i in kwargs:
        if kwargs[i] is None:
            lst.append(i)
    for i in lst:
        del kwargs[i]
    return kwargs


@app.route('/home')
def home():
    id_token = request.cookies.get('token')
    if id_token:
        user = None
        error_message = None
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])

        except WhatsupError as we:
            error_message = we.error_message
            print(error_message)
    return render_template('home.html', user=user, event_list=reversed(user.subscribed_events),
                           report_list=user.get_last_three_reports())


@app.route('/home/all_reports')
def home_all_reports():
    id_token = request.cookies.get('token')
    if id_token:
        user = None
        error_message = None
        try:
            claims = google.oauth2.id_token.verify_firebase_token(
                id_token, firebase_request_adapter)
            user = db.get_user(email=claims['email'])
        except WhatsupError as we:
            error_message = we.error_message
            print(error_message)
    return render_template('home-all-reports.html', user=user, report_list=reversed(user.reports))


@app.route('/contact')
def contact():
    return render_template('contact.html')


@app.route('/about')
def about():
    return render_template('about.html')


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8000, debug=True)
