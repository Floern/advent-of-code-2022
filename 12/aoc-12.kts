import java.awt.Point
import java.io.File
import java.util.LinkedList

val grid = File("input.txt").readLines()

with("Part A") {
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

		val currChr = grid[curr.y][curr.x]
		if (currChr == 'E') {
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
			.filter {
				val nextChr = grid[it.y][it.x]
				nextChr <= currChr + 1 && nextChr != 'E' || currChr == 'S' || currChr >= 'y' && nextChr == 'E'
			}
			.filter { !visited.contains(it) && !queue.containsKey(it) }
			.forEach { queue.put(it, dist + 1) }
	}

	println("$this: $shortestPath")
}


with("Part B") {
	val end = Point(
		grid.first { it.contains('E') }.indexOf('E'),
		grid.indexOfFirst { it.contains('E') }
	)

	val visited = mutableSetOf<Point>()
	val queue = linkedMapOf<Point, Int>(end to 0)

	var shortestPath = -1
	while (queue.isNotEmpty()) {
		val curr = queue.keys.first()
		val dist = queue.remove(curr)!!

		val currChr = grid[curr.y][curr.x]
		if (currChr == 'S' || currChr == 'a') {
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
			.filter {
				val nextChr = grid[it.y][it.x]
				nextChr >= currChr - 1 && currChr != 'E' || currChr == 'E' && nextChr in 'y'..'z' || currChr in 'a'..'b' && nextChr == 'S'
			}
			.filter { !visited.contains(it) && !queue.containsKey(it) }
			.forEach { queue.put(it, dist + 1) }
	}

	println("$this: $shortestPath")
}
