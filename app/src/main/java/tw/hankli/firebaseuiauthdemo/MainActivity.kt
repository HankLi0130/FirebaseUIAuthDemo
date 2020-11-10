package tw.hankli.firebaseuiauthdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        FirebaseAuth.getInstance().currentUser?.let {
            onUserSignedIn(it)
        } ?: onUserSignedOut()
    }

    private fun onUserSignedIn(user: FirebaseUser) {
        view_label.text = user.displayName
        view_btn.setText(R.string.sign_out)
        view_btn.setOnClickListener { signOut() }
    }

    private fun signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { onUserSignedOut() }
    }

    private fun onUserSignedOut() {
        view_label.text = null
        view_btn.setText(R.string.sign_in)
        view_btn.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val idpConfigs = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.FacebookBuilder().build()
        )

        val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(idpConfigs)
                .build()

        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                onUserSignedIn(FirebaseAuth.getInstance().currentUser!!)
            } else {
                val response = IdpResponse.fromResultIntent(data)
                response?.error?.printStackTrace()

                onUserSignedOut()
            }
        }
    }
}