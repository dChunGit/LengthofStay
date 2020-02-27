package com.simplex.whatsup.viewmodels

import androidx.lifecycle.ViewModel
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.api.mongo.MongoRepository
import io.reactivex.Flowable
import io.reactivex.Single
import org.koin.core.KoinComponent
import java.io.File

class AddViewModel(private val mongoRepository: MongoRepository): ViewModel(), KoinComponent {
    fun getEventCategory(): Flowable<List<String>> = mongoRepository.getEventCategory()

    fun getReportCategory(): Flowable<List<String>> = mongoRepository.getReportCategory()

    fun addReport(eventId: String, username: String, comments: String, time: String, imageFile: File?, category: String): Single<Report> {

        val newReport = Report(
            id = "", username = username, event_id = eventId, comments = comments,
            time = time, report_image = "", category = category
        )
        return mongoRepository.addReport(newReport, imageFile)
    }

    fun addEvent(title: String, description: String, time: String, location: String, imageFile: File?, latitude:String,longitude:String, category: String): Single<Event> {
        val newEvent = Event(
            id = "",
            title = title,
            description = description,
            time = time,
            location = location,
            status = true,
            last_updated_user = "",
            first_update_time = "",
            last_update_time = "",
            cover_image = "",
            reports = emptyList(),
            geo_latitude = latitude,
            geo_longitude = longitude,
            category = category
        )

        return mongoRepository.addEvent(newEvent, imageFile)
    }
}