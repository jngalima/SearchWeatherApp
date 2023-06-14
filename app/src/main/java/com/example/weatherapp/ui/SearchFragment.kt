package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.data.api.model.Result
import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.util.*
import com.example.weatherapp.viewModel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel by viewModels<WeatherViewModel>()

    private lateinit var binding: FragmentSearchBinding

    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Set click listeners, apply configurations to binding, etc....
         */
        initializeSearchUI()

        /**
         * Observe livedata used by UI
         */
        initializeObservers()

        /**
         * Check location permission settings or request permissions if needed
         * If location permissions are granted then show current location weather
         * If location permissions are denied/not available then check for last searched city
         */
        handleLocationOrLastSearched()
    }

    private fun initializeSearchUI() {
        /**
         * When submit button is pressed, make api call with text in search field (city name)
         * and selected radio button (unit of measurement) as parameters
         */
        binding.submitButton.setOnClickListener {
            val searchInput = binding.searchField.text?.toString() ?: ""
            val units = binding.unitsRadioGroup.checkedRadioButtonId.let { checkedButton ->
                if (checkedButton == R.id.imperial) IMPERIAL else METRIC
            }
            val paramsMap = mapOf(CITY_NAME to searchInput, UNITS to units)
            viewModel.loadWeatherData(paramsMap)
        }

        /**
         * Add icons to search field and city name views
         */
        binding.city.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_icon, 0, 0, 0)
        binding.searchField.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_icon, 0, 0, 0)
    }

    private fun initializeObservers() {
        /**
         * if isLoading == true, show progress bar and hide results layout
         */
        viewModel.isLoading.observe(viewLifecycleOwner) { isWeatherLoading ->
            if (isWeatherLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.weatherDetailsLayout.visibility = View.GONE
                binding.errorText.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        /**
         * handle result of api call
         */
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
            when (weatherResponse) {
                is Result.Success -> {
                    handleSuccess(weatherResponse)
                }
                is Result.Error<*> -> {
                    handleError(weatherResponse)
                }
            }
        }

        /**
         * when icon is fetched from cache or api, display it on the UI
         */
        viewModel.iconBitMap.observe(viewLifecycleOwner) {
            binding.weatherIcon.setImageBitmap(it)
        }
    }

    /**
     * If api call is successful:
     * Store search parameters in sharedPreferences(last searched)
     * Fetch icon from cache or api
     * Map response to UI Model,
     * Display Results
     */
    private fun handleSuccess(weatherResponse: Result.Success<WeatherDataResponse>?) {
        viewModel.saveLastSearched()
        weatherResponse?.data?.toUIModel()?.let {
            binding.detailsModel = it
            it.icon?.let { iconId ->
                viewModel.getIcon(iconId, getString(R.string.icon_url, iconId))
            }
        }
        binding.weatherDetailsLayout.visibility = View.VISIBLE
    }

    /**
     * If api call fails due to error or exception, then display generic error
     * Given more time I would focus on displaying messages that are specific to the error
     * Examples: No city entered, City not found, API is down, etc...
     */
    private fun handleError(error: Result<WeatherDataResponse>) {
        binding.errorText.visibility = View.VISIBLE
        binding.weatherDetailsLayout.visibility = View.GONE
        binding.errorText.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_error_outline_24,
            0,
            0,
            0
        )
    }

    /**
     * Boilerplate code for location data fetching
     */
    private fun handleLocationOrLastSearched() {
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermission()
    }

    /**
     * if permissions are not granted then prompt the user
     * else retrieve location data
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it from user
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted
            retrieveLocation()
        }
    }

    /**
     * Retrieve latitude and longitude from location data and call weather api
     * If for some reason location is null then check for last searched
     */
    private fun retrieveLocation() {
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val paramsMap = mapOf(
                        LATITUDE to "${location.latitude}",
                        LONGITUDE to "${location.longitude}",
                        UNITS to IMPERIAL
                    )
                    viewModel.loadWeatherData(paramsMap)
                } else {
                    //location not available for some reason so look for last searched
                    viewModel.processLastSearched()
                }
            }
    }

    /**
     * This handles the result of the user being displayed with the permissions prompt
     * If user grants permission then fetch weather for location
     * If user denies permission then fetch last searched if available
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permissions
                retrieveLocation()
            } else {
                //User denied permission so check for last searched
                viewModel.processLastSearched()
            }
        }
    }
}