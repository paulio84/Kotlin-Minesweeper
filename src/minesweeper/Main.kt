package minesweeper

import kotlin.random.Random

fun main() {
    var minesPlaced = 0
    print("How many mines do you want on the field? ")
    val numOfMines = readLine()?.trim()?.toInt() ?: 10

    val minesweeper = Minesweeper(numOfMines)
    printGameBoard(minesweeper)

    while (!minesweeper.isMineSteppedOn && minesweeper.numOfMinesFound < numOfMines || minesPlaced > numOfMines) {
        print("Set/unset mine marks or claim a cell as free: ")
        val (xPos, yPos, action) = readLine()?.split(" ")?.toTypedArray() ?: throw IllegalArgumentException("Please supply x, y coords and an action (f or m)!")
        val gameAction = Action.findActionByValue(action)

        when (val response = minesweeper.makeGuess(xPos.toInt() - 1, yPos.toInt() - 1, gameAction)) {
            GuessResponse.GUESS_REMOVED, GuessResponse.GUESS_PLACED, GuessResponse.FREE_SPACE -> {
                if (response == GuessResponse.GUESS_REMOVED) {
                    minesPlaced = if (--minesPlaced < 0) 0 else minesPlaced
                } else if (response == GuessResponse.GUESS_PLACED) {
                    minesPlaced++
                }
                printGameBoard(minesweeper)
            }
            GuessResponse.INVALID_ACTION, GuessResponse.SELECTED_A_NUMBER -> println(response.message)
            GuessResponse.MINE_STEPPED_ON -> {
                printGameBoard(minesweeper)
                println(GuessResponse.MINE_STEPPED_ON.message)
            }
        }
    }
    if (!minesweeper.isMineSteppedOn) println("Congratulations! You found all the mines!")
}

fun printGameBoard(ms: Minesweeper) {
    val gameBoard = ms.gameBoard
    println("\n |123456789|\n-|---------|")

    for (i in gameBoard.indices) {
        print("${i + 1}|")
        for (j in gameBoard[i].indices) {
            val cell = gameBoard[i][j]

            if (cell.isRevealed) {
                print("${CellValue.getValueOf(gameBoard[i][j].value)}")
            } else {
                print("${CellValue.getValueOf(CellValue.EMPTY)}")
            }
        }
        println("|")
    }

    println("-|---------|")
}

enum class Action(val value: String) {
    FREE("free"),
    MINE("mine"),
    NULL("");

    companion object {
        fun findActionByValue(v: String): Action {
            for (enum in values()) {
                if (enum.value == v) return enum
            }
            return NULL
        }
    }
}

enum class GuessResponse(val message: String) {
    GUESS_REMOVED(""),
    GUESS_PLACED(""),
    SELECTED_A_NUMBER("There is a number here!"),
    MINE_STEPPED_ON("You stepped on a mine and failed!"),
    FREE_SPACE(""),
    INVALID_ACTION("Invalid action, enter 'f (free)' or 'm (mine)'")
}

enum class CellValue(private val ch: Char) {
    EMPTY('.'),
    MINE('X'),
    FREE('/'),
    GUESS('*'),
    ONE('1'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NULL(' ');

    companion object {
        fun findDigitByValue(value: Int): CellValue {
            for (enum in values()) {
                if (enum.ch == (value + '0'.toInt()).toChar()) return enum
            }
            return NULL
        }

        fun getValueOf(cv: CellValue): Char {
            for (enum in values()) {
                if (enum == cv) return enum.ch
            }
            return NULL.ch
        }
    }
}

class BoardCell(
    val x: Int = 0,
    val y: Int = 0,
    var isDigit: Boolean = false,
    var isMine: Boolean = false,
    var isRevealed: Boolean = false,
    var value: CellValue = CellValue.EMPTY) {

    var originalValue: CellValue = value

    fun placeMine(): Boolean {
        if (this.value != CellValue.MINE) {
            this.value = CellValue.MINE
            this.originalValue = value
            this.isMine = true
            return true
        }

        return false
    }

    fun isDigitOrFree(): Boolean {
        return this.isDigit || this.value == CellValue.FREE
    }
}

class Minesweeper(numOfMines: Int, boardSize: Int = 9) {
    lateinit var gameBoard: Array<Array<BoardCell>>
    var numOfMinesFound = 0
    var isMineSteppedOn = false

    init {
        this.initialiseBoardCells(boardSize)
        this.placeMines(numOfMines)
        this.generateHints()
    }

    private fun initialiseBoardCells(boardSize: Int) {
        this.gameBoard = Array(boardSize) { Array(boardSize) { BoardCell() } }
        for (y in 0 until boardSize) {
            for (x in 0 until boardSize) {
                this.gameBoard[y][x] = BoardCell(x, y)
            }
        }
    }

    private fun placeMines(numOfMines: Int) {
        var minesPlaced = 0
        while (minesPlaced < numOfMines) {
            val x = Random.nextInt(this.gameBoard.size)
            val y = Random.nextInt(this.gameBoard.size)

            if (this.gameBoard[y][x].placeMine()) minesPlaced++
        }
    }

    private fun generateHints() {
        var mineCounter = 0
        for (y in this.gameBoard.indices) {
            for (x in this.gameBoard[y].indices) {
                // check the previous row only if the current row is > 0
                if (y > 0) {
                    if (x > 0 && this.gameBoard[y - 1][x - 1].isMine) mineCounter++
                    if (this.gameBoard[y - 1][x].isMine) mineCounter++
                    if (x < this.gameBoard[y].lastIndex && this.gameBoard[y - 1][x + 1].isMine) mineCounter++
                }

                // check the current row
                if (x > 0 && this.gameBoard[y][x - 1].isMine) mineCounter++
                if (x < this.gameBoard[y].lastIndex && this.gameBoard[y][x + 1].isMine) mineCounter++

                // check the next row only if the current is < gameBoard.lastIndex
                if (y < this.gameBoard.lastIndex) {
                    if (x > 0 && this.gameBoard[y + 1][x - 1].isMine) mineCounter++
                    if (this.gameBoard[y + 1][x].isMine) mineCounter++
                    if (x < this.gameBoard[y].lastIndex && this.gameBoard[y + 1][x + 1].isMine) mineCounter++
                }

                if (this.gameBoard[y][x].value == CellValue.EMPTY && mineCounter > 0) {
                    this.gameBoard[y][x].value = CellValue.findDigitByValue(mineCounter)
                    this.gameBoard[y][x].isDigit = true
                    this.gameBoard[y][x].originalValue = this.gameBoard[y][x].value
                }
                mineCounter = 0
            }
        }
    }

    private fun revealAllMines() {
        for (y in this.gameBoard.indices) {
            for (x in this.gameBoard[y].indices) {
                val cell = this.gameBoard[y][x]
                if (cell.isMine) {
                    cell.value = CellValue.MINE
                    cell.isRevealed = true
                }
            }
        }
    }

    private fun revealEmptyCells(x: Int, y: Int) {
        val cell = this.gameBoard[y][x]
        cell.isRevealed = true

        if (cell.isDigitOrFree()) {
            if (cell.value == CellValue.GUESS) cell.value = cell.originalValue
            return
        }
        else {
            cell.value = CellValue.FREE

            // automatically reveal cells until a digit or a mine is found
            // check adjacent cells
            if (x > 0) this.revealEmptyCells(cell.x - 1, cell.y)
            if (x < this.gameBoard.lastIndex) this.revealEmptyCells(cell.x + 1, cell.y)

            // check previous line if y is > 0
            if (y > 0) {
                if (x > 0) this.revealEmptyCells(cell.x - 1, cell.y - 1)
                this.revealEmptyCells(cell.x, cell.y - 1)
                if (x < this.gameBoard.lastIndex) this.revealEmptyCells(cell.x + 1, cell.y - 1)
            }
            if (y < this.gameBoard.lastIndex) {
                if (x > 0) this.revealEmptyCells(cell.x - 1, cell.y + 1)
                this.revealEmptyCells(cell.x, cell.y + 1)
                if (x < this.gameBoard.lastIndex) this.revealEmptyCells(cell.x + 1, cell.y + 1)
            }
        }
    }

    fun makeGuess(x: Int, y: Int, action: Action): GuessResponse {
        val cell = this.gameBoard[y][x]

        return when {
            action == Action.MINE -> {
                // right clicking to mark a cell as a mine
                if (cell.value != CellValue.FREE) {
                    cell.isRevealed = !cell.isRevealed
                    if (cell.isRevealed) {
                        if (cell.isMine) this.numOfMinesFound++
                        cell.value = CellValue.GUESS
                        GuessResponse.GUESS_PLACED
                    } else {
                        if (cell.isMine) this.numOfMinesFound--
                        cell.value = cell.originalValue
                        GuessResponse.GUESS_REMOVED
                    }
                } else {
                    GuessResponse.FREE_SPACE
                }
            }
            cell.isDigit && cell.isRevealed -> {
                // clicking on a digit
                GuessResponse.SELECTED_A_NUMBER
            }
            cell.isMine && action == Action.FREE -> {
                // left clicking on a mine - KABOOM!!
                this.isMineSteppedOn = true
                this.revealAllMines()
                GuessResponse.MINE_STEPPED_ON
            }
            !cell.isMine && action == Action.FREE -> {
                // left clicking on a cell to explore - expand the free space
                this.revealEmptyCells(x, y)
                GuessResponse.FREE_SPACE
            }
            else -> GuessResponse.INVALID_ACTION
        }
    }
}
