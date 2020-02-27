package com.simplex.whatsup.api.nfc

import android.nfc.Tag
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.OptionalEvent
import io.reactivex.Flowable

class NFCRepositoryImpl(private val ndefApi: NdefApi): NFCRepository {
    override fun readEvent(tag: Tag): Flowable<String> = ndefApi.readTag(tag)

    override fun writeEvent(tag: Tag, eventId: String): Flowable<Boolean> = ndefApi.writeTag(tag, eventId)
}