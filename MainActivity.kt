package com.example.aifpsbooster

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var advice: TextView
    private val green = Color.rgb(70, 255, 145)
    private val bg = Color.rgb(12, 15, 20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
        refreshStatus()
    }

    private fun buildScreen() {
        val scroll = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 38, 28, 28)
            setBackgroundColor(bg)
        }
        scroll.addView(layout)

        val title = TextView(this).apply {
            text = "AI FPS BOOSTER"
            textSize = 27f
            setTextColor(green)
            gravity = Gravity.CENTER
        }
        layout.addView(title)

        val subtitle = TextView(this).apply {
            text = "REDMI NOTE 13 PRO 5G  •  FREE FIRE"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        layout.addView(subtitle)

        status = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(18, 18, 18, 18)
            setBackgroundColor(Color.rgb(27, 34, 42))
        }
        layout.addView(status)

        val boost = makeButton("ANALISAR E OTIMIZAR")
        boost.setOnClickListener {
            refreshStatus()
            advice.text = buildAdvice()
        }
        layout.addView(boost)

        val launch = makeButton("ABRIR FREE FIRE")
        launch.setOnClickListener { launchFreeFire() }
        layout.addView(launch)

        val settings = makeButton("CONFIGURAÇÕES DE BATERIA")
        settings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
        }
        layout.addView(settings)

        val info = TextView(this).apply {
            text = "RECOMENDAÇÕES"
            textSize = 18f
            setTextColor(green)
            setPadding(0, 26, 0, 8)
        }
        layout.addView(info)

        advice = TextView(this).apply {
            text = "Toque em ANALISAR para receber recomendações com base no estado atual do aparelho."
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(18, 18, 18, 18)
            setBackgroundColor(Color.rgb(27, 34, 42))
        }
        layout.addView(advice)

        val disclaimer = TextView(this).apply {
            text = "\nEste app não altera o Free Fire, não desbloqueia FPS e não encerra à força processos de outros apps. Ele apresenta indicadores e ajustes seguros. O FPS real depende do jogo, da temperatura e dos limites do aparelho."
            textSize = 12f
            setTextColor(Color.LTGRAY)
        }
        layout.addView(disclaimer)

        setContentView(scroll)
    }

    private fun makeButton(label: String): Button = Button(this).apply {
        text = label
        textSize = 14f
        isAllCaps = false
        setTextColor(Color.BLACK)
        setBackgroundColor(green)
        val p = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        p.setMargins(0, 12, 0, 4)
        layoutParams = p
    }

    private fun refreshStatus() {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val total = mi.totalMem / (1024 * 1024)
        val free = mi.availMem / (1024 * 1024)
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val saver = pm.isPowerSaveMode
        val thermal = if (Build.VERSION.SDK_INT >= 29) {
            when (pm.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> "Normal"
                PowerManager.THERMAL_STATUS_LIGHT -> "Leve"
                PowerManager.THERMAL_STATUS_MODERATE -> "Moderada"
                PowerManager.THERMAL_STATUS_SEVERE -> "Alta"
                PowerManager.THERMAL_STATUS_CRITICAL -> "Crítica"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergência"
                else -> "Indisponível"
            }
        } else "Não disponível nesta versão"
        status.text = "MEMÓRIA DISPONÍVEL: ${free} MB / ${total} MB\n" +
                "ECONOMIA DE BATERIA: ${if (saver) "ATIVADA" else "DESATIVADA"}\n" +
                "ESTADO TÉRMICO: $thermal"
    }

    private fun buildAdvice(): String {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val tips = mutableListOf<String>()
        if (pm.isPowerSaveMode) tips.add("• Desative a Economia de bateria antes de jogar, se o aparelho estiver frio e com carga suficiente.")
        if (Build.VERSION.SDK_INT >= 29 && pm.currentThermalStatus >= PowerManager.THERMAL_STATUS_MODERATE)
            tips.add("• O aparelho está aquecido. Faça uma pausa e deixe esfriar antes de jogar.")
        if (mi.availMem < 900L * 1024L * 1024L)
            tips.add("• Há pouca memória livre. Feche manualmente apps que você não está usando.")
        tips.add("• Nas opções do Free Fire, escolha gráficos suaves e a maior taxa de FPS oficialmente disponível.")
        tips.add("• Evite jogar carregando se isso aumentar muito a temperatura.")
        tips.add("• Mantenha o jogo e o sistema atualizados.")
        return "Análise concluída:\n\n" + tips.joinToString("\n\n") +
                "\n\nO botão não faz overclock nem modifica arquivos do jogo."
    }

    private fun launchFreeFire() {
        val packages = listOf("com.dts.freefireth", "com.dts.freefiremax")
        for (pkg in packages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                startActivity(intent)
                return
            }
        }
        advice.text = "Não encontrei o Free Fire instalado. Instale/atualize o jogo pela loja oficial e tente novamente."
    }
}
