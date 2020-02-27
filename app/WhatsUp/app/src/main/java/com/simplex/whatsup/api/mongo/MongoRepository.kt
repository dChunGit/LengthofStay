package com.simplex.whatsup.api.mongo

import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.models.User
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.intellij.lang.annotations.Flow
import java.io.File

interface MongoRepository {
    fun addUser(user: User): Single<Any>
    fun getUser(): Flowable<User>
    fun getEventReports(eventId: String): Flowable<List<Report>>
    fun getEvent(eventId: String): Flowable<Event>
    fun getEvents(): Flowable<List<Event>>
    fun getReports(): Flowable<List<Report>>
    fun addReport(report: Report, file: File?): Single<Report>
    fun addEvent(event: Event, file: File?): Single<Event>
    fun subscribeEvent(eventId: String, subscribe: Boolean): Single<User>

    fun getEventCategory(): Flowable<List<String>>
    fun getReportCategory(): Flowable<List<String>>

    fun getEventImage(imageId: String): Flowable<ResponseBody>
    fun getReportImage(imageId: String): Flowable<ResponseBody>
}