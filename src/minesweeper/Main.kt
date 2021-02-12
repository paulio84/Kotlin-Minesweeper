package minesweeper

import kotlin.random.Random

fun main() {
    var minesPlaced = 0
    print("How many mines do you want on the field? ")
    val numOfMines = readLine()!!.toInt()

    val minesweeper = Minesweeper(numOfMines)
    println(minesweeper.toString())

    while (minesweeper.numOfMinesFound < numOfMines || minesPlaced > numOfMines) {
        print("Set/delete mine marks (x and y coordinates): ")
        val (xPos, yPos) = readLine()!!.split(" ").toTypedArray()

        when (minesweeper.makeGuess(xPos.toInt() - 1, yPos.toInt() - 1)) {
            GuessResponse.MINE_REMOVED -> {
                minesPlaced = if (--minesPlaced < 0) 0 else minesPlaced
                println(minesweeper.toString())
            }
            GuessResponse.MINE_PLACED -> {
                minesPlaced++
                println(minesweeper.toString())
            }
            GuessResponse.SELECTED_A_NUMBER -> println(GuessResponse.SELECTED_A_NUMBER.message)
        }
    }
    println("Congratulations! You found all the mines!")
}

enum class GuessResponse(val message: String) {
    MINE_REMOVED(""),
    MINE_PLACED(""),
    SELECTED_A_NUMBER("There is a number here!")
}

class MineLocation(
    val x: Int,
    val y: Int) {

    override fun toString(): String {
        return "$x,$y"
    }
}

fun MutableList<MineLocation>.findByMineLocation(x: Int, y: Int): Int {
    for (i in this.indices) {
        if (this[i].x == x && this[i].y == y) return i
    }
    return -1
}

class Minesweeper(private val mines: Int, boardSize: Int = 9) {

    private val gameBoard = Array(boardSize) { Array(boardSize) { '.' } }
    private val mineLocations = mutableListOf<MineLocation>()
    var numOfMinesFound = 0

    init {
        this.placeMinesOnBoard()
        this.generateHints()
    }

    fun makeGuess(x: Int, y: Int): GuessResponse {
        val mineLocationIndex = this.mineLocations.findByMineLocation(x, y)
        return when (this.gameBoard[y][x]) {
            '.' -> {
                this.gameBoard[y][x] = '*'
                if (mineLocationIndex >= 0) numOfMinesFound++
                GuessResponse.MINE_PLACED
            }
            '*' -> {
                this.gameBoard[y][x] = '.'
                if (mineLocationIndex >= 0) numOfMinesFound--
                GuessResponse.MINE_REMOVED
            }
            else -> GuessResponse.SELECTED_A_NUMBER
        }
    }

    override fun toString(): String {
        var str = "\n |123456789|\n-|---------|\n"
        for (i in gameBoard.indices) {
            str += "${i + 1}|${gameBoard[i].joinToString("")}|\n"
        }
        str += "-|---------|"

        return str
    }

    private fun placeMinesOnBoard() {
        while (mineLocations.size < this.mines) {
            val x = Random.nextInt(gameBoard.size)
            val y = Random.nextInt(gameBoard.size)

            if (gameBoard[y][x] == '.') {
                gameBoard[y][x] = 'X'

                mineLocations.add(MineLocation(x, y))
            }
        }
    }

    private fun generateHints() {
        var mineCounter = 0
        for (y in gameBoard.indices) {
            for (x in gameBoard[y].indices) {
                // check the previous row only if the current row is > 0
                if (y > 0) {
                    if (x > 0 && gameBoard[y - 1][x - 1] == 'X') mineCounter++
                    if (gameBoard[y - 1][x] == 'X') mineCounter++
                    if (x < gameBoard[y].lastIndex && gameBoard[y - 1][x + 1] == 'X') mineCounter++
                }

                // check the current row
                if (x > 0 && gameBoard[y][x - 1] == 'X') mineCounter++
                if (x < gameBoard[y].lastIndex && gameBoard[y][x + 1] == 'X') mineCounter++

                // check the next row only if the current is < gameBoard.lastIndex
                if (y < gameBoard.lastIndex) {
                    if (x > 0 && gameBoard[y + 1][x - 1] == 'X') mineCounter++
                    if (gameBoard[y + 1][x] == 'X') mineCounter++
                    if (x < gameBoard[y].lastIndex && gameBoard[y + 1][x + 1] == 'X') mineCounter++
                }

                if (gameBoard[y][x] == '.' && mineCounter > 0) gameBoard[y][x] = (mineCounter + '0'.toInt()).toChar()
                mineCounter = 0
            }
        }

        for (i in this.mineLocations.indices) {
            this.gameBoard[this.mineLocations[i].y][this.mineLocations[i].x] = '.'
        }
    }
}