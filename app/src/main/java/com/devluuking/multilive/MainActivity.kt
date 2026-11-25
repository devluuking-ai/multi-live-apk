package com.devluuking.multilive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var statusText: TextView
    private lateinit var controlBar: View
    private lateinit var stopChip: Button
    private lateinit var liveButton: Button
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val svc = Intent(this, StreamService::class.java).apply {
                action = StreamService.ACTION_START
                putExtra(StreamService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(StreamService.EXTRA_DATA, result.data)
            }
            ContextCompat.startForegroundService(this, svc)
            setLiveUi(true)
            webView.evaluateJavascript("window.startAudioOnce && startAudioOnce()", null)
        } else {
            toast("Screen capture ခွင့်ပြုချက် မရပါ")
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.RECORD_AUDIO] == true) {
            launchProjection()
        } else {
            toast("Microphone ခွင့်ပြုချက် လိုအပ်ပါသည်")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        statusText = findViewById(R.id.statusText)
        controlBar = findViewById(R.id.controlBar)
        stopChip = findViewById(R.id.stopChip)
        liveButton = findViewById(R.id.liveButton)

        setupWebView()
        setupTts()

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        liveButton.setOnClickListener { onGoLive() }
        stopChip.setOnClickListener { stopLive() }
        findViewById<Button>(R.id.simChatButton).setOnClickListener {
            webView.evaluateJavascript("window.simChat && simChat()", null)
        }
        findViewById<Button>(R.id.simGiftButton).setOnClickListener {
            webView.evaluateJavascript("window.simGift && simGift()", null)
        }
        findViewById<Button>(R.id.botsButton).setOnClickListener {
            webView.evaluateJavascript("window.toggleBots && toggleBots()", null)
        }

        StreamService.statusListener = { msg ->
            runOnUiThread {
                statusText.text = msg
                if (msg.startsWith("Live ရပ်")) setLiveUi(false)
            }
        }
        setLiveUi(StreamService.streaming)
        statusText.text = if (StreamService.streaming) "Live လွှင့်နေသည်" else "အဆင်သင့် — ⚙ Keys မှာ stream key ထည့်ပြီး Go Live နှိပ်ပါ"
    }

    private fun setupWebView() {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(Bridge(), "AndroidBridge")
        webView.loadUrl("file:///android_asset/www/scene.html")
    }

    private fun setupTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("my", "MM"))
                ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    inner class Bridge {
        @JavascriptInterface
        fun speak(text: String) {
            if (!ttsReady) return
            tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "mlive")
        }
    }

    private fun onGoLive() {
        val dests = Prefs.destinations(this).filter { it.enabled && it.server.isNotBlank() && it.key.isNotBlank() }
        if (dests.isEmpty()) {
            toast("အရင် ⚙ Keys မှာ platform တစ်ခုခုရဲ့ stream key ထည့်ပါ")
            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        val missing = perms.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        } else {
            launchProjection()
        }
    }

    private fun launchProjection() {
        val mpm = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projectionLauncher.launch(mpm.createScreenCaptureIntent())
    }

    private fun stopLive() {
        val svc = Intent(this, StreamService::class.java).apply { action = StreamService.ACTION_STOP }
        startService(svc)
        setLiveUi(false)
    }

    private fun setLiveUi(live: Boolean) {
        controlBar.visibility = if (live) View.GONE else View.VISIBLE
        stopChip.visibility = if (live) View.VISIBLE else View.GONE
        statusText.visibility = if (live) View.GONE else View.VISIBLE
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    override fun onDestroy() {
        StreamService.statusListener = null
        tts?.shutdown()
        super.onDestroy()
    }
}
