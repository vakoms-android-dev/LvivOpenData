package com.vakoms.oleksandr.havruliyk.lvivopendata.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.vakoms.oleksandr.havruliyk.lvivopendata.R
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.map.MapRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.vm.MapViewModel
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.DEFAULT_ZOOM
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.getDefaultLatLnt
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.isValid
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.back_button.*
import kotlinx.android.synthetic.main.item_list.*
import java.lang.String.format
import javax.inject.Inject


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MapViewModel

    private var googleMap: GoogleMap? = null
    private lateinit var mapFragment: SupportMapFragment

    private val records = mutableListOf<MapRecord>()

    private var mLocationPermissionGranted: Boolean = false
    private var mLastKnownLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initView()
        initMapFragment()
        initViewModel()
        initObserver()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun initView() {
        label_view.text = resources.getString(R.string.map_label)

        back_button.setOnClickListener { finish() }
    }

    private fun initMapFragment() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MapViewModel::class.java)
    }

    private fun initObserver() {
        viewModel.mapRecords.observe(this, Observer { records ->
            upDataView(records)
        })

        viewModel.addressRecords.observe(this, Observer { records ->
            setLoadingMessage(records.size)
        })
    }

    private fun setLoadingMessage(countOfItems: Int) {
        loading_msg.text = format(resources.getString(R.string.map_loading_items), countOfItems.toString())
    }

    private fun upDataView(newRecords: List<MapRecord>) {
        setLoadingVisibility(newRecords.isEmpty())
        records.addAll(newRecords)
        showMarkers()
    }

    private fun setLoadingVisibility(isRecordsEmpty: Boolean) {
        if (isRecordsEmpty) {
            loading_layout.visibility = View.VISIBLE
        } else {
            loading_layout.visibility = View.INVISIBLE
        }
    }

    private fun showMarkers() {
        for (record: MapRecord in records) {
            with(record) {
                if (isValid()) {
                    addMarker(latLng, address.title)
                }
            }
            showCurrentLocation()
        }
    }

    private fun showCurrentLocation() {
        updateLocationUI()
        getDeviceLocation()
    }

    private fun addMarker(latLng: LatLng, title: String) {
        googleMap?.addMarker(MarkerOptions().position(latLng).title(title))
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
        )
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun updateLocationUI() {
        if (googleMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap!!.isMyLocationEnabled = true
                googleMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                googleMap!!.isMyLocationEnabled = false
                googleMap!!.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null

                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }

    private fun getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                val locationResult = LocationServices.getFusedLocationProviderClient(this).lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        with(task.result!!) {
                            mLastKnownLocation = LatLng(latitude, longitude)
                            googleMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), DEFAULT_ZOOM
                                )
                            )
                        }
                    } else {
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(getDefaultLatLnt(), DEFAULT_ZOOM))
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}