import java.awt.Point
import java.io.File

val input = File("input.txt").readLines()


with("Part A") {
	val rawGrid = input.takeWhile { it.isNotEmpty() }
	val width = rawGrid.maxOf { it.length }
	val grid = listOf(" ".repeat(width + 2)) +
			rawGrid.map { " " + it.padEnd(width, ' ') + " " } +
			listOf(" ".repeat(width + 2))

	val instructions = input.last().split("(?<=[A-Z])(?=\\d)|(?<=\\d)(?=[A-Z])".toRegex())

	val currPosition = Point(input.first().indexOfFirst { it == '.' },1)
	var currHeading = 0

	instructions.forEach { instruction ->
		if (instruction == "R") {
			currHeading = (currHeading + 1) % 4
		} else if (instruction == "L") {
			currHeading = (currHeading + 3) % 4
		} else {
			for (i in 0 until instruction.toInt()) {
				val nextPos = Point(currPosition).apply {
					when (currHeading) {
						0 -> x++
						1 -> y++
						2 -> x--
						3 -> y--
					}
				}
				if (grid[nextPos.y][nextPos.x] == ' ') {
					// wrap around
					when (currHeading) {
						0 -> nextPos.x = grid[nextPos.y].indexOfFirst { it != ' ' }
						1 -> nextPos.y = grid.indexOfFirst { it[nextPos.x] != ' ' }
						2 -> nextPos.x = grid[nextPos.y].indexOfLast { it != ' ' }
						3 -> nextPos.y = grid.indexOfLast { it[nextPos.x] != ' ' }
					}
				}
				if (grid[nextPos.y][nextPos.x] == '#') {
					break
				} else {
					currPosition.setLocation(nextPos)
				}
			}
		}
	}

	val pass = 1000 * currPosition.y + 4 * currPosition.x + currHeading

	println("$this: $pass")
}


with("Part B") {
	val rawGrid = input.takeWhile { it.isNotEmpty() }
	val width = rawGrid.maxOf { it.length }
	val grid = listOf(" ".repeat(width + 2)) +
			rawGrid.map { " " + it.padEnd(width, ' ') + " " } +
			listOf(" ".repeat(width + 2))

	val instructions = input.last().split("(?<=[A-Z])(?=\\d)|(?<=\\d)(?=[A-Z])".toRegex())

	val currPosition = Point(input.first().indexOfFirst { it == '.' },1)
	var currHeading = 0

	instructions.forEach { instruction ->
		if (instruction == "R") {
			currHeading = (currHeading + 1) % 4
		} else if (instruction == "L") {
			currHeading = (currHeading + 3) % 4
		} else {
			for (i in 0 until instruction.toInt()) {
				val nextPos = Point(currPosition).apply {
					when (currHeading) {
						0 -> x++
						1 -> y++
						2 -> x--
						3 -> y--
					}
				}
				var nextHeading = currHeading
				if (grid[nextPos.y][nextPos.x] == ' ') {
					// wrap around
					when (currHeading) {
						0 -> {
							// right
							if (currPosition.y <= 50) {
								nextHeading = 2
								nextPos.x = 100
								nextPos.y = 151 - currPosition.y
							} else if (currPosition.y <= 100) {
								nextHeading = 3
								nextPos.x = currPosition.y + 50
								nextPos.y = 50
							} else if (currPosition.y <= 150) {
								nextHeading = 2
								nextPos.x = 150
								nextPos.y = 151 - currPosition.y
							} else if (currPosition.y <= 200) {
								nextHeading = 3
								nextPos.x = currPosition.y - 100
								nextPos.y = 150
							}
						}
						1 -> {
							// down
							if (currPosition.x <= 50) {
								nextHeading = 1
								nextPos.x = currPosition.x + 100
								nextPos.y = 1
							} else if (currPosition.x <= 100) {
								nextHeading = 2
								nextPos.x = 50
								nextPos.y = currPosition.x + 100
							} else if (currPosition.x <= 150) {
								nextHeading = 2
								nextPos.x = 100
								nextPos.y = currPosition.x - 50
							}
						}
						2 -> {
							// left
							if (currPosition.y <= 50) {
								nextHeading = 0
								nextPos.x = 1
								nextPos.y = 151 - currPosition.y
							} else if (currPosition.y <= 100) {
								nextHeading = 1
								nextPos.x = currPosition.y - 50
								nextPos.y = 101
							} else if (currPosition.y <= 150) {
								nextHeading = 0
								nextPos.x = 51
								nextPos.y = 151 - currPosition.y
							} else if (currPosition.y <= 200) {
								nextHeading = 1
								nextPos.x = currPosition.y - 100
								nextPos.y = 1
							}
						}
						3 -> {
							// up
							if (currPosition.x <= 50) {
								nextHeading = 0
								nextPos.x = 51
								nextPos.y = currPosition.x + 50
							} else if (currPosition.x <= 100) {
								nextHeading = 0
								nextPos.x = 1
								nextPos.y = currPosition.x + 100
							} else if (currPosition.x <= 150) {
								nextHeading = 3
								nextPos.x = currPosition.x - 100
								nextPos.y = 200
							}
						}
					}
				}
				if (grid[nextPos.y][nextPos.x] == '#') {
					break
				} else {
					currPosition.setLocation(nextPos)
					currHeading = nextHeading
				}
			}
		}
	}

	val pass = 1000 * currPosition.y + 4 * currPosition.x + currHeading

	println("$this: $pass")
}
