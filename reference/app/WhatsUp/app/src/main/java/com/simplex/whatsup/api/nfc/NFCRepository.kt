package com.simplex.whatsup.api.nfc

import android.nfc.Tag
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.OptionalEvent
import io.reactivex.Flowable

interface NFCRepository {
    fun readEvent(tag: Tag): Flowable<String>
    fun writeEvent(tag: Tag, eventId: String): Flowable<Boolean>
}