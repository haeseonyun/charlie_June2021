package de.htwberlin.learningcompanion.userinterface.fragment.places.details


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.setActivityTitle
import kotlinx.android.synthetic.main.fragment_my_place.*
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.IOException
import java.util.*


class MyPlaceFragment : Fragment() {

    private val args: MyPlaceFragmentArgs by navArgs()


    private val viewModelFactory = ViewModelFactory
    private lateinit var placeViewModel: PlaceViewModel

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val RC_LOCATION_ACTIVITY = 9000
        const val RC_PICK_IMAGE = 9001
    }

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private var imageUri: Uri? = null

    private var editMode = false
    private var place: Place? = null

    var takenPhotoPath: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)

        addAddressClickListener()
        addSaveButtonClickListener()

        tv_tapforImage.setOnClickListener {
            callPickImageIntent()
        }

        iv_image_preview.setOnClickListener {
            callPickImageIntent()
        }

        checkForEditablePlace()
    }

    private fun checkForEditablePlace() {
        if (args.ID != -1L) {
            val id = args.ID
            context?.let {
                place = placeViewModel.repository.getPlaceByID(id)
            }
            if (place != null) {
                initLayoutWithPlace(place!!)
            }
            editMode = true
        } else {
            setActivityTitle("New Place")
            editMode = false
        }
    }

    private fun initLayoutWithPlace(place: Place) {
        et_name.setText(place.name)
        tv_address.text = place.addressString
        imageUri = place.imageUri
        if (imageUri != null) {
            tv_tapforImage.visibility = View.GONE
            imageViewAdd.visibility = View.GONE
        }

        Glide.with(context!!).load(imageUri).fitCenter().into(iv_image_preview)
    }

    private fun addSaveButtonClickListener() {
        btn_save.setOnClickListener {
            val nameString = et_name.text.toString()
            val addressString = tv_address.text.toString()
            val timestamp = Calendar.getInstance().timeInMillis

            if (nameString.isNotEmpty()) {
                if (editMode && place != null) {
                    val updatedPlace = Place(null, imageUri, nameString, latitude, longitude, addressString, timestamp)
                    updatedPlace.id = place?.id ?: 0
                    updatePlace(updatedPlace)
                    toast("Place updated")
                } else {
                    val place = Place(null, imageUri, nameString, latitude, longitude, addressString, timestamp)
                    savePlace(place)
                    toast("Place saved to Database")
                }
                navigateToPlaceOverview()
            } else {
                tintTextInputLayout( true)
            }
        }
    }

    private fun tintTextInputLayout(errorTint: Boolean) {
        layoutName.error = "Please insert name for your place"
        layoutName.isErrorEnabled = errorTint
    }

    private fun updatePlace(place: Place) {
        placeViewModel.repository.updatePlace(place) {
            placeViewModel.currentPlace.value = it
        }
    }

    private fun savePlace(place: Place) {
        placeViewModel.repository.insertPlace(place) {
            placeViewModel.currentPlace.value = it
        }
    }

    private fun navigateToPlaceOverview() {
        findNavController().navigate(R.id.action_global_mainScreenFragment)
    }

    private fun callPickImageIntent() {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT // this is freaking important, else the uri will not be persistable
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE)

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            val chooserIntent = Intent.createChooser(pickIntent, "Select Image from")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(dispatchTakePictureIntent()))

            startActivityForResult(chooserIntent, RC_PICK_IMAGE)
    }

    private fun addAddressClickListener() {
        btn_set_address.setOnClickListener {
            startGetLocationActivity()
        }
    }

    private fun startGetLocationActivity() {
        val intent = Intent(context, GetLocationActivity::class.java)
        startActivityForResult(intent, RC_LOCATION_ACTIVITY)
    }

    private fun dispatchTakePictureIntent() : Intent{
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            context!!,
                            "de.htwberlin.learningcompanion.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // CrtimeStameate an image file name
        val timeStamp = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance())
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            takenPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == RC_LOCATION_ACTIVITY && resultCode == Activity.RESULT_OK -> {
                    val addressDisplayName = data?.extras?.getString(LOCATION_DISPLAYNAME_EXTRA)
                    tv_address.text = addressDisplayName

                    latitude = data?.extras?.getDouble(LOCATION_LATITUDE_EXTRA) ?: 0.0
                    longitude = data?.extras?.getDouble(LOCATION_LONGITUDE_EXTRA) ?: 0.0
            }
            requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK-> {
                if (data != null && data.data != null) {
                    val uri = data.data
                    imageUri = uri

//                    Picasso.get().load(uri).fit().centerInside().into(iv_image_preview)
                    Glide.with(context!!).load(uri).fitCenter().into(iv_image_preview)

                    tv_tapforImage.visibility = View.GONE
                    imageViewAdd.visibility = View.GONE
                } else if (takenPhotoPath != null){
                    val f = File(takenPhotoPath)
                    imageUri = Uri.fromFile(f)

//                    Picasso.get().load(imageUri).fit().centerInside().into(iv_image_preview)
                    Glide.with(context!!).load(imageUri).fitCenter().into(iv_image_preview)

                    tv_tapforImage.visibility = View.GONE
                    imageViewAdd.visibility = View.GONE
                }
            }
        }
    }
}
