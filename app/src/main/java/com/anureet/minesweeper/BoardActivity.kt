package com.anureet.minesweeper

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_board.*
import kotlin.random.Random


class BoardActivity : AppCompatActivity() {

    private lateinit var chronometer : Chronometer
    private lateinit var mineCount : TextView

    var choice : Int = 1
    var status  = Status.ONGOING
    private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val intent = intent
        var flag = intent.getIntExtra("flag",2)
        if(flag==1){
            var level = intent.getStringExtra("selectedLevel")
            if(level.equals("easy")){
                setUpBoard(12,12,6)
            }else if(level.equals("medium")){
                setUpBoard(14,14,14)
            }else if(level.equals("hard")){
                setUpBoard(16,16,20)
            }
        }else{
            var row = intent.getIntExtra("numberOfRows",0)
            var col = intent.getIntExtra("numberOfColumns",0)
            var mine = intent.getIntExtra("numberOfMines",0)
            setUpBoard(row,col,mine)

        }

        // Restarting the game
        restartGame.setOnClickListener{
            GameRestart()
        }
    }

    private fun GameRestart() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setMessage("Do you want to restart the game ?")
        builder.setTitle("Alert!")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes"
        ){ dialog, which ->
            restartGame.setImageResource(R.drawable.happy_face)
            val intent = getIntent()
            finish()
            startActivity(intent)

        }

        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    // Count-up timer
    private fun startTimer(){
        chronometer = findViewById(R.id.timer)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun setUpBoard(row: Int, col: Int, mine: Int) {

        val cellBoard = Array(row) { Array(col) {MineCell(this)}}

        mineFlagOption.setOnClickListener{
            if(choice==1) {
                mineFlagOption.setImageResource(R.drawable.flag)
                choice=2
            }else{
                mineFlagOption.setImageResource(R.drawable.bomb)
                choice=1
            }
        }

        mineCount = findViewById(R.id.mineCount)
        mineCount.setText(""+mine)

        var counter = 1
        var isFirstClick = true

        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        val params2 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        for(i in 0 until row){
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params1
            params1.weight  = 1.0F

            for(j in 0 until col){
                val button = MineCell(this)

                //Buttons are being stored to their corresponding locations in the array
                cellBoard[i][j] = button
                button.id = counter
                button.textSize = 18.0F

                button.layoutParams = params2
                params2.weight = 1.0F
                button.setBackgroundResource(R.drawable.ten)
                button.setOnClickListener{

                    // Checking for first click
                    if(isFirstClick){
                        isFirstClick = false

                        // Setting up mines
                        setMines(i,j,mine,cellBoard,row,col)

                        //Start Timer
                        startTimer()

                    }
                    // Checking if it is already marked and
                    // Unmarking it
                    if(choice==2 && button.isMarked){
                        choice = 3
                    }

                    // Move function
                    move(choice,i,j,cellBoard,row,col)
                    display(cellBoard)

                }
                linearLayout.addView(button)
                counter++
            }
            board.addView(linearLayout)
        }
    }

    private fun setMines(row:Int, col:Int, mine:Int, cellBoard:Array<Array<MineCell>>,rowSize:Int, colSize:Int) {
        //Generate random coordinates to set mine
        var mineCount = mine
        var i=1
        while(i<=mineCount){
            val rand = Random(System.nanoTime())
            var r = (Random(System.nanoTime()).nextInt(0, rowSize))
            var c = (Random(System.nanoTime()).nextInt(0, colSize))
//            (0 until colSize).random(rand)
            if(r==row || cellBoard[r][c].isMine){
                continue
            }
            cellBoard[r][c].isMine = true
            cellBoard[r][c].value = -1
            updateNeighbours(r,c,cellBoard,rowSize,colSize)
            i++;
        }

    }

    private fun updateNeighbours(row: Int,column: Int,cellBoard: Array<Array<MineCell>>,rowSize:Int,colSize:Int) {
        for (i in movement) {
            for (j in movement) {
                if(((row+i) in 0 until rowSize) && ((column+j) in 0 until colSize) && cellBoard[row+i][column+j].value != MINE)
                    cellBoard[row+i][column+j].value++
            }
        }
    }

    // Handles when board[x][y]==0
    private val xDir = intArrayOf(-1, -1, 0, 1, 1, 1, 0, -1)
    private val yDir = intArrayOf(0, 1, 1, 1, 0, -1, -1, -1)
    fun handleZero(x:Int ,y:Int, cellBoard:Array<Array<MineCell>>, rowSize: Int,colSize: Int){

        cellBoard[x][y].isRevealed = true
        for(i in 0..7){
            var xstep = x+xDir[i]
            var ystep = y+yDir[i]
            if((xstep<0 || xstep>=rowSize) || (ystep<0 || ystep>=colSize)){
                continue;
            }
            if(cellBoard[xstep][ystep].value>0 && !cellBoard[xstep][ystep].isMarked){
                cellBoard[xstep][ystep].isRevealed = true
            }else if( !cellBoard[xstep][ystep].isRevealed && !cellBoard[xstep][ystep].isMarked && cellBoard[xstep][ystep].value==0){
                handleZero(xstep,ystep,cellBoard,rowSize,colSize)

            }
        }

    }

    // To update status (ongoing/won)
    fun checkStatus(cellBoard:Array<Array<MineCell>>, rowSize:Int, colSize: Int){
        var flag1=0
        var flag2=0
        for(i in 0 until rowSize){
            for(j in 0 until colSize){
                if(cellBoard[i][j].value==MINE && !cellBoard[i][j].isMarked){
                    flag1=1
                }
                if(cellBoard[i][j].value!=MINE && !cellBoard[i][j].isRevealed){
                    flag2=1
                }
            }
        }
        if(flag1==0 || flag2==0) status = Status.WON
        else status = Status.ONGOING
    }

    fun move(choice: Int, x: Int, y:Int, cellBoard:Array<Array<MineCell>>, rowSize: Int,colSize: Int): Boolean{

        if(choice==1){
            if(cellBoard[x][y].isMarked || cellBoard[x][y].isRevealed){
                return false
            }
            if(cellBoard[x][y].value == MINE){
                status = Status.LOST;
                return true
            }
            else if(cellBoard[x][y].value >0){
                cellBoard[x][y].isRevealed = true
                checkStatus(cellBoard,rowSize,colSize);
                return true
            }
            else if(cellBoard[x][y].value==0){
                handleZero(x,y,cellBoard,rowSize,colSize)
                checkStatus(cellBoard,rowSize,colSize);
                return true
            }

        }

        if(choice == 2){
            if(cellBoard[x][y].isRevealed || cellBoard[x][y].isMarked){
                return false;
            }
            cellBoard[x][y].isMarked = true;
            checkStatus(cellBoard,rowSize,colSize)

            return true;
        }

        if(choice == 3){
            if(cellBoard[x][y].isRevealed) return false
            cellBoard[x][y].setBackgroundResource(R.drawable.ten)
            cellBoard[x][y].isMarked = false
            checkStatus(cellBoard,rowSize,colSize)

            return true
        }

        return false
    }

    private fun display(cellBoard:Array<Array<MineCell>>) {
        cellBoard.forEach { row ->
            row.forEach {
                if(it.isRevealed)
                    setNumberImage(it)
//                else if(!it.isMarked)
//                    it.setBackgroundResource(R.drawable.ten)
                else if (it.isMarked)
                    it.setBackgroundResource(R.drawable.flag1)
                else if (status == Status.LOST && it.value == MINE) {
                    restartGame.setImageResource(R.drawable.sad_face)
                    it.setBackgroundResource(R.drawable.mine)
                    chronometer.stop()
                }
                //To show that mine is not present here but it is marked
                if(status == Status.LOST && it.isMarked && !it.isMine){
                    it.setBackgroundResource(R.drawable.crossedflag)
                }
                else if (status == Status.WON && it.value == MINE) {
                    it.setBackgroundResource(R.drawable.flag1)
                    restartGame.setImageResource(R.drawable.won)
                    chronometer.stop()
                }
                else
                    it.text = " "
            }

        }
    }

    private fun setNumberImage(button:MineCell) {
        if(button.value==0) button.setBackgroundResource(R.drawable.zero)
        if(button.value==1) button.setBackgroundResource(R.drawable.one)
        if(button.value==2) button.setBackgroundResource(R.drawable.two)
        if(button.value==3) button.setBackgroundResource(R.drawable.three)
        if(button.value==4) button.setBackgroundResource(R.drawable.four)
        if(button.value==5) button.setBackgroundResource(R.drawable.five)
        if(button.value==6) button.setBackgroundResource(R.drawable.six)
        if(button.value==7) button.setBackgroundResource(R.drawable.seven)
        if(button.value==8) button.setBackgroundResource(R.drawable.eight)

    }


    companion object{
        const val MINE = -1
        val movement = intArrayOf(-1, 0, 1)
    }
}
enum class Status{
    WON,
    ONGOING,
    LOST
}