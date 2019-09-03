package com.example.base.model.base

import androidx.room.Entity
import androidx.room.Ignore
import com.example.base.core.MainSchedulers
import com.example.base.model.database.IDatabase
import com.example.base.model.database.NetworkResourceStateDao
import com.example.base.utils.Optional
import com.example.base.utils.noUnsubscribe
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.joda.time.*

abstract class NetworkResource<ResponseType, DataType>(
    private val database: IDatabase
) : INetworkResource<DataType> {

    @Entity(
        tableName = "NetworkResourceState",
        primaryKeys = ["ownerClassName", "ownerId"]
    )
    data class State(
        val ownerClassName: String,
        val ownerId: String,
        val lastSuccessfulUpdate: LocalDateTime? = null,
        @Ignore val lastError: Throwable? = null,
        @Ignore val isUpdating: Boolean = false
    ) {

        constructor(ownerClassName: String, ownerId: String, lastSuccessfulUpdate: LocalDateTime?) : this(
            ownerClassName, ownerId, lastSuccessfulUpdate, null, false
        )

    }

    data class Snapshot<DataType>(
        val state: State,
        val data: DataType?,
        val isDataChanged: Boolean = true
    )

    private lateinit var initialStateLoading: Completable
    private lateinit var currentState: State

    private val stateDao: NetworkResourceStateDao = database.getDao(NetworkResourceStateDao::class.java)

    private var snapshotStream: BehaviorSubject<Snapshot<DataType>> = BehaviorSubject.create<Snapshot<DataType>>()
    private var previousReportedData: DataType? = null
    private var errorStream: PublishSubject<Throwable> = PublishSubject.create()
    private var activeUpdate: SingleSubject<Snapshot<DataType>>? = null

    protected abstract val resourceId: String

    @Synchronized
    private fun loadInitialState(): Completable {
        if (::initialStateLoading.isInitialized) {
            return initialStateLoading
        }

        initialStateLoading = Single
            .fromCallable {
                var state: State? = stateDao.getState(javaClass.canonicalName, resourceId)
                if (state == null) {
                    state = State(javaClass.simpleName, resourceId)
                }
                Snapshot(state, loadData())
            }
            .subscribeOn(MainSchedulers.databaseScheduler)
            .doOnSuccess { snapshot ->
                currentState = snapshot.state
                previousReportedData = snapshot.data
                publishStateAndData(currentState, previousReportedData)
            }
            .ignoreElement()
            .cache()

        return initialStateLoading
    }

    private fun saveState(): Completable {
        return loadInitialState()
            .andThen(Completable.fromAction {
                stateDao.saveState(currentState)
            })
    }

    protected abstract val updatePeriod: Period

    protected abstract fun loadData(): DataType?

    protected abstract fun fetchData(): Single<ResponseType>

    protected abstract fun saveData(response: ResponseType)

    override val observable: Observable<Snapshot<DataType>>
        get() = loadInitialState().andThen(snapshotStream)

    override val errorObservable: Observable<Throwable>
        get() = errorStream

    private fun publishState(newState: State): Snapshot<DataType> {
        currentState = newState
        val snapshot = Snapshot(newState, previousReportedData, false)
        snapshotStream.onNext(snapshot)
        return snapshot
    }

    private fun publishStateAndData(newState: State, newData: DataType?): Snapshot<DataType> {
        currentState = newState
        previousReportedData = newData
        val snapshot = Snapshot(newState, newData, true)
        snapshotStream.onNext(snapshot)
        return snapshot
    }

    override fun isUpdateNeeded(): Boolean {
        val lastUpdate = currentState.lastSuccessfulUpdate
        return if (lastUpdate == null) {
            true
        } else {
            val now = LocalDateTime.now()
            Seconds.secondsBetween(now, lastUpdate).seconds > updatePeriod.seconds
        }
    }

    @Synchronized
    override fun updateIfNeeded(): Single<Snapshot<DataType>> {
        if (activeUpdate != null) {
            return activeUpdate!!
        }

        val update = loadInitialState()
            .andThen(Single.fromCallable { isUpdateNeeded() })
            .flatMap<Snapshot<DataType>> { needsUpdate ->
                if (needsUpdate) {
                    update()
                } else {
                    Single.just(snapshotStream.value)
                }
            }
            .cache()
        update
            .subscribe({ }, { })
            .noUnsubscribe()
        return update
    }

    @Synchronized
    override fun update(): Single<Snapshot<DataType>> {
        if (activeUpdate != null) {
            return activeUpdate!!
        }

        activeUpdate = SingleSubject.create<Snapshot<DataType>>()
        loadInitialState()
            .observeOn(MainSchedulers.networkScheduler)
            .andThen(Completable.fromAction {
                publishState(currentState.copy(isUpdating = true))
            })
            .andThen(fetchData())
            .observeOn(MainSchedulers.databaseScheduler)
            .doOnSuccess { response ->
                database.runInTransaction {
                    saveData(response)
                }
                currentState = currentState.copy(lastSuccessfulUpdate = LocalDateTime.now())
            }
            .flatMapCompletable { saveState() }
            .andThen(Single.fromCallable { Optional(loadData()) })
            .observeOn(MainSchedulers.uiScheduler)
            .subscribe({ dataOptional ->
                val snapshot = publishStateAndData(currentState.copy(isUpdating = false, lastError = null), dataOptional.value)
                activeUpdate!!.onSuccess(snapshot)
                activeUpdate = null
            }, { error ->
                publishState(currentState.copy(isUpdating = false, lastError = error))
                errorStream.onNext(error)
                activeUpdate!!.onError(error)
                activeUpdate = null
            })
            .noUnsubscribe()

        return activeUpdate!!
    }

    @Synchronized
    fun updateWithResponse(response: ResponseType): Single<Snapshot<DataType>> {
        return loadInitialState()
            .observeOn(MainSchedulers.databaseScheduler)
            .andThen(Completable.fromAction {
                database.runInTransaction {
                    saveData(response)
                }
                currentState = currentState.copy(lastSuccessfulUpdate = LocalDateTime.now())
            })
            .andThen(saveState())
            .andThen(Single.fromCallable { Optional(loadData()) })
            .map { dataOptional -> publishStateAndData(currentState, dataOptional.value) }
    }

    @Synchronized
    fun notifyDataChanged() {
        loadInitialState()
            .observeOn(MainSchedulers.databaseScheduler)
            .andThen(Single.fromCallable { Optional(loadData()) })
            .map { dataOptional -> publishStateAndData(currentState, dataOptional.value) }
            .subscribe()
    }

}