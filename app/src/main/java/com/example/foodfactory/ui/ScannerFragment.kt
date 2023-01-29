package com.example.foodfactory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodfactory.databinding.FragmentScannerBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class ScannerFragment : Fragment() {
    private val TAG: String = "Scann"
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())

        val direction=ScannerFragmentDirections.actionScannerToNavMenu()
        findNavController().navigate(direction)
        val barcodeLauncher =
            registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Scanned: " + result.contents,
                        Toast.LENGTH_LONG
                    ).show()
                    if (prefs.getTable().length > 0) {
                        prefs.deleteTable()
                    }
                    prefs.setTable(result.contents)
                    val dir = ScannerFragmentDirections.actionScannerToNavMenu()
                    findNavController().navigate(dir)
                }
            }
        binding.btnScan.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            options.setPrompt("Find your table's QR image")
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(false)
            barcodeLauncher.launch(options)
        }
    }


}
