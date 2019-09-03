package com.example.base.model.database

import androidx.room.*
import com.example.base.model.base.NetworkResource

@Dao
interface NetworkResourceStateDao {

    @Query("SELECT * FROM NetworkResourceState WHERE NetworkResourceState.ownerClassName = :ownerClassName AND NetworkResourceState.ownerId = :ownerId")
    fun getState(ownerClassName: String?, ownerId: String): NetworkResource.State

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveState(state: NetworkResource.State)

}
