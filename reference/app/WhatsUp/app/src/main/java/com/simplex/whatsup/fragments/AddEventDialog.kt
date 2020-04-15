package com.simplex.whatsup.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.simplex.whatsup.R
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.networkSubscribe
import com.simplex.whatsup.viewmodels.MapViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import org.koin.android.ext.android.inject
import java.util.*

class AddEventDialog(private val location: Location): DialogFragment() {
    private val mapViewModel: MapViewModel by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()
    private lateinit var listener: AddEventListener

    private var disposables = CompositeDisposable()
    private var chosenDate = ""
    private var chosenTime = ""

    interface AddEventListener {
        fun saveNFCEvent(event: Event)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        val builder = AlertDialog.Builder(it)
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.add_event_dialog, null)
        val dateButton = dialogView.findViewById<Button>(R.id.date_button)
        val timeButton = dialogView.findViewById<Button>(R.id.time_button)
        val titleEntry = dialogView.findViewById<EditText>(R.id.editText_title)
        val locationEntry = dialogView.findViewById<EditText>(R.id.editText_description)
        val autocomplete = dialogView.findViewById<AutoCompleteTextView>(R.id.autocomplete_category)

        val cal = Calendar.getInstance()

        dateButton.setOnClickListener {
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                chosenDate = "$year-$monthOfYear-$dayOfMonth"
                dateButton.text = chosenDate
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        timeButton.setOnClickListener{
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                chosenTime = String.format("%02d:%02d", hourOfDay, minute)
                timeButton.text = chosenTime
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        setupData(autocomplete)

        builder.setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newEvent = Event(
                    id = "",
                    title = titleEntry.text.toString(),
                    description = locationEntry.text.toString(),
                    time = "${chosenDate}T${chosenTime}",
                    location = "",
                    status = true,
                    last_updated_user = "",
                    first_update_time = "",
                    last_update_time = "",
                    cover_image = "",
                    reports = emptyList(),
                    geo_latitude = location.latitude.toString(),
                    geo_longitude = location.longitude.toString(),
                    category = autocomplete.text.toString()
                )
                listener.saveNFCEvent(newEvent)
                dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            .create()

    }?: throw IllegalStateException("Activity cannot be null")

    private fun setupData(textView: AutoCompleteTextView) {
        val categoryObservable = mapViewModel.getEventCategory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { eventCategoryList ->
                Log.d(TAG, "Got event categories: $eventCategoryList")
                val dataAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventCategoryList)
                textView.setAdapter(dataAdapter)
            }, networkErrorHandler)

        disposables += categoryObservable
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddEventListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NFCDialogListener")
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        const val TAG = "NFC_TAG_DIALOG"
    }

}