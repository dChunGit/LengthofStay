package com.simplex.whatsup.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.api.mongo.MongoRepository
import com.simplex.whatsup.models.User
import io.reactivex.Flowable
import io.reactivex.Single
import org.koin.core.KoinComponent

//TODO use livedata to update ui and move rx here
class EventViewModel(private val mongoRepository: MongoRepository): ViewModel(), KoinComponent {
    fun getUser(): Flowable<User> = mongoRepository.getUser()

    fun getEventReports(eventId: String): Flowable<List<Report>> = mongoRepository.getEventReports(eventId)

    fun getEvent(eventId: String): Flowable<Event> = mongoRepository.getEvent(eventId)

    fun getReportImage(imageId: String): Flowable<Bitmap> = mongoRepository.getReportImage(imageId)
        .map {
            val data = it.string()
            val dataArray = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(dataArray, 0, dataArray.size)
        }

    fun getEventImage(imageId: String): Flowable<Bitmap> = mongoRepository.getEventImage(imageId)
        .map {
            val data = it.string()
            val dataArray = Base64.decode(data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(dataArray, 0, dataArray.size)
        }

    fun subscribeEvent(eventId: String, subscribe: Boolean): Single<User> = mongoRepository.subscribeEvent(eventId, subscribe)
}