package com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.fitness.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitness.FitnessRecord

@Dao
interface FitnessDao {
    @Query("DELETE FROM fitness")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<FitnessRecord>)

    @Query("SELECT * FROM fitness WHERE id>:offset AND id<=:lastId ORDER BY id")
    fun get(offset: Int, lastId: Int): LiveData<List<FitnessRecord>>

    @Query("SELECT * FROM fitness WHERE name LIKE :name ORDER BY id")
    fun getByName(name: String): LiveData<List<FitnessRecord>>

    @Query("SELECT * FROM fitness WHERE id=:id")
    fun getById(id: Int): LiveData<FitnessRecord>

    @Query("SELECT * FROM fitness WHERE name LIKE :name ORDER BY id")
    fun getDataSourceFactoryByName(name: String): DataSource.Factory<Int, FitnessRecord>

    @Query("SELECT * FROM fitness ORDER BY id")
    fun getDataSourceFactory(): DataSource.Factory<Int, FitnessRecord>
}