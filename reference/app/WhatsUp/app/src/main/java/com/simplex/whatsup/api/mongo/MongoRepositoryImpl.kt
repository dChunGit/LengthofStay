package com.simplex.whatsup.api.mongo

import android.util.Log
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.models.User
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.core.KoinComponent
import retrofit2.Retrofit
import java.io.File

class MongoRepositoryImpl(client: Retrofit): MongoRepository, KoinComponent {
    private var mongoDbApi = client.create(MongoDbApi::class.java)
    // TODO handle caching here

    override fun addUser(user: User): Single<Any> {
        val email = RequestBody.create(MediaType.parse(FORM_TYPE), user.email)
        val username = RequestBody.create(MediaType.parse(FORM_TYPE), user.name)

        return mongoDbApi.addUser(email, username)
    }

    override fun getUser(): Flowable<User> = mongoDbApi.getUser()

    override fun getEventReports(eventId: String): Flowable<List<Report>> = mongoDbApi.getEventReports(event = eventId)

    override fun getEvent(eventId: String): Flowable<Event> = mongoDbApi.getEvent(event_id = eventId)

    override fun getEvents(): Flowable<List<Event>> = mongoDbApi.getEvents()

    override fun getReports(): Flowable<List<Report>> = mongoDbApi.getReports()

    override fun getEventImage(imageId: String): Flowable<ResponseBody> = mongoDbApi.getEventImage(imageId)

    override fun getReportImage(imageId: String): Flowable<ResponseBody> = mongoDbApi.getReportImage(imageId)

    override fun addReport(report: Report, file: File?): Single<Report> {
        val eventId = RequestBody.create(MediaType.parse(FORM_TYPE), report.event_id)
        val comments = RequestBody.create(MediaType.parse(FORM_TYPE), report.comments)
        val category = RequestBody.create(MediaType.parse(FORM_TYPE), report.category)
        val body: MultipartBody.Part? = if (file != null) {
            val imageFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            MultipartBody.Part.createFormData("report_image", file.name, imageFile)
        }
        else {
            null
        }

        return mongoDbApi.addReport(eventId, comments, category, body)
            .doOnError { Log.d(MONGO_TAG, it.toString()) }
    }

    override fun addEvent(event: Event, file: File?): Single<Event> {
        val title = RequestBody.create(MediaType.parse(FORM_TYPE), event.title)
        val description = RequestBody.create(MediaType.parse(FORM_TYPE), event.description)
        val time = RequestBody.create(MediaType.parse(FORM_TYPE), event.time)
        val location = RequestBody.create(MediaType.parse(FORM_TYPE), event.location)
        val latitude = RequestBody.create(MediaType.parse(FORM_TYPE), event.geo_latitude)
        val longitude = RequestBody.create(MediaType.parse(FORM_TYPE), event.geo_longitude)
        val category = RequestBody.create(MediaType.parse(FORM_TYPE), event.category)
        val body: MultipartBody.Part? = if (file != null) {
            val imageFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            MultipartBody.Part.createFormData("cover_image", file.name, imageFile)
        }
        else {
            null
        }

        return mongoDbApi.addEvent(title, description, time, location,latitude,longitude, category, body )
            .doOnError { Log.d(MONGO_TAG, it.toString()) }
    }

    override fun subscribeEvent(eventId: String, subscribe: Boolean): Single<User> {
        val subscribeStatus = RequestBody.create(MediaType.parse("text/plain"), if (subscribe) "Subscribe" else "Unsubscribe")

        return mongoDbApi.subscribeEvent(eventId, subscribeStatus)
            .doOnError { Log.d(MONGO_TAG, it.toString()) }
    }

    override fun getEventCategory(): Flowable<List<String>> = mongoDbApi.getEventCategory()

    override fun getReportCategory(): Flowable<List<String>> = mongoDbApi.getReportCategory()

    companion object {
        const val FORM_TYPE = "multipart/form-data"
        const val MONGO_TAG = "MONGODBAPI"
    }

}