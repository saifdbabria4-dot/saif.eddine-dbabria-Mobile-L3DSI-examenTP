package com.example.gestioncontact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var ivDetailPhoto: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var etDetailNom: EditText
    private lateinit var etDetailPrenom: EditText
    private lateinit var etDetailTelephone: EditText
    private lateinit var etDetailEmail: EditText
    private lateinit var btnCall: Button
    private lateinit var btnEmail: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var contact: Contact? = null
    private var contactIndex: Int = -1

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contact?.photoUri = it.toString()
            ivDetailPhoto.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            ivDetailPhoto.setImageBitmap(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        ivDetailPhoto = findViewById(R.id.ivDetailPhoto)
        btnGallery = findViewById(R.id.btnGallery)
        btnCamera = findViewById(R.id.btnCamera)
        etDetailNom = findViewById(R.id.etDetailNom)
        etDetailPrenom = findViewById(R.id.etDetailPrenom)
        etDetailTelephone = findViewById(R.id.etDetailTelephone)
        etDetailEmail = findViewById(R.id.etDetailEmail)
        btnCall = findViewById(R.id.btnCall)
        btnEmail = findViewById(R.id.btnEmail)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        contact = intent.getSerializableExtra("CONTACT") as? Contact
        contactIndex = intent.getIntExtra("CONTACT_INDEX", -1)

        contact?.let { c ->
            etDetailNom.setText(c.nom)
            etDetailPrenom.setText(c.prenom)
            etDetailTelephone.setText(c.telephone)
            etDetailEmail.setText(c.email)
            if (!c.photoUri.isNullOrEmpty()) {
                try {
                    ivDetailPhoto.setImageURI(Uri.parse(c.photoUri))
                } catch (e: Exception) {
                    ivDetailPhoto.setImageResource(R.mipmap.ic_launcher)
                }
            }
        }

        btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(this, "Caméra non disponible", Toast.LENGTH_SHORT).show()
            }
        }

        btnCall.setOnClickListener {
            val phone = etDetailTelephone.text.toString().trim()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Aucune application d'appel trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Numéro de téléphone vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val email = etDetailEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Aucune application e-mail trouvée", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Adresse e-mail vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val nom = etDetailNom.text.toString().trim()
            val prenom = etDetailPrenom.text.toString().trim()
            val telephone = etDetailTelephone.text.toString().trim()
            val email = etDetailEmail.text.toString().trim()

            if (nom.isEmpty() || prenom.isEmpty()) {
                Toast.makeText(this, R.string.msg_champs_obligatoires, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            contact?.apply {
                this.nom = nom
                this.prenom = prenom
                this.telephone = telephone
                this.email = email
            }

            val resultIntent = Intent().apply {
                putExtra("CONTACT", contact)
                putExtra("CONTACT_INDEX", contactIndex)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
