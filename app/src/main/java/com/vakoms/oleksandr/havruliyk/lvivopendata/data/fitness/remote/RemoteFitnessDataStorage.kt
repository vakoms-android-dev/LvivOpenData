package com.vakoms.oleksandr.havruliyk.lvivopendata.data.fitness.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.api.OpenDataApi
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.api.RetrofitService
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.fitness.FitnessDataStorage
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.market.remote.RemoteMarketDataStorage
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitnesscenters.FitnessCentersRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitnesscenters.FitnessCentersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteFitnessDataStorage : FitnessDataStorage {

    companion object {
        const val TAG = "RemoteMarketDataStorage"

        private var remoteFitnessDataStorage: RemoteFitnessDataStorage? = null

        fun getInstance(): RemoteFitnessDataStorage? {
            if (remoteFitnessDataStorage == null) {
                remoteFitnessDataStorage =
                    RemoteFitnessDataStorage()
            }
            return remoteFitnessDataStorage
        }
    }

    private val openDataApi: OpenDataApi = RetrofitService.createService(
        OpenDataApi::class.java
    )

    override fun getFitnessData(): MutableLiveData<FitnessCentersResponse> {
        val newsData = MutableLiveData<FitnessCentersResponse>()
        openDataApi.getFitnessCenters().enqueue(object : Callback<FitnessCentersResponse> {
            override fun onResponse(
                call: Call<FitnessCentersResponse>,
                response: Response<FitnessCentersResponse>
            ) {
                if (response.isSuccessful) {
                    newsData.value = response.body()
                    Log.i(RemoteMarketDataStorage.TAG, newsData.value.toString())
                }
            }

            override fun onFailure(call: Call<FitnessCentersResponse>, t: Throwable) {
                newsData.value = null
            }
        })
        return newsData
    }

    override fun saveFitnessData(data: List<FitnessCentersRecord>) {

    }

    override fun destroyInstance() {
        remoteFitnessDataStorage = null
    }
}