package com.example.gestioncontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_carte_contact, container, false)

        val id = viewModel.selectedContactId
        val contact = viewModel.contacts.value?.find { it.id == id }

        if (contact != null) {
            val lat = contact.latitude
            val lon = contact.longitude

            if (lat != null && lon != null) {
                val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
                mapFragment?.getMapAsync { googleMap ->
                    val location = LatLng(lat, lon)
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title("${contact.nom} ${contact.prenom}")
                    )
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            } else {
                Toast.makeText(requireContext(), R.string.msg_no_coords, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        } else {
            Toast.makeText(requireContext(), R.string.msg_no_coords, Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
