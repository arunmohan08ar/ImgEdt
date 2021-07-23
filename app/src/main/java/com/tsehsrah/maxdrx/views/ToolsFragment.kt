package com.tsehsrah.maxdrx.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tsehsrah.imageops.imageOperations.configs.ImageOperators
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.adapters.OperatorListAdapter
import com.tsehsrah.imageops.imageOperations.models.IToolsStatus
import com.tsehsrah.maxdrx.databinding.FragmentToolsBinding
import com.tsehsrah.maxdrx.di.IServiceLocator

import com.tsehsrah.maxdrx.viewmodels.EditorVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ToolsFragment : Fragment(R.layout.fragment_tools) {
    private val editorVM: EditorVM by activityViewModels()
    @Inject
    lateinit var sL:IServiceLocator

    private lateinit var  tools: IToolsStatus
    private lateinit var binding: FragmentToolsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentToolsBinding.bind(view)
        tools=sL.getToolStatusCopy(editorVM.toolsStatus.value)
        regListeners()
        fun resetCntrls(){
            binding.toolSeekbar1.progress=0
            binding.toolSeekbar2.progress=100
        }
        editorVM.controlReset.observe(viewLifecycleOwner,{
            resetCntrls()
        }
        )

        val adpt= OperatorListAdapter(
            { toolMode->
                updateTool(toolMode)
                resetCntrls()
            },{ toolMode, v ->
                moreToolOptionsPopup(toolMode, v)
            }
        )
        adpt.data= ImageOperators.values()
        adpt.currentOperator=tools.getMode()
        val rv=binding.rcyclrToolSlct
        rv.layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        rv.adapter=adpt
    }
    private fun regListeners(){
        binding.toolSeekbar1.progress=tools.getSeekbar1()
        binding.toolSeekbar1.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                tools.setSeekbar1(progress)
                updateTool2VM()
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                tools.setSeekbar1(seek.progress)
                updateTool2VM()
            }
        })
        binding.toolSeekbar2.progress=tools.getSeekbar2()
        binding.toolSeekbar2.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,progress: Int, fromUser: Boolean) {
                tools.setSeekbar2(progress)
                updateTool2VM()
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {
                tools.setSeekbar2(seek.progress)
                updateTool2VM()
            }
        })
        binding.toolsUp.setOnClickListener {
            editorVM.toggleSelectionVisibility()
        }
        binding.toolsApply.setOnClickListener {
            editorVM.applyChanges()
        }
    }
    private fun updateTool(toolMode: ImageOperators){
        tools.setMode(toolMode)
        updateTool2VM( )
    }
    fun updateTool2VM(){
        editorVM.updateToolStatus(tools)
    }
    private fun moreToolOptionsPopup(t: ImageOperators, v:View){
        val inflater =  layoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_more_tool_options
            , LinearLayout(context)
            , false)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.showAsDropDown(v,0,- (v.height*3))

        popupView.findViewById<TextView>(R.id.tool_description)?.text=t.operator.description
        val seekbar=popupView.findViewById<SeekBar>(R.id.intencity_seekbar)
        if(t.operator.hasMoreOptions) {
            seekbar.visibility=VISIBLE
            popupView.findViewById<TextView>(R.id.tool_more_sb_head).visibility= VISIBLE
            seekbar?.progress = tools.getIntensity()
            seekbar?.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, p1: Int, p2: Boolean) {
                    tools.setIntensity(p1)
                    updateTool2VM()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(seek: SeekBar) {
                    tools.setIntensity(seek.progress)
                    updateTool2VM()
                }
            })
        }
    }
}
