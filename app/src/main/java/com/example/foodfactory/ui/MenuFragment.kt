package com.example.foodfactory.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfactory.adapter.MenuAdapter
import com.example.foodfactory.databinding.FragmentMenuBinding
import com.example.foodfactory.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MenuFragment : Fragment() {

    private val TAG: String = "MEH"
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var menuArraylist: ArrayList<Category>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        menuArraylist = arrayListOf()
        menuAdapter = MenuAdapter(this, menuArraylist)
        binding.recyclerView.adapter = menuAdapter
        load_menu()
        binding.button.setOnClickListener {
            val dir = MenuFragmentDirections.actionNavMenuToOrderFragment()
            view.findNavController().navigate(dir)
        }
        val prefs = Prefs(requireContext())
        val size = prefs.viewOrders()?.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun load_menu() {
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Menu-Collection")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                menuArraylist.clear()
                for (doc in document) {
                    menuArraylist.add(doc.toObject<Category>())
                }
                menuAdapter.notifyDataSetChanged()
                binding.pbMenu.visibility = View.GONE
            } else {
                Log.d(TAG, "No such document")
                binding.pbMenu.visibility = View.GONE
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with", exception)
            binding.pbMenu.visibility = View.GONE
        }
    }
}