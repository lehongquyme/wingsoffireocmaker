package com.example.wingsoffireocmaker.core.service

import com.example.wingsoffireocmaker.core.service.ApiService
import com.example.wingsoffireocmaker.core.service.BaseRetrofitHelper
import com.example.wingsoffireocmaker.core.utils.key.DomainKey
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitClient : BaseRetrofitHelper() {
    val api =
        Retrofit.Builder().baseUrl(DomainKey.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient!!)
            .build()
            .create(ApiService::class.java)
}
