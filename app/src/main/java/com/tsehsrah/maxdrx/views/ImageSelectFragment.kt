package com.tsehsrah.maxdrx.views

import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity.CENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.adapters.ImageSelectListAdapter
import com.tsehsrah.maxdrx.viewmodels.SelectorVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageSelectFragment : Fragment(R.layout.fragment_image_select) {
    private val selectorVM: SelectorVM by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rtv  =inflater.inflate(R.layout.fragment_image_select, container, false)
        val adpt = ImageSelectListAdapter({pos -> imageSelected(pos)},{pos,v-> showOptionsPopup(pos,v)})
        selectorVM.imageSelectList.observe(viewLifecycleOwner,  { l ->
            adpt.data = l
            adpt.notifyDataSetChanged()
        })
        selectorVM.selectorHeadsUp.observe(viewLifecycleOwner,{
            it?.let {
                rtv?.findViewById<TextView>(R.id.selector_heads_up)?.text = it
            }
        })
        val rv           =rtv.findViewById<RecyclerView>(R.id.Img_slct_rclr)
        rv.layoutManager = LinearLayoutManager(this.context,
            if(resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT) {
                RecyclerView.HORIZONTAL
            }else{
                 RecyclerView.VERTICAL
                 },
            false)
        rv.adapter=adpt
        return rtv
    }
    private fun imageSelected(at:Int){
        selectorVM.setCurrentPos(at)
    }
    private fun showOptionsPopup(at:Int, v:View){
        val inflater        =  layoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_image_select
            , LinearLayout(context)
            , false)
        val width       = LinearLayout.LayoutParams.MATCH_PARENT
        val height      = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        (v as ImageView).drawable?.let{
            popupView.findViewById<ImageView>(R.id.select_popup_image)?.setImageDrawable(it)
        }
        popupWindow.showAtLocation(v,CENTER,0,0)
        popupView.findViewById<TextView>(R.id.img_slct_st_rfrnce)?.setOnClickListener {
            selectorVM.setReferencePos(at)
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.img_slct_st_sec)?.setOnClickListener {
            selectorVM.setSecondaryPos(at)
            popupWindow.dismiss()
        }
        popupView.findViewById<ImageView>(R.id.popup_image_replace)?.setOnClickListener {
            selectorVM.setExpectNewFile(at)
            popupWindow.dismiss()
        }
        popupView.findViewById<ImageView>(R.id.popup_img_delete)?.setOnClickListener {
            selectorVM.deleteDataAt(at)
            popupWindow.dismiss()
        }
    }
}










