package com.anureet.minesweeper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var level = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSavedScores()

        buttonCustomBoard.setOnClickListener{
            level=""
            customTextVisibility(buttonCustomBoard)
        }
        easy.setOnClickListener {
            level="easy"
            customTextVisibility(easy)
        }
        medium.setOnClickListener{
            level="medium"
            customTextVisibility(medium)
        }
        hard.setOnClickListener{
            level="hard"
            customTextVisibility(hard)
        }
        startButton.setOnClickListener{
            startGame(level)
        }

    }

    override fun onResume() {
        super.onResume()
        getSavedScores()
        val intent = intent
        lastGameTime.text=""+intent.getStringExtra("lastTime")
        bestTime.text = ""+intent.getStringExtra("highScore")
    }

    fun getSavedScores() {
        // Setting highScore and LastGame Time
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        var highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), 0)
        var lastTime = sharedPref.getInt(getString(R.string.last_time),0)

        Log.d("MainActivity","entered + $highScore + $lastTime")
        lastGameTime.text = ""+lastTime
        bestTime.text = ""+highScore

    }

    fun startGame(level: String){
        if(level.equals("")){
            val rows = findViewById<EditText>(R.id.enterRows)
            val columns = findViewById<EditText>(R.id.enterColumns)
            val mines = findViewById<EditText>(R.id.enterMines)

            // Making sure the fields are not empty
            if(TextUtils.isEmpty(rows.text.toString()) || TextUtils.isEmpty(columns.text.toString()) || TextUtils.isEmpty(mines.text.toString())){
                Toast.makeText(this,"Fields cannot be empty!!",Toast.LENGTH_LONG).show()
            }
            else {
                var row = Integer.parseInt(rows.text.toString())
                var column = Integer.parseInt(columns.text.toString())
                var mine = Integer.parseInt(mines.text.toString())

                //Checking for overcrowding of mines
                if(mine > (row*column/4)){
                    Toast.makeText(this,"The number of mines should be less",Toast.LENGTH_LONG).show()
                }else {
                    val intent = Intent(this, BoardActivity::class.java).apply {
                        putExtra("numberOfRows", row)
                        putExtra("numberOfColumns", column)
                        putExtra("numberOfMines", mine)
                        putExtra("flag", 0)
                    }
                    startActivity(intent)
                }
            }
        }else{
            val intent = Intent(this, BoardActivity::class.java).apply {
                putExtra("selectedLevel",level)
                putExtra("flag",1)
            }
            startActivity(intent)
        }
    }

    fun customTextVisibility(view: View){
        if (view is RadioButton) {
            val checked = view.isChecked
            if(checked) {
                // Setting view visibility if radio buttons are used
                enterRows.visibility = View.INVISIBLE
                enterColumns.visibility = View.INVISIBLE
                enterMines.visibility = View.INVISIBLE
            }
        }else{
            // Clearing Radio Buttons
            selectGameLevel.clearCheck()

            // Setting view visibility
            enterRows.visibility = View.VISIBLE
            enterColumns.visibility = View.VISIBLE
            enterMines.visibility = View.VISIBLE
        }
    }



}
