package com.example.foodfactory.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfactory.R
import com.example.foodfactory.adapter.BillOrderAdapter
import com.example.foodfactory.databinding.FragmentPaymentBinding
import com.example.foodfactory.model.Bill
import com.example.foodfactory.model.OrderView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime


class PaymentFragment : Fragment() {
    private val TAG: String = "BILLL"
    internal val UPI_PAYMENT = 0
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private var selected_payment_method = "Card"
    private lateinit var prefs: Prefs
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var billOrderArraylist: ArrayList<OrderView>
    private lateinit var billOrderAdapter: BillOrderAdapter
    private var amt = 0.00
    private var cgst = 0.00
    private var sgst = 0.00


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth
        db = Firebase.firestore
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billOrderArraylist = arrayListOf()
        prefs = Prefs(requireContext())
        binding.recyclerViewBill.layoutManager = LinearLayoutManager(requireContext())
        load_bills()
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioCard -> selected_payment_method = "Card"
                R.id.radioCash -> selected_payment_method = "Cash"
                R.id.radioUPI -> selected_payment_method = "UPI"
            }
        }

        binding.btnPayment.setOnClickListener {
            val bill = Bill(
                date = LocalDateTime.now().toString(),
                paymentStatus = true,
                amt = amt,
                prefs.getOrderId(),
                auth.currentUser!!.uid,
                payMethod = selected_payment_method,
            )

            when (selected_payment_method) {
                "Card" -> {
                    Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_LONG).show()
                }
                "Cash" -> {
                    updatePaymentStatus(selected_payment_method)

                }
                "UPI" -> {
//                    payUsingUpi(amt.toString())
                    makeOrderintoHistory(prefs.getOrderId(), auth.currentUser!!.uid, bill)
                }
            }
        }
    }

    private fun load_bills() {
        db = FirebaseFirestore.getInstance()
        if (prefs.getOrderId().length != 0) {
            val docRef =
                auth.currentUser?.let { db.collection("orders").get() }
            docRef?.addOnSuccessListener { document ->
                if (document != null) {
                    billOrderArraylist.clear()
                    for (doc in document) {
                        val element = doc.toObject<OrderView>()
                        billOrderArraylist.add(element)
                        amt += (element.price.toDouble()) * (element.qty)
                    }
                    billOrderAdapter =
                        billOrderArraylist.let { BillOrderAdapter(billOrderArraylist) }
                    binding.recyclerViewBill.adapter = billOrderAdapter
                    binding.pbBill.visibility = View.GONE

                    cgst = 0.025 * amt
                    sgst = 0.025 * amt

                    binding.textcgstAmt.text = cgst.toString()
                    binding.textsgstAmt.text = sgst.toString()
                    amt += cgst + sgst
                    binding.textAmount.text = amt.toString()

                } else {
                    Log.d(TAG, "No such document")
                }
            }?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with", exception)
            }
        } else {
            Log.d(TAG, "Order is empty!")
        }

    }

    fun payUsingUpi(
        amount: String,
        upiId: String = "9454760187@paytm",
        name: String = "Food Factory",
        note: String = "Payment"
    ) {

        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("mc", "your-merchant-code")
            .appendQueryParameter("tr", "your-transaction-ref-id")
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()


        val upiPayIntent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.data = uri

        // will always show a dialog to user to choose an app
        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

        // check if intent resolves
        if (null != chooser.resolveActivity(requireActivity().packageManager)) {
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(
                context,
                "No UPI app found, please install one to continue",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt != null) {
                        dataList.add(trxt)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d(
                    "UPI",
                    "onActivityResult: " + "Return data is null"
                ) //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {

        var str: String? = data[0]
        Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
        var paymentCancel = ""
        if (str == null) str = "discard"
        var status = ""
        var approvalRefNo = ""
        val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in response.indices) {
            val equalStr =
                response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (equalStr.size >= 2) {
                if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                    status = equalStr[1].toLowerCase()
                } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                    approvalRefNo = equalStr[1]
                }
            } else {
                paymentCancel = "Payment cancelled by user."
            }
        }

        if (status == "success") {
            //Code to handle successful transaction here.
            Toast.makeText(context, "Transaction successful.", Toast.LENGTH_SHORT).show()
            Log.d("UPI", "responseStr: $approvalRefNo")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updatePaymentStatus(ptype = "upi")
            }
        } else if ("Payment cancelled by user." == paymentCancel) {
            Toast.makeText(context, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
            updatePaymentStatus(ptype = "cancel")
        } else {
            Toast.makeText(context, "Transaction failed.Please try again", Toast.LENGTH_SHORT)
                .show()
            updatePaymentStatus(ptype = "upi")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePaymentStatus(ptype: String) {

        when (ptype) {
            "upi" -> {
                val bill = Bill(
                    date = LocalDateTime.now().toString(),
                    paymentStatus = true,
                    amt = amt,
                    prefs.getOrderId(),
                    auth.currentUser!!.uid,
                    payMethod = ptype,
                )
                bill.approved = "yes"
                db.collection("payments")
                    .document(auth.currentUser!!.uid)
                    .set(bill)
                    .addOnFailureListener {
                        Snackbar.make(
                            binding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                    .addOnSuccessListener {
                        makeOrderintoHistory(prefs.getOrderId(), auth.currentUser!!.uid, bill)
                    }
            }
            "Cash" -> {
                val bill = Bill(
                    date = LocalDateTime.now().toString(),
                    paymentStatus = true,
                    amt = amt,
                    prefs.getOrderId(),
                    auth.currentUser!!.uid,
                    payMethod = ptype,
                )
                bill.approved = "no"
                db.collection("payments")
                    .document(prefs.getOrderId())
                    .set(bill)
                    .addOnFailureListener {
                        Snackbar.make(
                            binding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                    .addOnSuccessListener {
                        makeOrderintoHistory(prefs.getOrderId(), auth.currentUser!!.uid, bill)
                    }
            }
            "card" -> {

            }
            "cancel" -> {

            }
            "failed" -> {

            }
        }
    }

    private fun makeOrderintoHistory(orderId: String, uid: String, bill: Bill) {

        val hist = db.collection("history").document(uid).collection(orderId)
        val document = db.collection("orders")
        document.get().addOnSuccessListener { snapShots ->
            if (snapShots.size() > 0) {
                val oldOrders = snapShots.toObjects<OrderView>()
                oldOrders.forEach {
                    hist.add(it).addOnSuccessListener { a ->

                    }.addOnFailureListener {
                        Snackbar.make(
                            binding.root,
                            it.message.toString(),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
                snapShots.documents.forEach {
                    db.collection("orders").document(it.id).delete()
                }
                if (bill.approved == "no") {
                    AlertDialog.Builder(requireContext()).setTitle("Pay Cash as Counter")
                        .setMessage("Please pay Rs. ${amt} at the counter, and verify it by the manager. Failure to do so will result in punishment by law")
                        .setPositiveButton(
                            "Dont click until paid",
                            DialogInterface.OnClickListener { dialog, which ->
                                val dir =
                                    PaymentFragmentDirections.actionPaymentFragmentToQrFragment(bill = bill)
                                findNavController().navigate(dir)
                            })
                        .setCancelable(false).create().show()

                }

            } else {
                Snackbar.make(
                    binding.root,
                    "no orders",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }.addOnFailureListener {
            Snackbar.make(
                binding.root,
                it.message.toString(),
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }

    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected
                && netInfo.isConnectedOrConnecting
                && netInfo.isAvailable
            ) {
                return true
            }
            return false
        }
    }
}