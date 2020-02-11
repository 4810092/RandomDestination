package com.gka.codesample.randomdestination.ui

import android.content.DialogInterface
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gka.codesample.randomdestination.R

open class BaseActivity : AppCompatActivity() {
    private var progressDialog: AlertDialog? = null

    protected fun showProgress() {
        if (progressDialog == null)
            progressDialog =
                AlertDialog.Builder(this)
                    .setTitle(R.string.loading_title)
                    .setCancelable(false)
                    .setView(
                        ProgressBar(this)
                            .apply { setPadding(50, 50, 50, 50) })
                    .create()

        progressDialog?.show()
    }

    protected fun hideProgress() {
        progressDialog?.dismiss()
    }

    protected fun showMessage(
        messageId: Int,
        positiveListener: (dialogInterface: DialogInterface, i: Int) -> Unit
    ) = AlertDialog.Builder(this)
        .setTitle(R.string.message_title)
        .setMessage(messageId)
        .setPositiveButton(android.R.string.ok, positiveListener)
        .setNegativeButton(android.R.string.no) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        .create()
        .show()


    protected fun showToast(stringId: Int) =
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()

}