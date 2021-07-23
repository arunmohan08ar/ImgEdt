package com.tsehsrah.maxdrx.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.models.IItemImageSelectList

class ImageSelectListAdapter (private val onClick: (pos:Int) -> Unit, private val onLongClick:(pos:Int,v:View)->Unit)  :
    RecyclerView.Adapter<ImageSelectListAdapter.SelectImage>() {
    var data= listOf<IItemImageSelectList>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SelectImage {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_img_lst, viewGroup, false)

        return SelectImage(view)
    }

    override fun onBindViewHolder(viewHolder: SelectImage, position: Int) {
        viewHolder.imgV.setOnClickListener {
            onClick(position)
        }
        viewHolder.imgV.setOnLongClickListener { v->
            onLongClick(position, v)
            true
        }
        viewHolder.bind(data[position])
    }
    override fun getItemCount() = data.size




    class SelectImage(view: View) : RecyclerView.ViewHolder(view) {
        val imgV: ImageView = view.findViewById(R.id.Itm_img)
        val tV: TextView = view.findViewById(R.id.Img_Itm_Lbl)

        fun bind(dta: IItemImageSelectList){
            imgV.setImageBitmap(dta.thumpBmp)
            tV.text=dta.nme
            tV.setBackgroundColor(clrLte)
            tV.setTextColor(clrDrk)
        }
        companion object{
            val clrLte= Color.parseColor("#FFFFFF")
            val clrDrk= Color.parseColor("#111111")
        }
    }



}
