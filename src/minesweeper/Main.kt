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
    private val boardSize: Int = 9) {

    private val gameBoard = Array(boardSize) { Array(boardSize) { '.' } }

    init {
        this.placeMinesOnBoard()
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
}