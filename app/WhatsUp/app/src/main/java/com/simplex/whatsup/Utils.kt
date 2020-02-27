package com.simplex.whatsup

import android.util.Base64
import android.view.View
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.models.Report
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.text.SimpleDateFormat
import java.util.*

fun <T> Flowable<T>.networkSubscribe(consumer: Consumer<in T>, networkErrorHandler: NetworkErrorHandler): Disposable {
    return subscribe(consumer, Consumer{ networkErrorHandler.handleNetworkError() })
}

fun <T> Single<T>.networkSubscribe(consumer: Consumer<in T>, networkErrorHandler: NetworkErrorHandler): Disposable {
    return subscribe(consumer, Consumer{ networkErrorHandler.handleNetworkError() })
}

fun String.toBase64Array(): ByteArray = Base64.decode(this, Base64.DEFAULT)

fun String.prettyPrintTime(): String {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(this)
    val prettyFormatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US)
    return if (date != null) prettyFormatter.format(date) else this
}

fun ArrayList<Report>.resetList(newData: ArrayList<Report>) = this.apply {
    clear()
    addAll(newData)
}

fun View.toggle(): Boolean = this.let {
    val prevVisible = (this.visibility == View.VISIBLE)
    visibility = if (prevVisible) View.GONE else View.VISIBLE
    prevVisible
}