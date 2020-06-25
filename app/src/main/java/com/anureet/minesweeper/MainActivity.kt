package com.anureet.minesweeper

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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

    // On resuming of activity
    override fun onResume() {
        super.onResume()
        val intent = intent
        if(intent.getStringExtra("lastTime") != null || intent.getStringExtra("highScore") != null ) {
            lastGameTime.text = "" + intent.getStringExtra("lastTime")
            bestTime.text = "" + intent.getStringExtra("highScore")
        }else{
            lastGameTime.text = " NA"
            bestTime.text = " NA"
        }
    }

    // This function will get called on clicking start button
    private fun startGame(level: String){
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

                // Checking for overcrowding of rows and columns
                if(row>25 || column>25){
                    Toast.makeText(this,"The number of rows and columns should be less than 25",Toast.LENGTH_SHORT).show()
                }
                //Checking for overcrowding of mines
                else if(mine > (row*column/4)){
                    Toast.makeText(this,"The number of mines should be less to avoid overcrowding",Toast.LENGTH_LONG).show()
                }
                // Sending row, column and mine number using intents
                else {
                    val intent = Intent(this, BoardActivity::class.java).apply {
                        putExtra("numberOfRows", row)
                        putExtra("numberOfColumns", column)
                        putExtra("numberOfMines", mine)
                        putExtra("flag", 0)
                    }
                    startActivity(intent)
                }
            }
        }
        // Sending the selected level using intents
        else{
            val intent = Intent(this, BoardActivity::class.java).apply {
                putExtra("selectedLevel",level)
                putExtra("flag",1)
            }
            startActivity(intent)
        }
    }

    // Setting visibility of textViews on the basis of level/custom board selection
    private fun customTextVisibility(view: View){
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
