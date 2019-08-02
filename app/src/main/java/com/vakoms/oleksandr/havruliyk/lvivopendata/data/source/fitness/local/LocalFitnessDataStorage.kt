package com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.fitness.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.fitness.FitnessDataStorage
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitnesscenters.FitnessCentersRecord

class LocalFitnessDataStorage(context: Context) : FitnessDataStorage {

    companion object {
        const val TAG = "LocalFitnessDataStorage"
    }

    private var fitnessDao: FitnessDao

    init {
        val roomDB: FitnessRoomDatabase = Room.databaseBuilder(
            context,
            FitnessRoomDatabase::class.java, "database"
        )
            .allowMainThreadQueries()
            .build()

        fitnessDao = roomDB.fitnessDao()
    }

    override fun getFitnessData(): LiveData<List<FitnessCentersRecord>> {
        return fitnessDao.getAll()
    }

    override fun saveFitnessData(data: List<FitnessCentersRecord>) {
        fitnessDao.insert(data)
    }

    override fun deleteAllData() {
        fitnessDao.deleteAll()
    }
}