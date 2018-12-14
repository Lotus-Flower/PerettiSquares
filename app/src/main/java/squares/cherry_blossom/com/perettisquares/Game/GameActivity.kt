package squares.cherry_blossom.com.perettisquares.Game

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.game_activity.*
import squares.cherry_blossom.com.perettisquares.HighScores.HighScoreActivity
import squares.cherry_blossom.com.perettisquares.R
import squares.cherry_blossom.com.perettisquares.Menu.MenuActivity


class GameActivity : AppCompatActivity() {

    val game: SqaureGame = SqaureGame(this)
    var squares: ArrayList<Button> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        game.sound = intent.getBooleanExtra("sound", true)

        setupUI()
    }

    private fun setupUI(){

        button_restart.isEnabled = false

        val dimension: Int = getSquareDimensions()

        squares.add(button_square_1)
        squares.add(button_square_2)
        squares.add(button_square_3)
        squares.add(button_square_4)

        for(square in squares){
            val params:LinearLayout.LayoutParams = LinearLayout.LayoutParams(dimension, dimension)
            params.setMargins(8, 8, 8, 8)
            square.layoutParams = params
        }

        setEventHandlers()
    }

    private fun setEventHandlers(){

        button_restart.setOnClickListener {
            showPause()
        }

        for(i in squares.indices){
            squares.get(i).setOnClickListener {
                notifySquareEvent(i + 1)
            }
        }

        game.setupGame()
    }

    private fun getSquareDimensions() : Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        return (width * 2)/5
    }

    private fun notifySquareEvent(index: Int){
        if(!game.gameStarted){
            //60 seconds on initial start
            game.startGame(60000)
            button_restart.isEnabled = true
            game.checkSquare(index)
        }else{
            game.checkSquare(index)
        }

        if(game.sound){
            val mediaPlayer:MediaPlayer = MediaPlayer.create(this, R.raw.square)
            mediaPlayer.start()
        }
    }

    fun updateColors() {
        for(i in squares.indices){
            if((i + 1) == game.correctIndex){
                squares.get(i).setBackgroundColor(game.correctColor)
            }else{
                squares.get(i).setBackgroundColor(game.incorrectColor)
            }
        }
        layout_game.setBackgroundColor(game.backgroundColor)
    }

    fun updateTime(time: Long) {
        val timeInSeconds: Long = (time/1000)
        text_view_time_value.text = timeInSeconds.toString()
    }

    fun updatePoints(points: Long, streak: Int) {
        text_view_streak_value.text = streak.toString()
        text_view_points_value.text = points.toString()
    }

    fun endGame() {
        val intent = Intent(this, HighScoreActivity::class.java)
        intent.putExtra("sound", game.sound)
        intent.putExtra("score", game.score)
        startActivity(intent)
    }

    fun showPause(){

        game.pauseGame()

        val dialogBuilder =  MaterialDialog.Builder(this)
                .customView(R.layout.pause_view, false)
                .canceledOnTouchOutside(false)
        val dialog = dialogBuilder.build()

        val resumeButton = dialog.customView!!.findViewById<Button>(R.id.button_pause_resume)
        val restartButton = dialog.customView!!.findViewById<Button>(R.id.button_pause_restart)
        val menuButton = dialog.customView!!.findViewById<Button>(R.id.button_pause_menu)

        resumeButton.setOnClickListener {
            game.startGame(game.currentTime)
            dialog.dismiss()
        }

        restartButton.setOnClickListener {
            game.setupGame()
            updatePoints(0, 0)
            updateTime(60000)
            dialog.dismiss()
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
}
