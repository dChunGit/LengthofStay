package com.simplex.whatsup.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.simplex.whatsup.api.network.HeaderInterceptor
import com.simplex.whatsup.api.mongo.MongoRepositoryImpl
import com.simplex.whatsup.api.mongo.MongoRepository
import com.simplex.whatsup.api.network.NetworkErrorHandler
import com.simplex.whatsup.api.nfc.NFCRepository
import com.simplex.whatsup.api.nfc.NFCRepositoryImpl
import com.simplex.whatsup.api.nfc.NdefApi
import com.simplex.whatsup.viewmodels.EventViewModel
import com.simplex.whatsup.viewmodels.AddViewModel
import com.simplex.whatsup.viewmodels.LogInViewModel
import com.simplex.whatsup.viewmodels.MapViewModel
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

val dataModule = module {
    single<MongoRepository> { MongoRepositoryImpl(get()) }

    single { provideFirebaseInstance() }
    single { provideFirebaseAuth() }

    single { provideDefaultRetrofitClient(get()) }
    single { provideHeaderInterceptor() }
}

val viewModelModule = module {
    viewModel { AddViewModel(get()) }
    viewModel { EventViewModel(get())}
    viewModel { LogInViewModel(get()) }
    viewModel { MapViewModel(get(), get()) }
}

val extrasModule = module {
    single { NetworkErrorHandler(androidContext()) }
}

val nfcModule = module {
    single<NFCRepository> { NFCRepositoryImpl(get()) }
    single { NdefApi() }

    single { provideMoshiClient() }
}

fun provideDefaultRetrofitClient(headerInterceptor: HeaderInterceptor): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://5412-dot-whats-up-255316.appspot.com")
//        .baseUrl("https://7928df92.ngrok.io")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(OkHttpClient()
            .newBuilder()
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(headerInterceptor)
            .build())
        .build()

fun provideHeaderInterceptor(): HeaderInterceptor = HeaderInterceptor()

fun provideFirebaseInstance(): FirebaseStorage = FirebaseStorage.getInstance()

fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

fun provideMoshiClient(): Moshi = Moshi.Builder().build()
