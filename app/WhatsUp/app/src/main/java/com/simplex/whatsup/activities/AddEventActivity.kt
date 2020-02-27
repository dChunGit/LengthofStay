package com.simplex.whatsup.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.simplex.whatsup.R
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.networkSubscribe
import com.simplex.whatsup.viewmodels.AddViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_add_event.autocomplete_category
import kotlinx.android.synthetic.main.activity_add_report.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppCompatActivity() {

    private val addViewModel: AddViewModel by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private var currentPhotoPathUri: Uri? = null
    private val client = OkHttpClient()
    private var disposables = CompositeDisposable()
    private var photoFile: File? = null

    private var currentLocation: Location? = null
    private var useCurrentLocation = true
    private var chosenDate = ""
    private var chosenTime = ""

    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var cal: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        setSupportActionBar(event_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        photoButton1.setOnClickListener {
            dispatchTakePictureIntent()
        }
        submitButton1.setOnClickListener {
            submitReport()
        }

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        location_content.addTextChangedListener { useCurrentLocation = false }

        current_location.setOnClickListener {
            location_content.text.clear()
            useCurrentLocation = true
        }

        event_image.setOnClickListener {
            photoFile = null
            it.visibility = View.GONE
            photoButton1.visibility = View.VISIBLE
            TransitionManager.beginDelayedTransition(event_actions)
        }

        cal = Calendar.getInstance()

        event_date.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                Log.d(TAG, "Date picked is: $year-$monthOfYear-$dayOfMonth")
                cal.set(year, monthOfYear, dayOfMonth)
                chosenDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                event_date.text = chosenDate
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        event_time.setOnClickListener {
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                Log.d(TAG, "Time picked is: $hourOfDay:$minute")
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                chosenTime = SimpleDateFormat("HH:mm", Locale.US).format(cal.time)
                event_time.text = chosenTime
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        getLocation()

        val categoryObservable = addViewModel.getEventCategory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { eventCategoryList ->
                Log.d(TAG, "Got event categories:")
                val categories = ArrayList<String>()
                for (eventCategory in eventCategoryList) {
                    categories.add(eventCategory)
                }
                val dataAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
                autocomplete_category.setAdapter(dataAdapter)
            }, networkErrorHandler)

        disposables += categoryObservable
    }

    private fun submitReport() {
        Thread {
            val title = title_content.text.toString()
            val description = description_content.text.toString()
            val location = location_content.text.toString()
            val currentTime = "${chosenDate}T${chosenTime}"

            var longitude = 0.0
            var latitude = 0.0

            val category = autocomplete_category.text.toString()

            currentLocation.takeIf { useCurrentLocation || location == "" }?.let {
                longitude = it.longitude
                latitude = it.latitude
            } ?: let {
                val request = Request.Builder()
                    .url(
                        "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(
                            location,
                            "UTF-8"
                        ) + "&key=AIzaSyBLV7OOGu63ByDcWv9aNX0eTA1kxDFQkao"
                    )
                    .build()
                val response = client.newCall(request).execute()
                val result = response.body()?.string()
                val jsonObject = JSONObject(result)
                val jsonArray = jsonObject.get("results") as JSONArray

                latitude = jsonArray.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .getDouble("lat")
                longitude = jsonArray.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .getDouble("lng")
            }

            Log.d(TAG, latitude.toString())
            Log.d(TAG, longitude.toString())

            val eventPostObservable =
                addViewModel.addEvent(title, description, currentTime, location, photoFile, latitude.toString(), longitude.toString(), category)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { event ->
                            Log.d(TAG, event.toString())
                            Toast.makeText(this, "Event Saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MapsActivity::class.java)
                            startActivity(intent)
                        },
                        { Toast.makeText(this, "Event not saved, please try again", Toast.LENGTH_SHORT).show() })

            disposables += eventPostObservable
        }.start()
    }

    private fun getLocation() {
        locationProviderClient.lastLocation.addOnSuccessListener { currentLocation = it }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Picture saved", Toast.LENGTH_SHORT).show()
            photoButton1.visibility = View.GONE
            event_image.apply {
                setImageURI(currentPhotoPathUri)
                visibility = View.VISIBLE
            }
            TransitionManager.beginDelayedTransition(event_actions)
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        const val TAG = "WhatsUp Add Event"
    }
}
