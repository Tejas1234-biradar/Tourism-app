package com.tejas.playing

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TranslationService {
    @POST("/translate")
    fun translateText(@Body request: TranslationRequest): Call<TranslationResponse>
}