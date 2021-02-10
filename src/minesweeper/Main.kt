package minesweeper

import kotlin.random.Random

fun main() {
    print("How many mines do you want on the field? ")
    val numOfMines = readLine()!!.toInt()

    val minesweeper = Minesweeper(numOfMines)
    println(minesweeper.displayGameBoard())
}

class Minesweeper(
    private val mines: Int,
    boardSize: Int = 9) {

    private val gameBoard = Array(boardSize) { Array(boardSize) { '.' } }

    init {
        this.placeMinesOnBoard()
        this.generateHints()
    }

    fun displayGameBoard(): String {
        var str = ""
        for (i in gameBoard.indices) {
            str += "${gameBoard[i].joinToString("")}\n"
        }

        return str
    }

    private fun placeMinesOnBoard() {
        var numOfPlacedMines = 0
        while (numOfPlacedMines < this.mines) {
            val x = Random.nextInt(gameBoard.size)
            val y = Random.nextInt(gameBoard.size)

            if (gameBoard[x][y] == '.') {
                gameBoard[x][y] = 'X'
                numOfPlacedMines++
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
    }
}