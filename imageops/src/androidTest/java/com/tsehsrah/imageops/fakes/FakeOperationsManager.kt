package com.tsehsrah.imageops.fakes

import com.tsehsrah.imageops.imageOperations.models.IOperationManager

object FakeOperationsManager:IOperationManager {
    override fun loadSecondaryBmp() {
    }

    override fun loadReferenceBmp() {
    }

    override fun exceptionNotifier(e: Exception?, vme: VirtualMachineError?, s: String?) {
    }
}