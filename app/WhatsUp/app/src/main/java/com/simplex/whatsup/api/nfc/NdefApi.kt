package com.simplex.whatsup.api.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.OptionalEvent
import com.squareup.moshi.Moshi
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

class NdefApi {
    fun readTag(tag: Tag): Flowable<String> {

        Ndef.get(tag).cachedNdefMessage.records.forEach { record ->
            if(record.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                return Flowable.just(parseText(record))
            }
        }

        return Flowable.just(null)
    }

    fun writeTag(tag: Tag, eventId: String): Flowable<Boolean> {
        Log.d(NDEF_API, "Writing: $eventId")
        val content = eventId.toByteArray(Charset.forName("UTF-8"))
        val lang = Locale.getDefault().language.toByteArray(Charset.forName("UTF-8"))

        val langSize = lang.size
        val contentSize = content.size

        val payload = ByteArrayOutputStream(1+langSize + contentSize).apply {
            write(langSize and 0x1f)
            write(lang, 0, langSize)
            write(content, 0, contentSize)
        }

        val ndefRecord = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
            ByteArray(0),
            payload.toByteArray())

        val message = NdefMessage(arrayOf(ndefRecord))

        Ndef.get(tag)?.apply {
            connect()
            if(maxSize < message.toByteArray().size) {
                return Flowable.just(false)
            }

            return if(isWritable) {
                writeNdefMessage(message)
                close()
                Flowable.just(true)
            } else {
                Flowable.just(false)
            }
        }

        return Flowable.just(false)
    }

    private fun parseText(record: NdefRecord): String = record.payload.let { payload ->
        val textEncoding = if (payload[0] and 128.toByte() == 0.toByte()) "UTF-8" else "UTF-16"
        val languageCodeLength = payload[0] and 63.toByte()

        return@let String(payload, languageCodeLength + 1,
            payload.size - 1 - languageCodeLength, Charset.forName(textEncoding))
    }

    companion object {
        const val NDEF_API = "NDEF"
    }
}