package com.simplex.whatsup.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.simplex.whatsup.*
import com.simplex.whatsup.adapters.ReportAdapter
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.models.User
import com.simplex.whatsup.viewmodels.EventViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.event_content.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class EventActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()

    private lateinit var reportAdapter: ReportAdapter
    private var event: Event? = null
    private var eventId: String? = ""
    private var currentUser: User? = null
    private var subscribed: Boolean = false

    private var reports: ArrayList<Report> = arrayListOf()
    private var saveReports: ArrayList<Report> = arrayListOf()
    private var disposables = CompositeDisposable()
    private var editTextSubject: PublishSubject<String>? = null

    private val filterText: String by lazy(LazyThreadSafetyMode.NONE) { resources.getString(R.string.filter_reports) }
    private val undoText: String by lazy(LazyThreadSafetyMode.NONE) { resources.getString(R.string.undo_filter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventId = intent.getStringExtra("eventId")

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }

        reportAdapter = ReportAdapter(reports)

        event_reviews_rv.apply {
            layoutManager = LinearLayoutManager(this@EventActivity).apply { orientation = LinearLayoutManager.VERTICAL }
            adapter = reportAdapter
        }

        setUpClickListeners()
        setUpFilter()
        setUpData()
    }

    private fun setUpClickListeners() {
        event_subscribe.setOnClickListener { setSubscription() }

        event_filter_report.setOnClickListener {
            val prevVisible = event_report_filter.toggle()
            if (prevVisible) {
                event_filter_report.text = filterText
                reports.resetList(saveReports)
                reportAdapter.notifyDataSetChanged()
            } else {
                event_filter_report.text = undoText
                TransitionManager.beginDelayedTransition(event_layout)
                saveReports.resetList(reports)
            }
        }

        event_add_report.setOnClickListener {
            val intent = Intent(this, AddReportActivity::class.java).apply {
                putExtra(EVENT_ID, eventId)
                putExtra(USERNAME, currentUser?.id)
                putExtra(EVENT_NAME, event?.title)
            }
            startActivity(intent)
        }
    }

    private fun setUpFilter() {
        editTextSubject = PublishSubject.create()
        editTextSubject?.apply {
            debounce(500, TimeUnit.MILLISECONDS)
            subscribe { search ->
                Log.d(TAG, "Searched for $search")
                reports.apply {
                    resetList(saveReports)
                    retainAll { report -> report.comments.contains(search, ignoreCase = true) }
                }
                reportAdapter.notifyDataSetChanged()
            }
        }

        event_report_filter.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextSubject?.onNext(s.toString())
            }

        })
    }

    private fun setUpData() = eventId?.apply {
        Log.d(TAG, "EventId is: $eventId")

        val reportObservable = eventViewModel.getEventReports(this)
            .flatMapIterable { reports -> reports }
            .flatMap { report ->
                Log.d(TAG, "${report.comments}: ${report.report_image}")
                if (report.report_image != null) {
                    Flowable.zip(Flowable.just(report), eventViewModel.getReportImage(report.id),
                        BiFunction { qReport: Report, qImage: Bitmap -> qReport.apply { image = qImage } }
                    )
                }
                else {
                    Flowable.just(report)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { report ->
                Log.d(TAG, "Got report: $report")
                event_report_empty.visibility = View.GONE
                reports.add(report)
                reports.sortByDescending { it.time }
                reportAdapter.notifyDataSetChanged()
            }, networkErrorHandler)

        val eventObservable = eventViewModel.getEvent(this)
            .flatMap { event ->
                if (event.cover_image != null) {
                    Flowable.zip(Flowable.just(event), eventViewModel.getEventImage(event.id),
                        BiFunction { qEvent: Event, qImage: Bitmap -> qEvent.apply { image = qImage }})
                }
                else {
                    Flowable.just(event)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { newEvent ->
                Log.d(TAG, "Got event: $newEvent")
                event = newEvent
                setEventUI()
            }, networkErrorHandler)

        val userObservable = eventViewModel.getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { user ->
                Log.d(TAG, "Got user: $user")
                currentUser = user
                setSubscribeStatus(user.subscribed_events)
            }, networkErrorHandler)

        disposables += reportObservable
        disposables += eventObservable
        disposables += userObservable
    }

    private fun setEventUI() {
        event?.apply {
            event_title.text = title
            event_category.text = category
            event_location.text = if(location != "") location else "$geo_latitude, $geo_longitude"
            event_description.text = description
            event_time.text = time.prettyPrintTime()
            image?.apply { event_image.setImageBitmap(this) }
        }
    }

    private fun setSubscribeStatus(userEvents: List<String>) {
        subscribed = userEvents.contains(eventId)
        event_subscribe.setImageDrawable(
            ContextCompat.getDrawable(this,
                if (subscribed) R.drawable.ic_star else R.drawable.ic_star_border
            )
        )
    }

    private fun setSubscription() {
        eventId?.apply {
            subscribed = !subscribed
            val subscribeObservable = eventViewModel.subscribeEvent(this, subscribed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .networkSubscribe( Consumer {
                    Toast.makeText(this@EventActivity, if (subscribed) "Subscribed!" else "Unsubscribed :(", Toast.LENGTH_SHORT).show()
                }, networkErrorHandler)

            disposables += subscribeObservable
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    companion object {
        const val TAG = "WhatsUp Event"
        const val EVENT_ID = "eventId"
        const val USERNAME = "username"
        const val EVENT_NAME = "eventName"
    }
}
