package com.example.gestioncontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.gestioncontact.prefs.PreferencesManager

class DetailContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()

    private lateinit var ivDetailPhoto: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var etDetailNom: EditText
    private lateinit var etDetailPrenom: EditText
    private lateinit var etDetailTelephone: EditText
    private lateinit var etDetailEmail: EditText
    private lateinit var etDetailLatitude: EditText
    private lateinit var etDetailLongitude: EditText
    private lateinit var btnViewOnMap: Button
    private lateinit var btnCall: Button
    private lateinit var btnEmail: Button
    private lateinit var btnDelete: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var currentPhotoUri: String? = null
    private var currentContactId: Long = -1L

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it.toString()
            ivDetailPhoto.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            ivDetailPhoto.setImageBitmap(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_contact, container, false)

        ivDetailPhoto = view.findViewById(R.id.ivDetailPhoto)
        btnGallery = view.findViewById(R.id.btnGallery)
        btnCamera = view.findViewById(R.id.btnCamera)
        etDetailNom = view.findViewById(R.id.etDetailNom)
        etDetailPrenom = view.findViewById(R.id.etDetailPrenom)
        etDetailTelephone = view.findViewById(R.id.etDetailTelephone)
        etDetailEmail = view.findViewById(R.id.etDetailEmail)
        etDetailLatitude = view.findViewById(R.id.etDetailLatitude)
        etDetailLongitude = view.findViewById(R.id.etDetailLongitude)
        btnViewOnMap = view.findViewById(R.id.btnViewOnMap)
        btnCall = view.findViewById(R.id.btnCall)
        btnEmail = view.findViewById(R.id.btnEmail)
        btnDelete = view.findViewById(R.id.btnDelete)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        val prefsManager = PreferencesManager(requireContext())
        val textSize = prefsManager.getTextSizeSp()
        val textColor = prefsManager.getTextColor()

        etDetailNom.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        etDetailNom.setTextColor(textColor)
        etDetailPrenom.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        etDetailPrenom.setTextColor(textColor)
        etDetailTelephone.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        etDetailEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        currentContactId = viewModel.selectedContactId
        if (currentContactId != -1L) {
            btnDelete.visibility = View.VISIBLE
            val contact = viewModel.contacts.value?.find { it.id == currentContactId }
            contact?.let { c ->
                etDetailNom.setText(c.nom)
                etDetailPrenom.setText(c.prenom)
                etDetailTelephone.setText(c.telephone)
                etDetailEmail.setText(c.email ?: "")
                c.latitude?.let { etDetailLatitude.setText(it.toString()) }
                c.longitude?.let { etDetailLongitude.setText(it.toString()) }
                currentPhotoUri = c.photoUri

                if (!currentPhotoUri.isNullOrEmpty()) {
                    try {
                        ivDetailPhoto.setImageURI(Uri.parse(currentPhotoUri))
                    } catch (e: Exception) {
                        ivDetailPhoto.setImageResource(R.mipmap.ic_launcher)
                    }
                }
            }
        } else {
            btnDelete.visibility = View.GONE
        }

        btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Caméra non disponible", Toast.LENGTH_SHORT).show()
            }
        }

        btnViewOnMap.setOnClickListener {
            val latStr = etDetailLatitude.text.toString().trim()
            val lonStr = etDetailLongitude.text.toString().trim()
            val lat = latStr.toDoubleOrNull()
            val lon = lonStr.toDoubleOrNull()

            if (lat == null || lon == null || lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                Toast.makeText(requireContext(), R.string.msg_invalid_coords, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentContactId != -1L) {
                updateContactInMemory()
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CarteContactFragment())
                .addToBackStack(null)
                .commit()
        }

        btnCall.setOnClickListener {
            val phone = etDetailTelephone.text.toString().trim()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Aucune application d'appel trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Numéro de téléphone vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val email = etDetailEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Aucune application e-mail trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Adresse e-mail vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Supprimer le contact")
                .setMessage("Voulez-vous vraiment supprimer ce contact ?")
                .setPositiveButton("Oui") { _, _ ->
                    viewModel.deleteContact(currentContactId) {
                        parentFragmentManager.popBackStack()
                    }
                }
                .setNegativeButton("Non", null)
                .show()
        }

        btnSave.setOnClickListener {
            val nom = etDetailNom.text.toString().trim()
            val prenom = etDetailPrenom.text.toString().trim()
            val telephone = etDetailTelephone.text.toString().trim()
            val email = etDetailEmail.text.toString().trim().ifEmpty { null }
            val latStr = etDetailLatitude.text.toString().trim()
            val lonStr = etDetailLongitude.text.toString().trim()

            if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(requireContext(), R.string.msg_champs_obligatoires, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val latitude = latStr.toDoubleOrNull()
            val longitude = lonStr.toDoubleOrNull()

            if ((latStr.isNotEmpty() && latitude == null) || (lonStr.isNotEmpty() && longitude == null)) {
                Toast.makeText(requireContext(), R.string.msg_invalid_coords, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (latitude != null && (latitude < -90 || latitude > 90)) {
                etDetailLatitude.error = getString(R.string.msg_invalid_coords)
                return@setOnClickListener
            }

            if (longitude != null && (longitude < -180 || longitude > 180)) {
                etDetailLongitude.error = getString(R.string.msg_invalid_coords)
                return@setOnClickListener
            }

            val contact = Contact(
                id = currentContactId,
                nom = nom,
                prenom = prenom,
                telephone = telephone,
                email = email,
                photoUri = currentPhotoUri,
                latitude = latitude,
                longitude = longitude
            )

            if (currentContactId == -1L) {
                viewModel.insertContact(contact) {
                    parentFragmentManager.popBackStack()
                }
            } else {
                viewModel.updateContact(contact) {
                    parentFragmentManager.popBackStack()
                }
            }
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun updateContactInMemory() {
        val contact = viewModel.contacts.value?.find { it.id == currentContactId }
        contact?.apply {
            nom = etDetailNom.text.toString().trim()
            prenom = etDetailPrenom.text.toString().trim()
            telephone = etDetailTelephone.text.toString().trim()
            email = etDetailEmail.text.toString().trim().ifEmpty { null }
            photoUri = currentPhotoUri
            latitude = etDetailLatitude.text.toString().trim().toDoubleOrNull()
            longitude = etDetailLongitude.text.toString().trim().toDoubleOrNull()
        }
    }
}
