package com.simplex.whatsup.viewmodels

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.api.mongo.MongoRepository
import com.simplex.whatsup.api.nfc.NFCRepository
import com.simplex.whatsup.models.OptionalEvent
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import java.io.File

class MapViewModel(private val mongoRepository: MongoRepository, private val nfcRepository: NFCRepository): ViewModel() {
    fun getEvents(): Flowable<List<Event>> = mongoRepository.getEvents()

    fun writeTag(tag: Tag, eventId: String): Flowable<Boolean> = nfcRepository.writeEvent(tag, eventId)

    fun readTag(tag: Tag): Flowable<String> = nfcRepository.readEvent(tag)

    fun addEvent(newEvent: Event): Single<Event> = mongoRepository.addEvent(newEvent, null)

    fun getEventCategory(): Flowable<List<String>> = mongoRepository.getEventCategory()

}