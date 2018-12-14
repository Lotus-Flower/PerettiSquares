package squares.cherry_blossom.com.perettisquares.Menu

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.menu_activity.*
import squares.cherry_blossom.com.perettisquares.Game.GameActivity
import squares.cherry_blossom.com.perettisquares.R
import com.google.android.gms.common.ConnectionResult
import android.support.annotation.NonNull
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import squares.cherry_blossom.com.perettisquares.HighScores.HighScoreActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Player
import com.google.android.gms.games.PlayersClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.pause_view.*
import java.lang.Exception


class MenuActivity : AppCompatActivity() {

    var sound:Boolean = true
    private val RC_SIGN_IN = 9001
    private val RC_LEADERBOARD_UI = 9004

    // Client used to sign in with Google APIs
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var playersClient: PlayersClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        googleSignInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build())

        setupUI()
    }

    private fun setupUI(){
        setEventHandlers()
    }

    private fun setEventHandlers(){

        button_menu_start.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("sound", sound)
            startActivity(intent)
        }

        button_menu_sound.setOnClickListener {
            sound = !sound

            if(sound){
                button_menu_sound.text = getString(R.string.menu_item_1)
            }else{
                button_menu_sound.text = getString(R.string.menu_item_1_toggle)
            }
        }

        button_menu_high_score.setOnClickListener {
            showLeaderboard()
        }

        button_menu_sign_in.setOnClickListener {
            startSignInIntent()
        }

        button_menu_sign_out.setOnClickListener {
            if (isSignedIn()) {
                googleSignInClient.signOut().addOnCompleteListener(this,
                        OnCompleteListener<Void> { task ->
                            val successful = task.isSuccessful

                            onDisconnected()
                        })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        signInSilently()
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun startSignInIntent(){
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun signInSilently() {
        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                OnCompleteListener<GoogleSignInAccount> { task ->
                    if (task.isSuccessful) {
                        onConnected(task.result)
                    } else {
                        onDisconnected()
                    }
                })
    }

    fun onConnected(googleSignInAccount: GoogleSignInAccount?) {
        sign_out_bar.visibility = View.VISIBLE
        sign_in_bar.visibility = View.GONE
        playersClient = Games.getPlayersClient(this, googleSignInAccount!!)
        playersClient.currentPlayer.addOnCompleteListener(object : OnCompleteListener<Player>{
            override fun onComplete(p0: Task<Player>) {
            }

        })
    }

    fun onDisconnected(){
        sign_out_bar.visibility = View.GONE
        sign_in_bar.visibility = View.VISIBLE
    }

    private fun showLeaderboard() {
        if(isSignedIn()){
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .getLeaderboardIntent(getString(R.string.leaderboard_peretti_squares_leaderboard))
                    .addOnSuccessListener(object : OnSuccessListener<Intent> {
                        override fun onSuccess(p0: Intent?) {
                            startActivityForResult(p0, RC_LEADERBOARD_UI);
                        }
                    })
                    .addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: Exception) {
                        }
                    })
        }else{
            startSignInIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result : GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                val signedInAccount = result.getSignInAccount()
                showLeaderboard()
            } else {
                var message = result.getStatus().getStatusMessage()

                if (message == null || message.isEmpty()) {
                    message = "Error signing in with Google"
                }

                AlertDialog.Builder(this).setMessage(message)
                    .setNeutralButton(android.R.string.ok, null).show()
            }
        }

        if (requestCode == RC_SIGN_IN && resultCode != 0){

        }
    }
}
