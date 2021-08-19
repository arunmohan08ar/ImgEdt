package com.tsehsrah.maxdrx.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

import com.tsehsrah.maxdrx.viewmodels.SelectorVM
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.DEFAULT_PREVIEW_QUALITY
import com.tsehsrah.maxdrx.configs.CONSTANTS.DEFAULT_SAVE_FORMAT
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_AUTO_SWITCH
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_IMAGE_FORMAT
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_PREV_QUALITY
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_SETTINGS
import com.tsehsrah.maxdrx.configs.EventAt
import com.tsehsrah.maxdrx.configs.FBEvent
import com.tsehsrah.maxdrx.configs.ImageFormats
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.utilities.IImageSelectUtility
import com.tsehsrah.maxdrx.viewmodels.EditorVM

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class EditorActivity : AppCompatActivity() {

    @Inject
    lateinit var fragFactory:EditorFragmentFactory
    @Inject
    lateinit var sL:IServiceLocator
    private val imgSelect:IImageSelectUtility by lazy { sL.getImageSelectUtilities() }


    private val selectorVM : SelectorVM by viewModels()
    private val editorVM   : EditorVM   by viewModels()


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uri = result.data?.data
                uri?.let {
                    selectorVM.addNewWorkFiles(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_act_edtr)
        supportFragmentManager.fragmentFactory=fragFactory
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(TOOL_FRAG,ToolsFragment::class.java,null)
            replace(PRVW_FRAG,ImagePreviewFragment::class.java,null)
            replace(IMGS_FRAG,ImageSelectFragment::class.java,null)
            replace(HEADSUP_FRAG,HeadsUpFragment::class.java,null)
        }
        observe()
        sL.getAnalytics().initAnalytics(this)
        sL.getAdsManager().initAds(this)

    }

    private fun observe(){
        selectorVM.repoBusy.observe(this, { b ->
            repoBusy=b
            findViewById<ProgressBar>(PRGRS_REPO)?.visibility=if(b)VISIBLE else GONE
        })
        selectorVM.reqAddImage.observe(this, {
            if(it) {
                selectorVM.resetImageRequest()
                imgSelect.requestImage(this,resultLauncher,sL)
            }
        })
        editorVM.selectionFragmentVisibility.observe(this, {b->
            supportFragmentManager.findFragmentById(IMGS_FRAG)?.let {
                toggleFragmentVisibility(it,b)
            }
        })
    }

    private fun toggleFragmentVisibility(f: Fragment, vsb:Boolean){
        val ft=supportFragmentManager.beginTransaction()
        if(vsb) ft.show(f)
        else ft.hide(f)
        ft.commit()
    }

    private var uri: Uri? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.moptns, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(sL.getAdsManager().display(this)){
            return super.onOptionsItemSelected(item)
        }
        when(item.itemId){
            SVE_BTN     ->  editorVM.requestImageSave(when(getSharedPreferences(PREF_SETTINGS,
                MODE_PRIVATE)
                .getString(PREF_IMAGE_FORMAT, DEFAULT_SAVE_FORMAT)
            ){
                    ImageFormats.WEBP.s-> ImageFormats.WEBP.format
                    ImageFormats.JPEG.s-> ImageFormats.JPEG.format
                    else-> ImageFormats.PNG.format
            })
            OPN_BTN     ->  reqNewFile()
            SETTINGS_BTN->  showSettingsPopup()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reqNewFile(){
        if(repoBusy){
            Toast.makeText(this,getText(R.string.please_wait),LENGTH_SHORT).show()
        }else{
            selectorVM.setExpectNewFile()
            sL.getAnalytics().logEvent(FBEvent.NEW_FILE,at= EventAt.EDITOR_ACT)
        }
    }

    override fun onBackPressed() {
        if(backPress){
            selectorVM.windUpAndClearAll()
            editorVM.clearAll()
            super.onBackPressed()
        }else {
            backPress()
        }
    }
    private fun backPress(){
        CoroutineScope(sL.getIODispatcher()).launch {
            backPress=true
            delay(800)
            backPress=false
        }
        Toast.makeText(this,getText(R.string.back_again_to_exit), LENGTH_SHORT).show()
    }
    private var selected:View?=null
    private fun showSettingsPopup(){
        val v:View=findViewById(R.id.repo_progress)
        val inflater        =  layoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_settings
            , LinearLayout(this)
            , false)
        val width       = LinearLayout.LayoutParams.MATCH_PARENT
        val height      = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.showAtLocation(v, Gravity.CENTER,0,0)
        val sp=getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        val png:TextView=popupView.findViewById(R.id.settings_text_png)
        val jpeg:TextView=popupView.findViewById(R.id.settings_text_jpeg)
        val webp:TextView=popupView.findViewById(R.id.settings_text_webp)
        val autoSwh:SwitchCompat=popupView.findViewById(R.id.settings_auto_switch)

        selected=when(sp.getString(PREF_IMAGE_FORMAT, DEFAULT_SAVE_FORMAT)?:DEFAULT_SAVE_FORMAT){
            ImageFormats.WEBP.s->webp
            ImageFormats.JPEG.s->jpeg
            else->png
        }
        selected?.setBackgroundColor(SELECTED_COLOR)
        autoSwh.isChecked=sp.getBoolean(PREF_AUTO_SWITCH,true)
        autoSwh.setOnCheckedChangeListener { _, b ->
            sp.edit().putBoolean(PREF_AUTO_SWITCH,b).apply()
            editorVM.updatePreferences()
        }

        fun formatChange(v:View,s:String){
            v.setBackgroundColor(SELECTED_COLOR)
            selected?.setBackgroundColor(NORMAL_COLOR)
            selected=v
            sp.edit().putString(PREF_IMAGE_FORMAT,s).apply()
        }
        png.setOnClickListener {formatChange(it,ImageFormats.PNG.s)}
        jpeg.setOnClickListener{formatChange(it,ImageFormats.JPEG.s)}
        webp.setOnClickListener{formatChange(it,ImageFormats.WEBP.s)}
        val seekbar:SeekBar=popupView.findViewById(R.id.settings_quality_seekbar)
        seekbar.progress=sp.getInt(PREF_PREV_QUALITY, (DEFAULT_PREVIEW_QUALITY*100).toInt())
        seekbar.setOnSeekBarChangeListener(
            object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar) {}
            override fun onStopTrackingTouch(p0: SeekBar) {
                getSharedPreferences(PREF_SETTINGS, MODE_PRIVATE)
                    .edit()
                    .putInt(PREF_PREV_QUALITY,p0.progress)
                    .apply()
                editorVM.updatePreferences()
            }
        })
        popupWindow.setOnDismissListener { PopupMenu.OnDismissListener{
            selected=null
        } }
    }

    companion object ActEdtCOMPONENTS{
        private const val SELECTED_COLOR=Color.GRAY
        private const val NORMAL_COLOR=Color.TRANSPARENT
        private var repoBusy:Boolean=false
        private var backPress:Boolean=false

        private const val HEADSUP_FRAG=R.id.frag_heads_up
        private const val TOOL_FRAG=R.id.frag_tool_select
        private const val IMGS_FRAG=R.id.frag_img_select
        private const val PRVW_FRAG=R.id.frag_img_view
        private const val PRGRS_REPO=R.id.repo_progress
        private const val SVE_BTN=R.id.Act_edt_Opt_Sve
        private const val OPN_BTN=R.id.Act_edt_Opt_Opn
        private const val SETTINGS_BTN=R.id.act_edt_opt_settings
    }

}



