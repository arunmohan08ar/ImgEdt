package com.tsehsrah.imageops.imageOperations.models

interface IOperationManager {
    fun loadSecondaryBmp()
    fun loadReferenceBmp()
    fun exceptionNotifier(e:Exception?=null,vme:VirtualMachineError?=null,s:String?=null)
}