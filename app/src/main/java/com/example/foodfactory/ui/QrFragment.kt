package com.example.foodfactory.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodfactory.databinding.FragmentQrBinding
import com.example.foodfactory.model.Bill
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder


class QrFragment : Fragment() {
    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!
    private lateinit var edata: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQrBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth
        db = Firebase.firestore
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = QrFragmentArgs.fromBundle(requireArguments())
//        Snackbar.make(binding.root,args.bill.orderId, Snackbar.LENGTH_INDEFINITE).show()
        val orderId:String = args.bill.orderId.split("/").last()
        db.collection("payments").document(orderId)
            .get()
            .addOnSuccessListener { task->
                val bill = task.toObject<Bill>()
                val msg = "Order ID: ${bill!!.orderId} \n User ID: ${bill.uid}\n Bill Amount: Rs.${bill.amt}\n Approved: ${bill.approved}\n Payment Method: ${bill.payMethod}\n Payment Status: ${bill.paymentStatus}\n Date: ${bill.date} "
                val multiFormatWriter = MultiFormatWriter()
                try {
                    val bitMatrix = multiFormatWriter.encode(msg, BarcodeFormat.QR_CODE, 200, 200)
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    binding.ivQRCode.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            }
            .addOnFailureListener { ex->

            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
