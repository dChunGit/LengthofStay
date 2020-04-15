package com.simplex.whatsup.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.simplex.whatsup.R
import java.lang.ClassCastException
import java.lang.IllegalStateException


class NfcDialog(private val eventId: String): DialogFragment() {
    private lateinit var listener: NFCDialogListener

    interface NFCDialogListener {
        fun onOptionSelected(nfcOptions: NfcOptions, eventId: String)
    }

    enum class NfcOptions {
        UPDATE, OPEN
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        val builder = AlertDialog.Builder(it)
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.nfc_dialog, null)
        dialogView.findViewById<TextView>(R.id.nfc_banner).text = requireActivity().resources.getString(R.string.event_dialog_title, eventId)

        dialogView.findViewById<Button>(R.id.view_event).setOnClickListener {
            listener.onOptionSelected(NfcOptions.OPEN, eventId)
            dismiss()
        }
        dialogView.findViewById<Button>(R.id.add_event).setOnClickListener {
            listener.onOptionSelected(NfcOptions.UPDATE, eventId)
            dismiss()
        }

        builder.setView(dialogView)
            .setPositiveButton("Cancel") { dialog, id ->
                dismiss()
            }
            .create()

    }?: throw IllegalStateException("Activity cannot be null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NFCDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NFCDialogListener")
        }
    }

    companion object {
        const val TAG = "NFC_TAG_DIALOG"
    }
}