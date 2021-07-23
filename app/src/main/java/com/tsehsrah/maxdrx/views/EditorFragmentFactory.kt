package com.tsehsrah.maxdrx.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class EditorFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            HeadsUpFragment::class.java.name->{
                HeadsUpFragment()
            }ImagePreviewFragment::class.java.name->{
                ImagePreviewFragment()
            }ImageSelectFragment::class.java.name->{
                ImageSelectFragment()
            }ToolsFragment::class.java.name->{
                ToolsFragment()
            }else->{
                super.instantiate(classLoader, className)
            }
        }
    }
}
