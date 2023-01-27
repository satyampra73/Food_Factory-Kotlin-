package com.example.foodfactory.ui

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
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfactory.adapter.CloudOrderAdapter
import com.example.foodfactory.adapter.OrderAdapter
import com.example.foodfactory.databinding.FragmentOrderBinding
import com.example.foodfactory.model.OrderView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderFragment : Fragment(), OrderClickListener {

    private val TAG: String = "MEHHH"
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: Prefs
    private lateinit var cloudOrderAdapter: CloudOrderAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var viewOrders: ArrayList<OrderView>
    private lateinit var prepOrderArraylist: ArrayList<OrderView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth
        db = Firebase.firestore
        prefs = Prefs(requireContext())
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun load_orders() {
        val uid = auth.currentUser!!.uid
        val dateTime = LocalDateTime.now()
        val datestr = dateTime.format(DateTimeFormatter.ofPattern("MdyH"))
        viewOrders.clear()
        prefs.viewOrders()?.forEach {
            val order = it.value.toString().split(",")
            if (order.size == 5) {
                viewOrders.add(
                    OrderView(
                        order[0],
                        order[2].toInt(),
                        order[1].toInt(),
                        order[4],
                        order[3].toBoolean(),
                        prefs.getTable(),
                        uid,
                        datestr,
                        )
                )
            } else {
                if (order.size > 3) {
                    viewOrders.add(
                        OrderView(
                            order[0],
                            order[2].toInt(),
                            order[1].toInt(),
                            "",
                            false,
                            prefs.getTable(),
                            uid,
                            datestr,
                        )
                    )
                }
            }
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        val adapter: OrderAdapter? = viewOrders?.let { OrderAdapter(viewOrders, this) }
        binding.recyclerViewCart.adapter = adapter
        binding.pbSubMenu.visibility = View.GONE

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        prepOrderArraylist = arrayListOf()
        viewOrders = arrayListOf<OrderView>()
        binding.recyclerViewPrepCart.layoutManager = LinearLayoutManager(requireContext())

        load_orders()
        load_preparing_orders()
        binding.btnCheckout.setOnClickListener {
            val dir = OrderFragmentDirections.actionOrderFragmentToPaymentFragment()
            //clear the prefs
            findNavController().navigate(dir)
        }

        binding.btnOrderNow.setOnClickListener {
            viewOrders.forEach { order ->
                auth.currentUser?.let { it1 ->
                    val datestr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MdyH"))
                    db.collection("orders")
                        .document("${auth.uid}:${datestr}:${order.name}")
                        .set(order)
                        .addOnSuccessListener {
                            prefs.setOrderId(order.orderId)
                        }
                }
                prefs.deleteOrder(order.name)

            }
            Toast.makeText(requireContext(), "Order Updated", Toast.LENGTH_LONG)
                .show()
            viewOrders.clear()
            binding.recyclerViewCart.adapter?.notifyDataSetChanged()

            load_preparing_orders()
            binding.recyclerViewPrepCart.adapter?.notifyDataSetChanged()
        }
        binding.refreshOrder.setOnRefreshListener {
            load_orders()
            binding.refreshOrder.isRefreshing = false
        }
        binding.refreshPreps.setOnRefreshListener {
            load_preparing_orders()
            binding.refreshPreps.isRefreshing = false
        }
    }

    private fun load_preparing_orders() {
        db = FirebaseFirestore.getInstance()
        if (prefs.getOrderId().length != 0) {

            db.collection("orders").get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        prepOrderArraylist.clear()

                        for (doc in document) {
                            prepOrderArraylist.add(doc.toObject<OrderView>())
                        }
                        cloudOrderAdapter =
                            prepOrderArraylist.let { CloudOrderAdapter(prepOrderArraylist, this) }
                        binding.recyclerViewPrepCart.adapter = cloudOrderAdapter
                        binding.pbSubMenuPrep.visibility = View.GONE
                    } else {
                        Log.d(TAG, "No such document")
                        binding.pbSubMenuPrep.visibility = View.GONE
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with", exception)
                    binding.pbSubMenuPrep.visibility = View.GONE
                }
        } else {
            Log.d(TAG, "Order is empty!")
            binding.pbSubMenuPrep.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCellClickListener(view: View, holder: RecyclerView.ViewHolder) {
        val orderView = view.tag as OrderView
        prefs.deleteOrder(orderView.name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            load_orders()
        }
    }

}