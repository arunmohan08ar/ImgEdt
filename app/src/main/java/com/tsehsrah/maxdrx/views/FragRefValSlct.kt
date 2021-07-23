package com.tsehsrah.maxdrx.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.models.IRefVal
import com.tsehsrah.maxdrx.viewmodels.ViewModelRefSlct
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragRefValSlct : Fragment() {

    val VMR: ViewModelRefSlct by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vw=inflater.inflate(R.layout.fragment_reference_select, container, false)
        vw.setOnClickListener {
            showPpup(it)
        }
        VMR.opRefPrvw.observe(viewLifecycleOwner,{img->
            vw.findViewById<ImageView>(R.id.ref_clr_img).setImageBitmap(img)
        })
        VMR.updtRef(128,128,128)
       // VM.updtRef(RefVal())
        return vw
    }

    fun showPpup(v:View){
        val inflater =  layoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_color_select
            , LinearLayout(context)
            , false)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.showAsDropDown(v)
        setSkbrLstnrs(popupView)
        popupWindow.setOnDismissListener {
            val rfvl=VMR.getRefVals()
          //  VM.updtRef(rfvl)
        }
    }

    private fun setSkbrLstnrs(v: View?) {
        val ref: IRefVal =VMR.getRefVals()
        v?.findViewById<SeekBar>(R.id.tool_seekbar1)?.progress=(ref.gtR()*100)/255
        v?.findViewById<SeekBar>(R.id.clr_slct_skbrG)?.progress=(ref.gtG()*100)/255
        v?.findViewById<SeekBar>(R.id.clr_slct_skbrB)?.progress=(ref.gtB()*100)/255

        v?.findViewById<SeekBar>(R.id.tool_seekbar1)?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                VMR.updtRef(r=(progress*(255.0/100.0)).toInt())
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                VMR.updtRef(r=(seek.progress*(255.0/100.0)).toInt())
            }
        })
        v?.findViewById<SeekBar>(R.id.clr_slct_skbrG)?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                VMR.updtRef(g=(progress*(255.0/100.0)).toInt())
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                VMR.updtRef(g=(seek.progress*(255.0/100.0)).toInt())
            }
        })
        v?.findViewById<SeekBar>(R.id.clr_slct_skbrB)?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                VMR.updtRef(b=(progress*(255.0/100.0)).toInt())
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                VMR.updtRef(b=(seek.progress*(255.0/100.0)).toInt())
            }
        })
    }


/*

    private lateinit var alertDialog: AlertDialog
    fun showCustomDialog() {
        val inflater: LayoutInflater = this.getLayoutInflater()
        val ppup: View = inflater.inflate(R.layout.popup_clr_slct, null)

        val header_txt = dialogView.findViewById<TextView>(R.id.header)
        header_txt.text = "Header Message"
        val details_txt = dialogView.findViewById<TextView>(R.id.details)
        val custom_button: Button = dialogView.findViewById(R.id.customBtn)
        custom_button.setOnClickListener {
            //perform custom action
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialogBuilder.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(arg0: DialogInterface) {

            }
        })
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create();
        alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
        alertDialog.show()
    }

*/


}