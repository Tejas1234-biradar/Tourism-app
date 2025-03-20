package com.tejas.playing

data class TranslationRequest(
    var text: String,
    var source_lang: String,  // Add this property
    var target_lang: String     // Add this property
)

