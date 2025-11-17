package com.example.equipoOcho.view.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.equipoOcho.R
import com.example.equipoOcho.databinding.FragmentHomeInventoryBinding
import com.example.equipoOcho.view.adapter.InventoryAdapter
import com.example.equipoOcho.viewmodel.InventoryViewModel
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.widget.Toast
import com.example.equipoOcho.view.widget.InventoryWidgetProvider


class HomeInventoryFragment : Fragment() {
    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeInventoryBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()
        observadorViewModel()

    }

    private fun controladores() {
        binding.fbagregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

    }

    private fun observadorViewModel(){
        observerListInventory()
        observerProgress()
    }

    private fun observerListInventory(){

        inventoryViewModel.getListInventory()
        inventoryViewModel.listInventory.observe(viewLifecycleOwner){ listInventory ->
            val recycler = binding.recyclerview
            val layoutManager =LinearLayoutManager(context)
            recycler.layoutManager = layoutManager
            val adapter = InventoryAdapter(listInventory, findNavController())
            recycler.adapter = adapter
            adapter.notifyDataSetChanged()

        }

    }
    private fun observerProgress(){
        inventoryViewModel.progresState.observe(viewLifecycleOwner){status ->
            binding.progress.isVisible = status
        }
    }

    private fun requestPinWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(requireContext())
            val myProvider = ComponentName(
                requireContext(),
                InventoryWidgetProvider::class.java
            )

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                appWidgetManager.requestPinAppWidget(myProvider, null, null)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Agrega el widget desde la pantalla de inicio.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "En esta versión de Android agrega el widget desde la pantalla de inicio.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


}