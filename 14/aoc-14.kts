import java.awt.Point
import java.io.File

val input = File("input.txt").readLines()

with("Part A") {
	// parse input
	val rocks = input.map { line ->
		line.split(" -> ")
			.map { it.split(",") }
			.map { (x, y) -> Point(x.toInt(), y.toInt()) }
	}
	val caveOffset = rocks.flatten().minOf { it.x }
	rocks.flatten().forEach { p -> p.x -= caveOffset }

	// draw cave matrix
	val caveWidth = rocks.flatten().maxOf { it.x } + 1
	val caveHeight = rocks.flatten().maxOf { it.y } + 1
	val cave = List(caveHeight) { MutableList(caveWidth) { '.' } }
	rocks.forEach { corners ->
		corners.windowed(2).forEach { (from, to) ->
			if (from.x == to.x) {
				for (y in from.y toward to.y) {
					cave[y][from.x] = '#'
				}
			} else if (from.y == to.y) {
				for (x in from.x toward to.x) {
					cave[from.y][x] = '#'
				}
			} else {
				error("skewed rock line")
			}
		}
	}

	val startX = 500 - caveOffset
	cave[0][startX] = '+'

	// fill up cave
	var sandCount = 0
	fill@ while (true) {
		var x = startX
		for (y in 0 until caveHeight) {
			when {
				y + 1 >= caveHeight -> break@fill
				cave[y + 1][x] == '.' -> continue
				x - 1 < 0 -> break@fill
				cave[y + 1][x - 1] == '.' -> x--
				x + 1 >= caveWidth -> break@fill
				cave[y + 1][x + 1] == '.' -> x++
				cave[y][x] == 'o' -> error("cave filled to ceiling")
				else -> {
					cave[y][x] = 'o'
					sandCount++
					continue@fill
				}
			}
		}
	}

	println("$this: $sandCount")
	//cave.forEach { println(it.joinToString("")) }
}


with("Part B") {
	// parse input
	val rocks = input.map { line ->
		line.split(" -> ")
			.map { it.split(",") }
			.map { (x, y) -> Point(x.toInt(), y.toInt()) }
	}.toMutableList()

	val caveHeight = rocks.flatten().maxOf { it.y } + 3

	// insert floor
	val floorFromX = rocks.flatten().minOf { it.x } - caveHeight
	val floorToX = rocks.flatten().maxOf { it.x } + caveHeight
	rocks.add(listOf(Point(floorFromX, caveHeight - 1), Point(floorToX, caveHeight - 1)))

	val caveOffset = rocks.flatten().minOf { it.x }
	rocks.flatten().forEach { p -> p.x -= caveOffset }

	// draw cave matrix
	val caveWidth = rocks.flatten().maxOf { it.x } + 1
	val cave = List(caveHeight) { MutableList(caveWidth) { '.' } }
	rocks.forEach { corners ->
		corners.windowed(2).forEach { (from, to) ->
			if (from.x == to.x) {
				for (y in from.y toward to.y) {
					cave[y][from.x] = '#'
				}
			} else if (from.y == to.y) {
				for (x in from.x toward to.x) {
					cave[from.y][x] = '#'
				}
			} else {
				error("skewed rock line")
			}
		}
	}

	val startX = 500 - caveOffset
	cave[0][startX] = '+'

	// fill up cave
	var sandCount = 0
	fill@ while (true) {
		var x = startX
		for (y in 0 until caveHeight) {
			when {
				y + 1 >= caveHeight -> error("cave bottomless pit")
				cave[y + 1][x] == '.' -> continue
				x - 1 < 0 -> error("cave side overflow")
				cave[y + 1][x - 1] == '.' -> x--
				x + 1 >= caveWidth -> error("cave side overflow")
				cave[y + 1][x + 1] == '.' -> x++
				cave[y][x] == 'o' -> break@fill
				else -> {
					cave[y][x] = 'o'
					sandCount++
					continue@fill
				}
			}
		}
	}

	println("$this: $sandCount")
	//cave.forEach { println(it.joinToString("")) }
}


infix fun Int.toward(to: Int): IntProgression {
	val step = if (this > to) -1 else 1
	return IntProgression.fromClosedRange(this, to, step)
}
