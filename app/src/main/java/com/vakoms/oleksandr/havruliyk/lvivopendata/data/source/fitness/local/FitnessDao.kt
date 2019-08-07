package com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.fitness.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitness.FitnessRecord

@Dao
interface FitnessDao {
    @Query("SELECT * FROM fitness")
    fun getAll(): LiveData<List<FitnessRecord>>

    @Query("DELETE FROM fitness")
    fun deleteAll()

    @Insert
    fun insert(data: List<FitnessRecord>)

    @Query("SELECT * FROM fitness WHERE id=:id")
    fun getById(id: Int): LiveData<FitnessRecord>

    @Query("SELECT * FROM fitness WHERE name LIKE :name")
    fun getByName(name: String): LiveData<List<FitnessRecord>>
}