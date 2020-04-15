package com.simplex.whatsup.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import com.simplex.whatsup.R
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.networkSubscribe
import com.simplex.whatsup.viewmodels.AddViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_add_report.*
import kotlinx.android.synthetic.main.activity_add_report.autocomplete_category
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddReportActivity : AppCompatActivity() {
    private val addViewModel: AddViewModel by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()

    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoFile: File? = null
    private var currentPhotoPathUri: Uri? = null
    private lateinit var currentPhotoPath: String

    private var disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report)
        setSupportActionBar(report_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = intent.getStringExtra("eventName")
        }
        photoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
        submitButton.setOnClickListener {
            submitReport()
        }

        val categoryObservable = addViewModel.getReportCategory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { reportCategoryList ->
                Log.d(TAG, "Got report categories:")
                val categories = ArrayList<String>()
                for (reportCategory in reportCategoryList) {
                    categories.add(reportCategory)
                }
                val dataAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
                autocomplete_category.setAdapter(dataAdapter)
            }, networkErrorHandler)

        disposables += categoryObservable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image.setImageURI(currentPhotoPathUri)
            Toast.makeText(this, "Picture saved", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.simplex.whatsup.fileprovider",
                        it
                    )
                    currentPhotoPathUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    private fun submitReport() {
        val comments = comment_content.text.toString()
//        val status = SpinnerStatusType.selectedItem.toString()
        val eventId = intent.getStringExtra("eventId")
        val username = intent.getStringExtra("username")
        val currentTime: String = SimpleDateFormat("M/dd/yyyy hh:mm:ss", Locale.US).format(Date())
        val category = autocomplete_category.text.toString()

        if (eventId != null && username != null) {
            val reportPostObservable =
                addViewModel.addReport(eventId, username, comments, currentTime, photoFile, category)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ report ->
                            Log.d(TAG, report.toString())
                            Toast.makeText(this, "Report Saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, EventActivity::class.java).apply {
                                putExtra("eventId", eventId)
                            }
                            startActivity(intent)
                        }, {
                            Toast.makeText(this, "Report not saved, please try again", Toast.LENGTH_SHORT).show()
                        })

            disposables += reportPostObservable
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        const val TAG = "WhatsUp Add Report"
    }
}
