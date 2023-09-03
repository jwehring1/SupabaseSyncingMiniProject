package com.example.supabase.domain.usecase.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.example.supabase.domain.model.Database
import com.example.supabase.domain.model.User
import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.domain.usecase.GetUsersSyncingUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataStore: DataStore<Preferences>,
    private val getUsersSyncingUseCase: GetUsersSyncingUseCase,
    private val showUserUseCase: GetShowUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        var outcomes = mutableListOf<Boolean>()
        Log.e("Test", "Do Work")
        getTimeStamp().collectLatest {
            observeInternetConnectivity(applicationContext).collectLatest { connectivity ->
                if (connectivity) {
                    when (val result =
                        getUsersSyncingUseCase.execute(GetUsersSyncingUseCase.Input(time = it))) {
                        is GetUsersSyncingUseCase.Output.Success -> {
                            Log.e("Database: ", result.localResult.toString())
                            Log.e("Repository: ", result.remoteResult.toString())

                            outcomes =
                                syncWithUpdatedLocalDatabase(result.localResult, outcomes)
                            outcomes =
                                syncWithUpdatedRemoteDatabase(result.remoteResult, outcomes)

                            if (outcomes.isNotEmpty() && outcomes.all { outcome -> outcome }) {
                                outcomes.clear()
                                setTimeStamp()
                            }
                        }

                        GetUsersSyncingUseCase.Output.Failure -> {
                            Log.e("Fail", "Fail")
                        }
                    }
                }
            }
        }
        return Result.success()
    }

    private suspend fun syncWithUpdatedLocalDatabase(localResult: List<User>, outcomes: MutableList<Boolean>): MutableList<Boolean> {
        localResult.forEach { localUser ->
            when(val showUser = showUserUseCase.execute(GetShowUserUseCase.Input(localUser.uuid, Database.RemoteDatabase))) {
                is GetShowUserUseCase.Output.Success -> {
                    val remoteUser = showUser.data
                    outcomes.addAll(compareLocalAndRemoteDatabases(localUser, remoteUser))
                }
                GetShowUserUseCase.Output.Failure -> {
                    //Option 4: item is not in repository side, insert repository
                    outcomes.add(createUser(localUser, true))
                }
            }
        }
        return outcomes
    }

    private suspend fun syncWithUpdatedRemoteDatabase(remoteResult: List<User>, outcomes: MutableList<Boolean>): MutableList<Boolean> {
        remoteResult.forEach { remoteUser ->
            when(val showUser = showUserUseCase.execute(GetShowUserUseCase.Input(remoteUser.uuid, Database.LocalDatabase))) {
                is GetShowUserUseCase.Output.Success -> {
                    val localUser = showUser.data
                    outcomes.addAll(compareLocalAndRemoteDatabases(localUser, remoteUser))
                }
                GetShowUserUseCase.Output.Failure -> {
                    //Option 4: item is not in local side, insert local
                    outcomes.add(createUser(remoteUser, false))
                }
            }
        }
        return outcomes
    }

    private suspend fun compareLocalAndRemoteDatabases(localUser: User, remoteUser: User): MutableList<Boolean> {
        val outcomes = mutableListOf<Boolean>()
        //Option 1: item is the same, do nothing
        if (localUser != remoteUser) {
            //Option 2: item is more up to date on local side, update repository
            if (localUser.updated_at > remoteUser.updated_at) {
                outcomes.add(updateUser(localUser, true))
            }
            //Option 3: item is more up to date on repository side, update local
            else if (remoteUser.updated_at > localUser.updated_at) {
                outcomes.add(updateUser(remoteUser, false))
            }
        } else {
            outcomes.add(true)
        }
        return outcomes
    }

    private suspend fun updateUser(user: User, toRemote: Boolean): Boolean {
        val database = if (toRemote) { Database.RemoteDatabase } else { Database.LocalDatabase }
        return when(updateUserUseCase.execute(UpdateUserUseCase.Input(user, database))) {
            UpdateUserUseCase.Output.TotalSuccess -> {
                true
            }
            UpdateUserUseCase.Output.RemoteDatabaseFailure -> {
                !toRemote
            }
            UpdateUserUseCase.Output.LocalDatabaseFailure -> {
                toRemote
            }
            else -> {
                false
            }
        }
    }

    private suspend fun createUser(user: User, toRemote: Boolean): Boolean {
        val database = if (toRemote) { Database.RemoteDatabase } else { Database.LocalDatabase }
        return when(createUserUseCase.execute(CreateUserUseCase.Input(user, database))) {
            CreateUserUseCase.Output.TotalSuccess -> {
                true
            }
            CreateUserUseCase.Output.RemoteDatabaseFailure -> {
                !toRemote
            }
            CreateUserUseCase.Output.LocalDatabaseFailure -> {
                toRemote
            }
            else -> {
                false
            }
        }
    }

    private suspend fun setTimeStamp() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx")
        val formattedTimestamp = Instant.now().atOffset(ZoneOffset.UTC).format(formatter)
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("timestamp")] = formattedTimestamp.toString()
        }
    }

    private fun getTimeStamp(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey("timestamp")] ?: "2003-05-12T15:34:45.123456Z"
        }
    }

    private fun observeInternetConnectivity(context: Context): Flow<Boolean> {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(true) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(false) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(false) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(false) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }

}