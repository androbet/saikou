package ani.saikou.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import ani.saikou.*
import ani.saikou.databinding.ActivityUserInterfaceSettingsBinding
import com.google.android.material.snackbar.Snackbar

class UserInterfaceSettingsActivity : AppCompatActivity() {
    lateinit var binding : ActivityUserInterfaceSettingsBinding
    private val ui = "ui_settings"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInterfaceSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity(this)
        binding.uiSettingsContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = statusBarHeight
            bottomMargin = navBarHeight
        }

        val settings = loadData<UserInterfaceSettings>(ui, toast = false) ?: UserInterfaceSettings().apply { saveData(ui, this) }

        binding.uiSettingsBack.setOnClickListener {
            onBackPressed()
        }

        val views = resources.getStringArray(R.array.home_layouts)
        binding.uiSettingsHomeLayout.setOnClickListener {
            AlertDialog.Builder(this, R.style.DialogTheme).setTitle(getString(R.string.home_layout_show)).apply {
                setMultiChoiceItems(views, settings.homeLayoutShow.toBooleanArray()){ _, i, value ->
                    settings.homeLayoutShow[i] = value
                    saveData(ui,settings)
                }
            }.show()
        }

        binding.uiSettingsImmersive.isChecked = settings.immersiveMode
        binding.uiSettingsImmersive.setOnCheckedChangeListener { _, isChecked ->
            settings.immersiveMode = isChecked
            saveData(ui,settings)
            restartApp()
        }

        binding.uiSettingsBannerAnimation.isChecked = settings.bannerAnimations
        binding.uiSettingsBannerAnimation.setOnCheckedChangeListener { _, isChecked ->
            settings.bannerAnimations = isChecked
            saveData(ui,settings)
            restartApp()
        }

        binding.uiSettingsLayoutAnimation.isChecked = settings.layoutAnimations
        binding.uiSettingsLayoutAnimation.setOnCheckedChangeListener { _, isChecked ->
            settings.layoutAnimations = isChecked
            saveData(ui,settings)
            restartApp()
        }

        val map = mapOf(2f to 0.5f, 1.75f to 0.625f, 1.5f to 0.75f, 1.25f to 1.25f,1f to 1f,0.75f to 1.25f,0.5f to 1.5f,0.25f to 1.75f,0f to 0f)
        val mapReverse = map.map { it.value to it.key }.toMap()
        binding.uiSettingsAnimationSpeed.value = mapReverse[settings.animationSpeed]?:1f
        binding.uiSettingsAnimationSpeed.addOnChangeListener { _, value, _ ->
            settings.animationSpeed = map[value] ?: 1f
            saveData(ui,settings)
            restartApp()
        }


        var previous: View = when(settings.defaultStartUpTab){
            0 -> binding.uiSettingsAnime
            1 -> binding.uiSettingsHome
            2 -> binding.uiSettingsManga
            else -> binding.uiSettingsHome
        }
        previous.alpha = 1f
        fun uiTheme(mode:Int,current: View){
            previous.alpha = 0.33f
            previous = current
            current.alpha = 1f
            settings.defaultStartUpTab = mode
            saveData("ui_settings",settings)
            initActivity(this)
        }

        binding.uiSettingsAnime.setOnClickListener {
            uiTheme(0,it)
        }

        binding.uiSettingsHome.setOnClickListener {
            uiTheme(1,it)
        }

        binding.uiSettingsManga.setOnClickListener {
            uiTheme(2,it)
        }
    }

    private fun restartApp(){
        Snackbar.make(binding.root,
            R.string.restart_app, Snackbar.LENGTH_SHORT).apply {
            val mainIntent = Intent.makeRestartActivityTask(context.packageManager.getLaunchIntentForPackage(context.packageName)!!.component)
            setAction("Do it!") {
                context.startActivity(mainIntent)
                Runtime.getRuntime().exit(0)
            }
            show()
        }
    }
}