package com.simplex.whatsup.activities

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.simplex.whatsup.R
import com.simplex.whatsup.api.network.HeaderInterceptor
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.fragments.AddEventDialog
import com.simplex.whatsup.fragments.NfcDialog
import com.simplex.whatsup.networkSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.ext.android.inject
import com.simplex.whatsup.models.Event
import com.simplex.whatsup.models.NdefStatus
import com.simplex.whatsup.prettyPrintTime
import com.simplex.whatsup.viewmodels.MapViewModel
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import kotlin.math.max

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NfcDialog.NFCDialogListener, AddEventDialog.AddEventListener {

    private val mapViewModel: MapViewModel by inject()
    private val firebaseAuth: FirebaseAuth by inject()
    private val networkErrorHandler: NetworkErrorHandler by inject()
    private val headerInterceptor: HeaderInterceptor by inject()

    private var disposables = CompositeDisposable()

    private var map: GoogleMap? = null
    private var event: Event? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var selectedEventId: String = ""

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var tag: Tag? = null
    private var ndefStatus = NdefStatus(false, "")
    private val noNfc: String by lazy { resources.getString(R.string.no_nfc) }
    private val retryNfc: String by lazy { resources.getString(R.string.no_tag_found_retry) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(map_toolbar)

        firebaseAuth.currentUser?.let {
            loggedIn()
        } ?: startActivity(Intent(this, UserLoginActivity::class.java))

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        eventInfo.setOnClickListener {
            event?.let {
                val intent = Intent(this, EventActivity::class.java).apply {
                    putExtra("eventId", it.id)
                }
                startActivity(intent)
            }
        }

        post_event_button.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        setupNfc()
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, noNfc, Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, javaClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        }
    }

    override fun onStart() {
        super.onStart()
        val timerObservable = Flowable.interval(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .flatMap { mapViewModel.getEvents() }
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged()
            .networkSubscribe(Consumer { events ->
                Log.d(TAG, "Got events")
                if (map != null) {
                    map?.clear()
                    for (event in events) {
                        val marker = map?.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    event.geo_latitude.toDouble(),
                                    event.geo_longitude.toDouble()
                                )
                            )
                        )
                        marker?.tag = event
                    }
                }
            }, networkErrorHandler)
        disposables += timerObservable
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@MapsActivity)
            setOnMapClickListener()
            setUpMap()
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map?.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
            }
        }
    }


    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0 != null && p0.tag != null) {
            event = p0.tag as Event
            event?.apply {
                val strTitle = this.title
                val strTime = "Time: " + this.time.prettyPrintTime()
                val span = SpannableString(strTitle + "\n\n" + strTime)
                span.setSpan(AbsoluteSizeSpan(18, true), 0, strTitle.length, 0)
                span.setSpan(StyleSpan(Typeface.BOLD), 0, strTitle.length, 0)
                span.setSpan(AbsoluteSizeSpan(14, true), strTitle.length + 2, span.length, 0)
                selectedEventId = this.id
                eventInfo.text = span
                eventInfo.visibility = View.VISIBLE
                post_event_button.visibility = View.GONE

                map?.setPadding(0, 0, 0, max(eventInfo.height - post_event_button.height, 0))
            }

        } else {
            selectedEventId = ""
            eventInfo.visibility = View.GONE
            post_event_button.visibility = View.VISIBLE
            map?.setPadding(0, 0, 0, 0)
        }

        return false
    }

    private fun setOnMapClickListener() {
        map?.setOnMapClickListener {
            eventInfo.visibility = View.GONE
            post_event_button.visibility = View.VISIBLE
            map?.setPadding(0, 0, 0, 0)
        }
    }

    private fun loggedIn() {
        firebaseAuth.currentUser?.apply {
            this.getIdToken(true)
                .addOnSuccessListener { headerInterceptor.updateToken(it.token) }
        }
    }

    private fun loggedOut() {
        AuthUI.getInstance().signOut(this)
            .addOnCompleteListener {
                headerInterceptor.clearToken()
                startActivity(Intent(this, UserLoginActivity::class.java))
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            loggedOut()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onOptionSelected(nfcOptions: NfcDialog.NfcOptions, eventId: String) {
        when (nfcOptions) {
            NfcDialog.NfcOptions.OPEN -> {
                startActivity(Intent(this, EventActivity::class.java).apply {
                    putExtra("eventId", eventId)
                })
            }
            NfcDialog.NfcOptions.UPDATE -> {
                getLocationLaunchEventDialog()
            }
        }
    }

    override fun saveNFCEvent(event: Event) {
        ndefStatus = NdefStatus(true, event.id)
        mapViewModel.addEvent(event)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .networkSubscribe( Consumer { savedEvent ->
                Log.d(EventActivity.TAG, "Saved event: ${savedEvent.id}")
                ndefStatus.eventId = savedEvent.id
                writeToTag()
            }, networkErrorHandler)
        writeToTag()
    }

    private fun writeToTag() {
        try {
            tag?.apply {
                mapViewModel.writeTag(this, ndefStatus.eventId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        if (result) {
                            ndefStatus.clearStatus()
                        } else {
                            Toast.makeText(applicationContext, retryNfc, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            Toast.makeText(this, retryNfc, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationLaunchEventDialog() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val addEventDialog = AddEventDialog(it)
            addEventDialog.show(supportFragmentManager, EVENT_DIALOG_TAG)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        tag = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        tag?.apply {
            if(ndefStatus.status) {
                writeToTag()
            } else {
                mapViewModel.readTag(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { eventId ->
                        Log.d(TAG, "Event is: $eventId")
                        val dialog = NfcDialog(eventId)
                        dialog.show(supportFragmentManager, NFC_DIALOG_TAG)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        const val TAG = "Whatsup Map"
        const val NFC_DIALOG_TAG = "NFCDialogFragment"
        const val EVENT_DIALOG_TAG = "AddEventDialogFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
