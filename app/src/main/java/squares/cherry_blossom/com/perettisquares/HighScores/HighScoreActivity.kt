package squares.cherry_blossom.com.perettisquares.HighScores

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.high_score_activity.*
import squares.cherry_blossom.com.perettisquares.Game.GameActivity
import squares.cherry_blossom.com.perettisquares.Menu.MenuActivity
import squares.cherry_blossom.com.perettisquares.R
import java.lang.Exception

class HighScoreActivity : AppCompatActivity() {

    private var sound: Boolean = true
    private var score: Long = 0
    private val RC_LEADERBOARD_UI = 9004
    private val RC_SIGN_IN = 9001

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.high_score_activity)

        sound = intent.getBooleanExtra("sound", true)
        score = intent.getLongExtra("score", 0)

        googleSignInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build())

        setupUI()
    }

    private fun setupUI(){
        text_view_score_value.textSize = getTextSize()
        text_view_score_value.text = score.toString()

        setupEventHandlers()
    }

    private fun setupEventHandlers(){
        button_main_menu.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        button_high_score.setOnClickListener {
            showLeaderboard()
        }

        button_play_again.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("sound", sound)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        signInSilently()
    }

    private fun getTextSize() : Float{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels

        width /= 12

        return width.toFloat()
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

    private fun signInSilently() {
        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                OnCompleteListener<GoogleSignInAccount> { task ->
                    if (task.isSuccessful) {
                        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                                .submitScore(getString(R.string.leaderboard_peretti_squares_leaderboard), score)
                    } else {

                    }
                })
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun startSignInIntent(){
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN && resultCode != 0){
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                    .submitScore(getString(R.string.leaderboard_peretti_squares_leaderboard), score)
            showLeaderboard()
        }
    }

}
