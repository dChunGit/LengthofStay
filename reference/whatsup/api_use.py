import datetime
import json
from mongoengine import *
from whatsup.db.whatsup_mongo import User, Event, Report
import whatsup.db.whatsup_mongo as db


def setup():
    """ Connect to database """
    connect('davidtest', host='mongodb+srv://dchun:whatsupadmin@whatsup-rwt91.mongodb.net')
    User.drop_collection()
    Event.drop_collection()
    Report.drop_collection()


def users():
    """ Example users api usage """
    sample_user = User(**{
        "email": "bob@gmail.com",
        "name": "Bob",
        "karma": 0
    })
    sample_user.save()
    print("\n#" + "=" * 50 + " USERS " + "=" * 50 + "#")
    print("The first user created is: " + str(sample_user))

    db.add_user("lilian@gmail.com", "Lilian")
    print("Added a user: " + str(db.get_user(email="lilian@gmail.com")))
    user1 = db.get_user(email="lilian@gmail.com")
    user1.update_info(name="LilianBis")
    print("Updated a user: " + str(db.get_user(email="lilian@gmail.com")))
    print("#" + "=" * 107 + "#")

    db.add_user("tkim10kb@gmail.com", "Tom Kim")
    print("Added another user: " + str(db.get_user(email="tkim10kb@gmail.com")))


def events():
    """ Example event api usage """
    sample_user = db.get_user(email="bob@gmail.com")
    sample_event = Event(**{
        "title": "EER_Free_T_shirt",
        "last_updated_user": "Bob",
        "location": "3.1415926",
        "time": datetime.datetime.now(),
        "status": True,
        "description": "Free T-shirts",
    })
    sample_event.save()
    print("\n\n#" + "=" * 49 + " EVENTS " + "=" * 50 + "#")
    print("A new event is happening: \n" + str(sample_event))
    sample_user.add_event( title="event3", description="Party", time=datetime.datetime.now(), location="33.232", status=True)
    sample_user.add_event( title="event6", description="Party", time=datetime.datetime.now(), location="33.232", status=True)
    sample_user.add_event( title="event4", description="Party", time=datetime.datetime.now(), location="33.232", status=True)
    sample_user.add_event( title="event5", description="Party", time=datetime.datetime.now(), location="33.232", status=True)

    sample_user.update_event(event=sample_event, new_location="1.57", new_status=False)
    print("The event has been updated: \n" + str(Event.objects(title="EER_Free_T_shirt")))

    user1 = db.get_user(email="lilian@gmail.com")
    user1.add_event(title="EER_Free_Food", location="3.14", time=datetime.datetime.now(),
                    status=True, description="Free food for students!")
    print("\nA new event has been added: \n" + str(db.get_events(title="EER_Free_Food")))

    event_list = db.get_events()
    print("\nThe free events are: \n" + str(event_list))
    print("#" + "~" * 51 + " END " + "~" * 51 + "#")


def reports():
    """ Example reports api usage """
    sample_user = db.get_user(email="bob@gmail.com")
    sample_event = db.get_events("EER_Free_T_shirt").first()
    sample_report = Report(**{
        "username": sample_user,
        "event_id": sample_event,
        "comments": "Almost out of food",
        "time": datetime.datetime.now()
    })
    sample_report.save()
    print("\n\n#" + "=" * 49 + " REPORTS " + "=" * 49 + "#")
    print("A new report has been filed: \n" + str(sample_report))

    alt_event = db.get_events("EER_Free_Food").first()
    sample_user.update_report(sample_report, alt_event)
    print("\nThe report has been updated: \n" + str(Report.objects(user=sample_user)))

    user1 = db.get_user(email="lilian@gmail.com")
    user1.add_report(event=sample_event,comments="This was fun")
    print("\nAnother report has been filed: \n" + str(Report.objects(user=user1)))

    print("\nPrinting get reports\n")
    print("All:           " + str(db.get_reports()))
    print("Email:         " + str(db.get_reports(email=sample_user.email)))
    print("Event:         " + str(db.get_reports(event=sample_event.title)))
    print("Email & Event: " + str(db.get_reports(email=user1.email, event=sample_event.title)))

    print("\nPrinting get reports by id\n")
    print("User:          " + str(db.get_reports_by_id(user=sample_user)))
    print("Report:        " + str(db.get_reports_by_id(user=sample_user, report_id=sample_report.id)))


def test_json():
    json_user = db.get_user("lilian@gmail.com")
    user_json = json.loads(json_user.to_json())
    print(json.dumps(user_json, indent=2, sort_keys=True))

    print("\n")

    json_report = db.get_reports_by_id(user=json_user)
    report_json = json.loads(json_report.to_json())
    print(json.dumps(report_json, indent=2, sort_keys=True))



if __name__ == "__main__":
    setup()
    users()
    events()
    # reports()
    # test_json()
