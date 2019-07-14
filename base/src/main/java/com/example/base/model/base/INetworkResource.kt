package com.example.base.model.base

import io.reactivex.Observable
import io.reactivex.Single

interface INetworkResource<DataType> {

    val observable: Observable<NetworkResource.Snapshot<DataType>>

    val errorObservable: Observable<Throwable>

    fun isUpdateNeeded(): Boolean

    fun updateIfNeeded(): Single<NetworkResource.Snapshot<DataType>>

    fun update(): Single<NetworkResource.Snapshot<DataType>>

}