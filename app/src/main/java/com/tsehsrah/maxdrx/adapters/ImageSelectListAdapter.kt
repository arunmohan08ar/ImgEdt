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
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil


class ImageSelectListAdapter (private val onClick: (pos:Int) -> Unit, private val onLongClick:(pos:Int,v:View)->Unit)  :
    RecyclerView.Adapter<ImageSelectListAdapter.SelectImage>() {

    private val diff: AsyncListDiffer<IItemImageSelectList> = AsyncListDiffer(this, DiffCallBack)

    fun submitList(l:List<IItemImageSelectList>){
        diff.submitList(l)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SelectImage {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_img_lst, viewGroup, false)
        return SelectImage(view)
    }

    override fun onBindViewHolder(viewHolder: SelectImage, position: Int) {
        viewHolder.bind(diff.currentList[position],position,onClick,onLongClick)
    }
    override fun getItemCount() = diff.currentList.size




    class SelectImage(view: View) : RecyclerView.ViewHolder(view) {
        private val imgV: ImageView = view.findViewById(R.id.Itm_img)
        private val tV: TextView = view.findViewById(R.id.Img_Itm_Lbl)

        fun bind(dta: IItemImageSelectList,
                 position: Int,
                 onClick: (pos:Int) -> Unit,
                 onLongClick:(pos:Int,v:View)->Unit){
            imgV.setImageBitmap(dta.thumpBmp)
            tV.text=dta.nme
            tV.setBackgroundColor(clrLte)
            tV.setTextColor(clrDrk)
            imgV.setOnClickListener {
                onClick(position)
            }
            imgV.setOnLongClickListener { v->
                onLongClick(position,v)
                true
            }
        }
        companion object{
            val clrLte= Color.parseColor("#FFFFFF")
            val clrDrk= Color.parseColor("#111111")
        }
    }

    object DiffCallBack : DiffUtil.ItemCallback<IItemImageSelectList>() {
        override fun areItemsTheSame(
            oldItem: IItemImageSelectList,
            newItem: IItemImageSelectList
        ): Boolean {
            return oldItem.nme == newItem.nme
        }

        override fun areContentsTheSame(
            oldItem: IItemImageSelectList,
            newItem: IItemImageSelectList
        ): Boolean {
            return  oldItem.thumpBmp?.equals(newItem.thumpBmp)?:false
        }

    }
}
