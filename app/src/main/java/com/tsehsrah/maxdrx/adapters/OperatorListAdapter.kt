package com.tsehsrah.maxdrx.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_TOOL_MODE
import com.tsehsrah.imageops.imageOperations.configs.ImageOperators
import com.tsehsrah.maxdrx.R

class OperatorListAdapter (private val onClick: (ImageOperators) -> Unit,
                           private val onLongClick:(ImageOperators, View) -> Unit) :
    RecyclerView.Adapter<OperatorListAdapter.SelectTool>() {
    var data:Array<ImageOperators> =arrayOf()
    var currentOperator:ImageOperators=DEFAULT_TOOL_MODE
    private var old: SelectTool?=null
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SelectTool {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_tool_slct, viewGroup, false)

        return SelectTool(view)
    }

    override fun onBindViewHolder(viewHolder: SelectTool, position: Int) {
        viewHolder.toolName.setOnClickListener {
            onClick(data[position])
            old?.updt(false)
            viewHolder.updt(true)
            old=viewHolder
            currentOperator=data[position]
        }
        viewHolder.toolName.setOnLongClickListener {
            onLongClick(data[position], it)
            true
        }
        if(currentOperator==data[position]){
            old=viewHolder
            viewHolder.updt(true)
        }
        viewHolder.bind(data[position])
    }
    override fun getItemCount() = data.size


    class SelectTool(view: View) : RecyclerView.ViewHolder(view) {
        val toolName: TextView = view.findViewById(R.id.tool_lst_itm_txt)

        fun bind(itm: ImageOperators){
            toolName.text=itm.operator.name
            toolName.hint=itm.operator.description
        }
        fun updt(b:Boolean){
            if(b)toolName.setBackgroundColor(colorSelected)
            else toolName.setBackgroundColor(colorNormal)
        }
        companion object{
            val colorSelected=Color.parseColor("#22222222")
            val colorNormal=Color.parseColor("#66666666")
        }
    }

}
