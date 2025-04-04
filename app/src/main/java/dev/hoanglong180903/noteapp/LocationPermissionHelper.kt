package dev.hoanglong180903.noteapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import com.mapbox.common.location.compat.permissions.PermissionsListener
import com.mapbox.common.location.compat.permissions.PermissionsManager
import java.lang.ref.WeakReference

class LocationPermissionHelper(val activity: WeakReference<Activity>) {
    private lateinit var permissionsManager: PermissionsManager

    fun checkPermissions(onMapReady: () -> Unit) {
        if (PermissionsManager.areLocationPermissionsGranted(activity.get())) {
            onMapReady()
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: List<String>) {
                    Toast.makeText(
                        activity.get(), "You need to accept location permissions.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        onMapReady()
                    } else {
                        activity.get()?.finish()
                    }
                }
            })
            permissionsManager.requestLocationPermissions(activity.get())
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //fragment
//    fun requestLocationPermission () {
//        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
//            AlertDialog.Builder(requireActivity())
//                .setTitle("Permission needed")
//                .setMessage("This permission is needed to enter the aplication")
//                .setPositiveButton(R.string.accept_location_auth) { dialog, which ->
//                    ActivityCompat.requestPermissions(requireActivity(), arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
//
//                }
//                .setNegativeButton(R.string.no_location_auth) { dialog, which ->
//                    dialog.dismiss()
//                }
//                .setCancelable(false)
//                .create().show();
//        }else{
//            requestPermissions(arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION);
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if(requestCode == LOCATION_PERMISSION){
//            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                recreate(requireActivity())
//                Toast.makeText(requireActivity(), "Permission GRANTED. Redirecting...", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(requireActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}