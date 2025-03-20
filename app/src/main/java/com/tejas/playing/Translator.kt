package com.tejas.playing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tejas.playing.databinding.ActivityTranslatorBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslatorBinding
    private lateinit var translationService: TranslationService
    private var speechRecognizer: SpeechRecognizer? = null

    private val languageMap = mapOf(
        "English" to "en",
        "French" to "fr",
        "Spanish" to "es",
        "Japanese" to "jp",
        "Hindi" to "hi"
    )

    private var sourceLang = "en"  // Default: English
    private var destLang = "fr"    // Default: French

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.137.1:5000/") // Replace with actual backend URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        translationService = retrofit.create(TranslationService::class.java)

        binding.backButton.setOnClickListener { finish() }

        // Setup Language Spinners
        val languageList = languageMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languageList)
        binding.spinnerSource.adapter = adapter
        binding.spinnerDest.adapter = adapter

        // Set default selection
        binding.spinnerSource.setSelection(languageList.indexOf("English"))
        binding.spinnerDest.setSelection(languageList.indexOf("French"))

        // Update language codes when selection changes
        binding.spinnerSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                sourceLang = languageMap[languageList[position]] ?: "en"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerDest.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                destLang = languageMap[languageList[position]] ?: "fr"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnTranslate.setOnClickListener {
            val textToTranslate = binding.inputHindi.text.toString().trim()
            if (textToTranslate.isNotEmpty()) {
                translateText(textToTranslate)
            } else {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSpeechInput.setOnClickListener {
            startListening()
        }
        initSpeechRecognizer()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            initSpeechRecognizer()
        } else {
            requestAudioPermission()
        }

    }

    // Initialize SpeechRecognizer
    private fun initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("TranslatorActivity", "Speech recognition not available on this device")
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@TranslatorActivity, "Listening...", Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        binding.inputHindi.setText(spokenText)
                        translateText(spokenText)
                    }
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Try again."
                        SpeechRecognizer.ERROR_NETWORK -> "Network error. Check connection."
                        SpeechRecognizer.ERROR_AUDIO -> "Audio error. Try again."
                        SpeechRecognizer.ERROR_SERVER -> "Server error. Try later."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
                        SpeechRecognizer.ERROR_NO_MATCH -> "No match found. Speak again."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy. Try later."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Enable microphone access."
                        else -> "Unknown error. Try again."
                    }
                    Toast.makeText(this@TranslatorActivity, message, Toast.LENGTH_SHORT).show()
                }


                override fun onBeginningOfSpeech() {
                    Log.d("TranslatorActivity", "Speech started")
                }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    Log.d("TranslatorActivity", "Speech ended")
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun startListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermission()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, sourceLang)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
    }

    // Request Microphone Permission
    private fun requestAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Microphone access is required for speech recognition", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(this, "Permission denied. Speech recognition won't work!", Toast.LENGTH_SHORT).show()
        }
    }

    // Cleanup resources on stop
    override fun onStop() {
        super.onStop()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    // Translate text using API
    private fun translateText(text: String) {
        binding.btnTranslate.isEnabled = false
        val request = TranslationRequest(text, sourceLang, destLang)

        translationService.translateText(request).enqueue(object : Callback<TranslationResponse> {
            override fun onResponse(call: Call<TranslationResponse>, response: Response<TranslationResponse>) {
                binding.btnTranslate.isEnabled = true
                if (response.isSuccessful) {
                    binding.outputEnglish.text = response.body()?.translated_text ?: "Translation unavailable"
                } else {
                    Toast.makeText(this@TranslatorActivity, "Translation failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TranslationResponse>, t: Throwable) {
                binding.btnTranslate.isEnabled = true
                Toast.makeText(this@TranslatorActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("TranslatorActivity", "Translation failed", t)
            }
        })
    }
}
