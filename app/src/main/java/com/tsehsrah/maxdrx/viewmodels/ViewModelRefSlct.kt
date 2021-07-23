package com.tsehsrah.maxdrx.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tsehsrah.maxdrx.models.IRefVal
import com.tsehsrah.maxdrx.models.RefVal

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelRefSlct @Inject constructor(application: Application): AndroidViewModel(application) {
    private var bmp= createBitmap(100,30, Bitmap.Config.ARGB_8888)
    private val refPrvw: MutableLiveData<Bitmap> by lazy{
        MutableLiveData<Bitmap>()
    }
    val opRefPrvw: LiveData<Bitmap>
        get()=refPrvw

    private val ref: IRefVal = RefVal()
    fun updtRef(r:Int=-1,g:Int=-1,b:Int=-1){
        if(r!=-1) ref.stR(r)
        if(g!=-1) ref.stG(g)
        if(b!=-1) ref.stB(b)
        viewModelScope.launch(Dispatchers.Default)  {
            bmp.eraseColor(Color.argb(255,ref.gtR(),ref.gtG(),ref.gtB()))
            refPrvw.postValue(bmp)
        }
    }
    fun getRefVals(): IRefVal {
        return ref
    }
}