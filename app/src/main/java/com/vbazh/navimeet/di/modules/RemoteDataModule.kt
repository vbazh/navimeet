package com.vbazh.navimeet.di.modules

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vbazh.navimeet.BuildConfig
import com.vbazh.navimeet.data.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RemoteDataModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient( ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        httpClient.addInterceptor { chain ->

            val original = chain.request()


            val requestBuilder = original.newBuilder()
//                .header("Bearer", bearer)
//                .header("Accept-Language", resourceManager.getLanguage())
//                .header(
//                    "X-CLIENT-VERSION",
//                    "android/${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_CI}"
//                )
//                .header(
//                    "Accept",
//                    "application/json;v=${BuildConfig.API_VERSION + BuildConfig.DEV_API_VERSION}"
//                )
                .method(original.method(), original.body())
            chain.proceed(requestBuilder.build())
        }

        httpClient.connectTimeout(25, TimeUnit.SECONDS)
        httpClient.readTimeout(25, TimeUnit.SECONDS)
        httpClient.writeTimeout(25, TimeUnit.SECONDS)

        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {

        return retrofit.create<ApiService>(ApiService::class.java)
    }

}