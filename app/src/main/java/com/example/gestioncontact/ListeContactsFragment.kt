package com.example.gestioncontact

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioncontact.prefs.PreferencesManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class ListeContactsFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var etSearch: EditText
    private lateinit var btnSettings: Button
    private lateinit var rvContacts: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: ContactAdapter

    private var allContacts = listOf<Contact>()
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_liste_contacts, container, false)

        etSearch = view.findViewById(R.id.etSearch)
        btnSettings = view.findViewById(R.id.btnSettings)
        rvContacts = view.findViewById(R.id.rvContacts)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        fabAdd = view.findViewById(R.id.fabAdd)

        prefsManager = PreferencesManager(requireContext())

        rvContacts.layoutManager = LinearLayoutManager(requireContext())

        adapter = ContactAdapter(
            emptyList(),
            prefsManager.getTextSizeSp(),
            prefsManager.getTextColor(),
            prefsManager.isCallFromListEnabled()
        ) { contactId ->
            viewModel.selectedContactId = contactId
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DetailContactFragment())
                .addToBackStack(null)
                .commit()
        }

        rvContacts.adapter = adapter

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            allContacts = contacts
            filterContacts(etSearch.text.toString())
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fabAdd.setOnClickListener {
            viewModel.selectedContactId = -1L
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DetailContactFragment())
                .addToBackStack(null)
                .commit()
        }

        btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ParametresFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadContacts()
    }

    private fun filterContacts(query: String) {
        val trimmed = query.trim().lowercase(Locale.ROOT)
        val filtered = if (trimmed.isEmpty()) {
            allContacts
        } else {
            allContacts.filter {
                it.nom.lowercase(Locale.ROOT).contains(trimmed) ||
                        it.prenom.lowercase(Locale.ROOT).contains(trimmed) ||
                        it.telephone.contains(trimmed)
            }
        }

        adapter = ContactAdapter(
            filtered,
            prefsManager.getTextSizeSp(),
            prefsManager.getTextColor(),
            prefsManager.isCallFromListEnabled()
        ) { contactId ->
            viewModel.selectedContactId = contactId
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DetailContactFragment())
                .addToBackStack(null)
                .commit()
        }
        rvContacts.adapter = adapter

        if (filtered.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvContacts.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvContacts.visibility = View.VISIBLE
        }
    }
}
