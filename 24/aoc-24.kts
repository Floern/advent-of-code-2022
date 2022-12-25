import java.awt.Point
import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()


with("Part A") {
	var field = input.map { it.map { mutableListOf(it) } }

	val start = Point(1, 0)
	val end = Point(field[0].size - 2, field.size - 1)

	val states = mutableSetOf<Point>(start)
	var time = 0

	while (true) {
		time++
		field = field.progress()

		val prevStates = states.toMutableList()
		states.clear()
		prevStates.forEach { pos ->
			states.addAll(field.getValidMoves(pos))
		}

		if (end in states) {
			break
		}
	}

	println("$this: $time")
}


with("Part B") {
	var field = input.map { it.map { mutableListOf(it) } }

	val start = Point(1, 0)
	val end = Point(field[0].size - 2, field.size - 1)
	val goals = LinkedList<Point>().apply {
		add(end)
		add(start)
		add(end)
	}

	val states = mutableSetOf<Point>(start)
	var time = 0

	while (true) {
		time++
		field = field.progress()

		val prevStates = states.toMutableList()
		states.clear()
		prevStates.forEach { pos ->
			states.addAll(field.getValidMoves(pos))
		}

		if (goals.first() in states) {
			states.clear()
			states.add(goals.removeFirst())
			if (goals.isEmpty()) {
				break
			}
		}
	}

	println("$this: $time")
}


operator fun <T> List<List<T>>.get(point: Point) = get(point.y).get(point.x)

typealias Field = List<List<MutableList<Char>>>

fun Field.progress(): Field {
	val WALL = mutableListOf('#')
	val newField = this.map {
		it.map {
			if ('#' in it) WALL
			else mutableListOf()
		}
	}
	this.forEachIndexed { y, row ->
		row.forEachIndexed { x, cell ->
			if ('>' in cell) {
				if ('#' in newField[y][x + 1]) newField[y][1] += '>'
				else newField[y][x + 1] += '>'
			}
			if ('<' in cell) {
				if ('#' in newField[y][x - 1]) newField[y][newField[y].size - 2] += '<'
				else newField[y][x - 1] += '<'
			}
			if ('^' in cell) {
				if ('#' in newField[y - 1][x]) newField[newField.size - 2][x] += '^'
				else newField[y - 1][x] += '^'
			}
			if ('v' in cell) {
				if ('#' in newField[y + 1][x]) newField[1][x] += 'v'
				else newField[y + 1][x] += 'v'
			}
		}
	}
	return newField
}

fun Field.getValidMoves(pos: Point): List<Point> {
	val newPositions = mutableListOf<Point>()

	if (this[pos].isEmpty()) newPositions += pos

	val left = Point(pos.x - 1, pos.y)
	if (left.x >= 0 && this[left].isEmpty()) newPositions += left

	val right = Point(pos.x + 1, pos.y)
	if (right.x < this[0].size && this[right].isEmpty()) newPositions += right

	val up = Point(pos.x, pos.y - 1)
	if (up.y >= 0 && this[up].isEmpty()) newPositions += up

	val down = Point(pos.x, pos.y + 1)
	if (down.y < this.size && this[down].isEmpty()) newPositions += down

	return newPositions
}
