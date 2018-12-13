package squares.cherry_blossom.com.perettisquares.Game

import android.content.res.Resources
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SqaureGame(val view: GameActivity) {

    var time: Int = 0
    var score: Long = 0
    var streak: Int = 0
    var gameStarted: Boolean = false

    var correctIndex: Int = -1
    var correctColor: Int = -1
    var incorrectColor: Int = -1
    var backgroundColor: Int = -1

    var sound:Boolean = true

    lateinit var timer: CountDownTimer
    var currentTime: Long = 60000

    val colors: HashMap<Int, ArrayList<String>> = hashMapOf()

    public fun setupGame(){
        setupColors()
        correctIndex = getNewCorrectIndex()
        getNewColors()
        view.updateColors()
    }

    public fun startGame(time : Long) {
        gameStarted = true

        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateViewTime(millisUntilFinished)
                currentTime = millisUntilFinished
            }

            override fun onFinish() {
                gameStarted = false
                updateViewEndGame()
            }
        }.start()

    }

    public fun pauseGame(){
        gameStarted = false
        timer.cancel()
    }

    public fun checkSquare(squareIndex: Int){
        if(gameStarted){
            if(squareIndex == correctIndex){
                score += streak
                streak++
                view.updatePoints(score, streak)
            }else{
                streak = 0
                view.updatePoints(score, streak)
            }
            correctIndex = getNewCorrectIndex()
            getNewColors()
            view.updateColors()
        }
    }

    private fun updateViewTime(time: Long) {
        view.updateTime(time)
    }

    private fun updateViewEndGame() {
        view.endGame()
    }

    private fun getNewCorrectIndex(): Int {
        val random: Random = Random()

        return random.nextInt(4) + 1
    }

    private fun getNewColors(){
        val random: Random = Random()

        val int: Int = random.nextInt(colors.size)
        val colorSet: ArrayList<String>? = colors.get(int)

        val correctColorString: String? = colorSet?.get(0)
        val incorrectColorString: String? = colorSet?.get(1)
        val backgroundColorString: String? = colorSet?.get(2)

        correctColor = Color.parseColor(correctColorString)
        incorrectColor = Color.parseColor(incorrectColorString)
        backgroundColor = Color.parseColor(backgroundColorString)
    }

    private fun setupColors(){
        colors.put(0, arrayListOf("#00FF00", "#FF00FF", "#228B22"))
        colors.put(1, arrayListOf("#42C0FB", "#FFFF00", "#35586C"))
        colors.put(2, arrayListOf("#236B8E", "#FFA500", "#FF8C00"))
        colors.put(3, arrayListOf("#FFFF00", "#BA55D3", "#800080"))
        colors.put(4, arrayListOf("#00FFFF", "#FF0000", "#8B0000"))
        colors.put(5, arrayListOf("#FF00FF", "#87CEFA", "#0000CD"))
        colors.put(6, arrayListOf("#FFA07A", "#FF0000", "#800000"))
        colors.put(7, arrayListOf("#7CFC00", "#00FA9A", "#006400"))
        colors.put(8, arrayListOf("#E6E6FA", "#00BFFF", "#000080"))
        colors.put(9, arrayListOf("#FF6347", "#FFD700", "#FF8C00"))
    }
}