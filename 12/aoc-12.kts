import java.awt.Point
import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()

with("Part A") {
	val grid = input
	println(grid)

	val start = Point(
		grid.first { it.contains('S') }.indexOf('S'),
		grid.indexOfFirst { it.contains('S') }
	)

	val visited = mutableSetOf<Point>()
	val queue = linkedMapOf<Point, Int>(start to 0)

	var shortestPath = -1
	while (queue.isNotEmpty()) {
		val curr = queue.keys.first()
		val dist = queue.remove(curr)!!
		println(curr.toString() + ": " + dist)

		if (grid[curr.y][curr.x] == 'E') {
			shortestPath = dist
			break
		}
		visited.add(curr)

		buildList<Point> {
			if (curr.x > 0) add(Point(curr.x - 1, curr.y))
			if (curr.y > 0) add(Point(curr.x, curr.y - 1))
			if (curr.x < grid.first().length - 1) add(Point(curr.x + 1, curr.y))
			if (curr.y < grid.size - 1) add(Point(curr.x, curr.y + 1))
		}
			.filter { grid[it.y][it.x] <= grid[curr.y][curr.x] + 1 || grid[curr.y][curr.x] == 'S' || grid[it.y][it.x] == 'E' }
			.filter { !visited.contains(it) && !queue.containsKey(it) }
			.forEach { queue.put(it, dist + 1) }
	}

	println(this + ": " + shortestPath)
}


with("Part B") {
	val grid = input
	println(grid)

	val starts = grid.mapIndexed { y, row ->
		row.mapIndexedNotNullTo(mutableListOf<Point>()) { x, v -> if (v == 'S' || v == 'a') Point(x, y) else null }
	}.flatten().toList()

	val trailLength = starts.minOf { start ->
		val visited = mutableSetOf<Point>()
		val queue = linkedMapOf<Point, Int>(start to 0)

		var shortestPath = Int.MAX_VALUE
		while (queue.isNotEmpty()) {
			val curr = queue.keys.first()
			val dist = queue.remove(curr)!!
			println(curr.toString() + ": " + dist)

			if (grid[curr.y][curr.x] == 'E') {
				shortestPath = dist
				break
			}
			visited.add(curr)

			buildList<Point> {
				if (curr.x > 0) add(Point(curr.x - 1, curr.y))
				if (curr.y > 0) add(Point(curr.x, curr.y - 1))
				if (curr.x < grid.first().length - 1) add(Point(curr.x + 1, curr.y))
				if (curr.y < grid.size - 1) add(Point(curr.x, curr.y + 1))
			}
				.filter { grid[it.y][it.x] <= grid[curr.y][curr.x] + 1 || grid[curr.y][curr.x] == 'S' || grid[it.y][it.x] == 'E' }
				.filter { !visited.contains(it) && !queue.containsKey(it) }
				.forEach { queue.put(it, dist + 1) }
		}
		shortestPath
	}

	println(this + ": " + trailLength)
}
