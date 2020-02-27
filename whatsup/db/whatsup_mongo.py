import datetime
from mongoengine import *
import mongoengine_goodjson as gj
from .whatsup_error import WhatsupError


class Report(gj.Document):
    username = StringField(max_length=30)
    event_id = ObjectIdField()
    comments = StringField(max_length=140)
    time = DateTimeField()
    report_image = StringField(max_length=80)
    category = StringField(max_length=40)

    def __str__(self):
        return self.username + " | " + str(
            get_event_by_id(self.event_id)) + " | " + self.category + " | " + self.comments

    def get_report_event(self):
        return get_event_by_id(self.event_id)


class ReportCategory(gj.Document):
    name = StringField(max_length=40, required=True, unique=True)

    def __str__(self):
        return self.name


class Event(gj.Document):
    title = StringField(max_length=80, required=True)
    description = StringField(max_length=300)
    time = DateTimeField()
    location = StringField(max_length=100)
    geo_latitude = StringField()
    geo_longitude = StringField()
    status = BooleanField()
    last_updated_user = StringField(max_length=30)
    first_updated_time = DateTimeField()
    last_updated_time = DateTimeField()
    cover_image = StringField(max_length=80)
    reports = ListField(ReferenceField(Report))
    category = StringField(max_length=40)

    def __str__(self):
        return ("Title: " + self.title + " | Author: " + self.last_updated_user + " | Category: " + self.category
                + " | Time: " + str(self.time) + " | Location: " + str(self.location) + " | Status: " + str(self.status)
                + " | Description: " + self.description)


class EventCategory(gj.Document):
    name = StringField(max_length=40, required=True, unique=True)

    def __str__(self):
        return self.name


class User(gj.Document):
    email = EmailField(required=True, unique=True)
    name = StringField(max_length=30, required=True, unique=True)
    karma = IntField()
    subscribed_events = ListField(ReferenceField(Event))
    reports = ListField(ReferenceField(Report))

    def __str__(self):
        return ("Email: " + self.email + " | Name: " + self.name + " | Karma: " + str(self.karma) +
                " | Subscribed Event: " + str(self.subscribed_events) + " | Reports: " + str(self.reports))

    # Effect: Update user info in database.
    #         Raise error if target username has been taken.
    def update_info(self, name=None):
        if check_username_exist(name):
            raise WhatsupError(0)
        kwargs = get_valid_kwargs(name=name)
        if kwargs:
            self.update(**kwargs)

    # Effect: Add a new event to database, and auto-subscribe the event for user. Return the id of new event.
    def add_event(self, title, description, time, location, status, cover_image="", geo_latitude=None,
                  geo_longitude=None, category=None):
        curr_time = datetime.datetime.now()
        new_event = Event(title=title, last_updated_user=self.name, description=description, time=time,
                          location=location, status=status, first_updated_time=curr_time, last_updated_time=curr_time,
                          cover_image=cover_image, reports=[])
        new_event.save()
        if geo_latitude is not None and geo_longitude is not None:
            new_event.update(geo_latitude=geo_latitude, geo_longitude=geo_longitude)
        if category:
            add_event_category(category)
            new_event.update(category=category)
        self.subscribe_event(new_event)
        return new_event

    # Effect: Update event in database, and auto-subscribe the event for user.
    # Require: event is in database.
    def update_event(self, event, title=None, description=None, time=None, location=None, geo_latitude=None,
                     geo_longitude=None, category=None, status=None):
        kwargs = get_valid_kwargs(title=title, description=description, time=time, location=location,
                                  geo_latitude=geo_latitude, geo_longitude=geo_longitude, category=category,
                                  status=status)
        if kwargs:
            curr_time = datetime.datetime.now()
            kwargs.update(last_updated_user=self.name, last_updated_time=curr_time)
            event.update(**kwargs)
            self.subscribe_event(event)

    # Effect: Add a new report to database
    # Require: reported event is in database
    def add_report(self, event_id, comments, photo_name="", category=None):
        event = get_event_by_id(event_id)
        if event is not None:
            current_time = datetime.datetime.now()
            new_report = Report(username=self.name, event_id=event.id, comments=comments, time=current_time,
                                report_image=photo_name)

            try:
                new_report.save()
                if category:
                    add_report_category(category)
                    new_report.update(category=category)
                event.update(add_to_set__reports=new_report)
                self.update(add_to_set__reports=new_report)
                return new_report
            except:
                return None

    # Effect: Update report info in database
    def update_report(self, report, event_id=None, comments=None, category=None):
        kwargs = get_valid_kwargs(event_id=event_id, comments=comments, category=category)
        if kwargs:
            current_time = datetime.datetime.now()
            kwargs.update(time=current_time)
            try:
                report.update(**kwargs)
                return True
            except:
                return False

    # Effect: get the lastest three reports info in database
    def get_last_three_reports(self):
        length = len(self.reports)
        if length < 4:
            return reversed(self.reports)
        else:
            result = []
            for i in range(length - 1, length - 4, -1):
                result.append(self.reports[i])
            return result

    # Effect: Delete report from database
    def delete_report(self, report):
        try:
            self.update(pull__reports=report)
            event = get_event_by_id(event_id=report.event_id)
            event.update(pull__reports=report)
            report.delete()
            return True
        except:
            return False

    def subscribe_event(self, event):
        self.update(add_to_set__subscribed_events=event)

    def unsubscribe_event(self, event):
        self.update(pull__subscribed_events=event)

    def check_subscribe_status(self, event):
        return event in self.subscribed_events


# Effect: Remove arguments with value None.
def get_valid_kwargs(**kwargs):
    lst = []
    for i in kwargs:
        if kwargs[i] is None:
            lst.append(i)
    for i in lst:
        del kwargs[i]
    return kwargs


# Effect: Check whether the username exists in database.
def check_username_exist(username):
    user_list = User.objects(name=username)
    if user_list:
        return True
    else:
        return False


# Effect: Check whether the email exists in database.
def check_email_exist(email):
    user_list = User.objects(email=email)
    if user_list:
        return True
    else:
        return False


# Effect: Add a new user to database.
#         Since email is unique, only save if user doesn't exist already.
def add_user(email, name):
    if not check_email_exist(email):
        new_user = User(email=email, name=name, karma=0, subscribed_events=[], reports=[])
        new_user.save()
        return new_user
    return get_user(email=email)


# Effect: Retrieve a user by user id or email, if user id or email is not None.
#         Retrieve a list of user if user id and email are both None.
#         Raise error if user_id or email is invalid.
def get_user(user_id=None, email=None):
    try:
        if user_id is not None:
            return User.objects.get(id=user_id)
        elif email is not None:
            return User.objects.get(email=email)
        else:
            return User.objects()
    except DoesNotExist:
        raise WhatsupError(1)


# Effect: Retrieve a list of events specified by arguments. That an argument is None
#         indicates no constraints on this field.
def get_events(title=None):
    if title is not None:
        return Event.objects(title=title)
    else:
        return Event.objects()


# Effect: Retrieve an event by its id. Returns only 1 event as id is unique.
#         Raise error if event id is invalid.
def get_event_by_id(event_id):
    try:
        return Event.objects.get(id=event_id)
    except DoesNotExist:
        raise WhatsupError(2)


# Effect: Get a list of reports specified by arguments. A None argument indicates
#         there are no constraints on the field.
def get_reports(email=None, event_id=None):
    if email is not None and event_id is not None:
        queried_user = User.objects(email=email).first()

        if queried_user is not None:
            return Report.objects(username=queried_user.name, event_id=event_id)

    elif email is not None:
        queried_user = User.objects(email=email).first()

        if queried_user is not None:
            return Report.objects(username=queried_user.name)

    elif event_id is not None:
        return Report.objects(event_id=event_id)

    print("getting reports")
    return Report.objects()


# Effect: Get a list of reports specified by id. Returns a single report
#         if report id is specified, otherwise returns all user reports
def get_reports_by_id(report_id):
    if report_id is not None:
        return Report.objects(id=report_id).first()

    return None


def search_reports(query, event_id=None):
    kwargs = get_valid_kwargs(event_id=event_id)
    return Report.objects(comments__icontains=query, **kwargs)


def add_event_category(cat_name):
    if not cat_name:
        return

    category_list = EventCategory.objects(name=cat_name)
    if not category_list:
        new_category = EventCategory(name=cat_name)
        new_category.save()


def add_report_category(cat_name):
    if not cat_name:
        return

    category_list = ReportCategory.objects(name=cat_name)
    if not category_list:
        new_category = ReportCategory(name=cat_name)
        new_category.save()


def get_event_category():
    return EventCategory.objects()


def get_report_category():
    return ReportCategory.objects()


def get_event_by_category(cat_name):
    return Event.objects(category=cat_name)


def get_report_by_category(cat_name, event_id=None):
    kwargs = get_valid_kwargs(event_id=event_id)
    return Report.objects(category=cat_name, **kwargs)
