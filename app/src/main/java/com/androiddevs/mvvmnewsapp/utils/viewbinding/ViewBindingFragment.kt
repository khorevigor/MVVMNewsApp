package com.androiddevs.mvvmnewsapp.utils.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

open class ViewBindingFragment<VB: ViewBinding>(
    private val inflate: Inflate<VB>
): Fragment() {
    private var _binding: VB? = null
    val binding get() = _binding ?: throw IllegalStateException("Binding is not inflated")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
