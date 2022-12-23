import java.awt.Point
import java.io.File

val input = File("input.txt").readLines()


with("Part A") {
	val elvesPositions =
		input.flatMapIndexed { y, line ->
			line.mapIndexedNotNull { x, chr -> if (chr == '#') Point(x, y) else null }
		}

	val directions = mutableListOf(
		Point(0, -1), // N
		Point(0, +1), // S
		Point(-1, 0), // W
		Point(+1, 0), // E
	)

	val elves = elvesPositions.toMutableSet()

	repeat(10) {
		val movements = mutableMapOf<Point, Point>()

		elves.forEach { currLocation ->
			val possibleDirections = directions.filter { dir ->
				!elves.contains(currLocation + dir) &&
						!elves.contains(currLocation + dir + dir.transpose()) &&
						!elves.contains(currLocation + dir - dir.transpose())
			}
			if (possibleDirections.size < directions.size) {
				possibleDirections.firstOrNull()?.let { dir ->
					val targetLocation = currLocation + dir
					movements.put(currLocation, targetLocation)
				}
			}
		}

		directions.add(directions.removeAt(0))

		movements.forEach { (from, to) ->
			if (movements.values.count { it == to } == 1) {
				elves.remove(from)
				elves.add(to)
			}
		}
	}

	val areaWidth = elves.maxOf { it.x } - elves.minOf { it.x } + 1;
	val areaHeight = elves.maxOf { it.y } - elves.minOf { it.y } + 1;
	val emptyArea = areaWidth * areaHeight - elves.size

	println("$this: $emptyArea")
}


with("Part B") {
	val elvesPositions =
		input.flatMapIndexed { y, line ->
			line.mapIndexedNotNull { x, chr -> if (chr == '#') Point(x, y) else null }
		}

	val directions = mutableListOf(
		Point(0, -1), // N
		Point(0, +1), // S
		Point(-1, 0), // W
		Point(+1, 0), // E
	)

	val elves = elvesPositions.toMutableSet()

	var rounds = 0
	while (true) {
		var moved = false

		val targetLocations = mutableMapOf<Point, Point>()

		elves.forEach { currLocation ->
			val possibleDirections = directions.filter { dir ->
				!elves.contains(currLocation + dir) &&
						!elves.contains(currLocation + dir + dir.transpose()) &&
						!elves.contains(currLocation + dir - dir.transpose())
			}
			if (possibleDirections.size < directions.size) {
				possibleDirections.firstOrNull()?.let { dir ->
					val targetLocation = currLocation + dir
					targetLocations.put(currLocation, targetLocation)
				}
			}
		}

		directions.add(directions.removeAt(0))

		targetLocations.forEach { (from, to) ->
			if (targetLocations.values.count { it == to } == 1) {
				elves.remove(from)
				elves.add(to)
				moved = true
			}
		}

		if (moved) {
			rounds++
		} else {
			break
		}
	}

	println("$this: ${rounds + 1}")
}


operator fun Point.plus(p: Point) = Point(x + p.x, y + p.y)
operator fun Point.minus(p: Point) = Point(x - p.x, y - p.y)
fun Point.transpose() = Point(y, x)
