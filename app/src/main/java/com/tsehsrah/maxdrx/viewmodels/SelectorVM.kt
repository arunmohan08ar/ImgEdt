package com.tsehsrah.maxdrx.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.BLANK
import com.tsehsrah.maxdrx.configs.CONSTANTS.INVALID
import com.tsehsrah.maxdrx.configs.EventAt
import com.tsehsrah.maxdrx.configs.FBEvent
import com.tsehsrah.maxdrx.di.IServiceLocator

import com.tsehsrah.maxdrx.models.IItemImageSelectList
import com.tsehsrah.maxdrx.repos.IImageRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SelectorVM @Inject constructor(application: Application,
                                     private val sL:IServiceLocator,
                                     private val imgRepo:IImageRepository
    ): AndroidViewModel(application)
{

    private val _reqAddImage:MutableLiveData<Boolean> by lazy { MutableLiveData(false) }
    val reqAddImage:LiveData<Boolean> = _reqAddImage

    private val _repoBusy:MutableLiveData<Boolean> by lazy {  MutableLiveData(false)}
    val repoBusy:LiveData<Boolean> =_repoBusy

    private val _imageSelectList: MutableLiveData<ArrayList<IItemImageSelectList>>
                                                    by lazy{MutableLiveData(imageList)}
    val imageSelectList: LiveData<ArrayList<IItemImageSelectList>> = _imageSelectList

    private val _selectorHeadsUp:MutableLiveData<String> by lazy{MutableLiveData("")}
    val selectorHeadsUp:LiveData<String> = _selectorHeadsUp

    private val _headsUpPreview:MutableLiveData<Bitmap?> by lazy {MutableLiveData(null)}
    val headsUpPreview:LiveData<Bitmap?> = _headsUpPreview

    private val _headsUpReference:MutableLiveData<Bitmap?> by lazy {MutableLiveData(null)}
    val headsUpReference:LiveData<Bitmap?> = _headsUpReference

    private val _headsUpSecondary:MutableLiveData<Bitmap?> by lazy {MutableLiveData(null)}
    val headsUpSecondary:LiveData<Bitmap?> =_headsUpSecondary


    init {
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            imgRepo.busyFlag.collect {
                _repoBusy.postValue(it>0)
            }
        }

        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            imgRepo.workDirectoryUpdated.collect{
                it?.let {
                    updateSelectionList(it)
                }
            }
        }
    }

    fun setExpectNewFile(at:Int= imageList.size){
        if(repoBusy.value==true) {
            return
        }
        expectAt =at
        _reqAddImage.postValue(true)
    }
    fun resetImageRequest(){
        _reqAddImage.postValue(false)
    }

    fun addNewWorkFiles(uri: Uri){
        try{
            imgRepo.addNewWrkFiles(
                sL.getAppContext(this@SelectorVM),
                expectAt,
                uri
            )
        }catch (oom:OutOfMemoryError){
            handleOOM("new")
        }catch (io:IOException){
            sL.getAnalytics().logEvent(FBEvent.IOException)
        }
    }

    fun setCurrentPos(pos:Int?=null){
        try{
            pos?.let {
                imgRepo.setCurrentPos(sL.getAppContext(this@SelectorVM), pos,false)
                _headsUpPreview.postValue(imageList[pos].thumpBmp)
            } ?: imgRepo.setCurrentPos(sL.getAppContext(this@SelectorVM),null,false)
        }catch (oom:OutOfMemoryError){
            handleOOM("setP")
        }
    }

    fun setReferencePos(pos:Int){
        if(pos==INVALID){
            _headsUpReference.postValue(null)
            return
        }
        try{
            imgRepo.setReference(sL.getAppContext(this@SelectorVM), pos)
        }catch (oom:OutOfMemoryError){
            handleOOM("setR")
        }
        _headsUpReference.postValue(imageList[pos].thumpBmp)
    }
    fun setSecondaryPos(pos:Int){
        try{
            imgRepo.setSecondary(sL.getAppContext(this@SelectorVM),pos)
        }catch (oom:OutOfMemoryError){
            handleOOM("setS")
        }
        _headsUpSecondary.postValue(imageList[pos].thumpBmp)
    }

    private fun updateSelectionList(at:Int= expectAt){
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            try {
                val bmp = imgRepo.getBmp(
                    sL.getAppContext(this@SelectorVM),
                    at,
                    true
                )
                val itm = getSelectListItem(
                    bmp,
                    "$at"
                )
                if (at == imageList.size) {
                    imageList.add(itm)
                } else {
                    imageList[at] = itm
                }
            }catch (oom:OutOfMemoryError){
                handleOOM("Updt_lst")
            }
            updateAndForceReload()
        }
    }

    private fun updateAndForceReload(){
        _imageSelectList.postValue(imageList)
        try {
            imgRepo.setCurrentPos(
                sL.getAppContext(this@SelectorVM),
                pos = null ,
                forceReload = true
            )
        }catch (oom:OutOfMemoryError){
            handleOOM("U_FR")
        }
    }

    private fun getSelectListItem(thump: Bitmap?, name:String):IItemImageSelectList{
        val itm:IItemImageSelectList=sL.getItemImageSelectList()
        itm.thumpBmp=thump
        itm.nme=name
        return itm
    }

    fun deleteDataAt(at:Int){
        try{
            imgRepo.removeDataAt(
                sL.getAppContext(this@SelectorVM),
                at,
                imageList.size - 1
            ) {
                if (!it) {
                    _selectorHeadsUp.postValue(
                        sL.getAppContext(this@SelectorVM)
                            .getString(R.string.req_app_restart)
                    )
                } else {
                    _selectorHeadsUp.postValue(BLANK)
                }
                imageList.removeAt(at)
                if (imageList.size == 0) {
                    try {
                        imgRepo.setCurrentPos(
                            sL.getAppContext(this@SelectorVM),
                            INVALID,
                            true
                        )
                    } catch (oom: OutOfMemoryError) {
                        handleOOM("del")

                    }
                }
                updateAndForceReload()
            }
        }catch (io:IOException){
            sL.getAnalytics().logEvent(FBEvent.IOException,description="del")
        }
    }

    fun windUpAndClearAll(){
        try{
            imgRepo.clearAllWorkData(sL.getAppContext(this@SelectorVM))
        }catch (io:IOException){
            sL.getAnalytics().logEvent(FBEvent.OOM,description="clr",at = EventAt.SELECTOR_VM)
        }
        expectAt=0
        imageList.clear()
        _imageSelectList.postValue(imageList)
    }

    private fun handleOOM(description:String){
        sL.getAnalytics().logEvent(FBEvent.OOM,description=description,at=EventAt.SELECTOR_VM)
    }

    companion object{
        private var expectAt:Int=0
        private val imageList:ArrayList<IItemImageSelectList> by lazy { ArrayList() }
    }
}