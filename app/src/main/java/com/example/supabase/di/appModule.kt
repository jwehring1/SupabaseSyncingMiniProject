package com.example.supabase.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.supabase.BuildConfig
import com.example.supabase.data.repository.UserDatabase
import com.example.supabase.data.repository.UserRepository
import com.example.supabase.data.repository.impl.UserRepositoryImpl
import com.example.supabase.domain.usecase.CreateUserUseCase
import com.example.supabase.domain.usecase.GetShowUserUseCase
import com.example.supabase.domain.usecase.GetUsersSyncingUseCase
import org.koin.dsl.module
import com.example.supabase.domain.usecase.GetUsersUseCase
import com.example.supabase.domain.usecase.UpdateUserUseCase
import com.example.supabase.domain.usecase.impl.CreateUserUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetShowUserUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetUsersSyncingUseCaseImpl
import com.example.supabase.domain.usecase.impl.GetUsersUseCaseImpl
import com.example.supabase.domain.usecase.impl.SyncWorker
import com.example.supabase.domain.usecase.impl.UpdateUserUseCaseImpl
import com.example.supabase.presentation.feature.listuser.ListUserViewModel
import com.example.supabase.presentation.feature.showuser.ShowUserViewModel
import com.example.supabase.presentation.feature.updateuser.UpdateUserViewModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")

val appModule: Module
    get() = module {
    includes(databaseModule,
        viewModelsModule,
        useCasesModule,
        dataStoreModule,
        userRepositoryModule,
        supabaseModule,
        workerModule)
}

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), UserDatabase::class.java, "userDb").build().userDao()
    }
}

val viewModelsModule = module {
    viewModelOf(::ListUserViewModel)
    viewModelOf(::ShowUserViewModel)
    viewModelOf(::UpdateUserViewModel)
}

val useCasesModule = module {
    singleOf(::CreateUserUseCaseImpl) { bind<CreateUserUseCase>() }
    singleOf(::GetShowUserUseCaseImpl) { bind<GetShowUserUseCase>() }
    singleOf(::GetUsersSyncingUseCaseImpl) { bind<GetUsersSyncingUseCase>() }
    singleOf(::GetUsersUseCaseImpl) { bind<GetUsersUseCase>() }
    singleOf(::UpdateUserUseCaseImpl) { bind<UpdateUserUseCase>() }
}

val dataStoreModule = module {
    single {
        androidContext().dataStore
    }
}

val userRepositoryModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
        }
    }
}

val workerModule = module {
    workerOf(::SyncWorker)
}