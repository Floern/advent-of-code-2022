import java.awt.Point
import java.io.File
import kotlin.math.abs
import kotlin.math.sign

val input = File("input.txt").readLines()

with("Part A") {
	val visitedPoints = mutableSetOf<Point>()
	val rope = listOf<Point>(Point(0, 0), Point(0, 0))

	input.forEach {
		val (dir, repeats) = it.split(' ', limit = 2)
		val dx = when (dir) {
			"R" -> 1; "L" -> -1; else -> 0
		}
		val dy = when (dir) {
			"U" -> 1; "D" -> -1; else -> 0
		}
		repeat(repeats.toInt()) {
			rope[0].translate(dx, dy)

			val sX = rope[0].x - rope[1].x
			val sY = rope[0].y - rope[1].y
			if (abs(sX) > 1 || abs(sX) >= 1 && abs(sY) > 1) {
				rope[1].x += sign(sX.toFloat()).toInt()
			}
			if (abs(sY) > 1 || abs(sY) >= 1 && abs(sX) > 1) {
				rope[1].y += sign(sY.toFloat()).toInt()
			}
			visitedPoints.add(Point(rope[1]))
		}
	}

	println(this + ": " + visitedPoints.size)
}


with("Part B") {
	val visitedPoints = mutableSetOf<Point>()
	val rope = List(10) { _ -> Point(0, 0) }

	input.forEach {
		val (dir, repeats) = it.split(' ', limit = 2)
		val dx = when (dir) {
			"R" -> 1; "L" -> -1; else -> 0
		}
		val dy = when (dir) {
			"U" -> 1; "D" -> -1; else -> 0
		}
		repeat(repeats.toInt()) {
			rope[0].translate(dx, dy)
			rope.windowed(2) { (a, b) ->
				val sX = a.x - b.x
				val sY = a.y - b.y
				if (abs(sX) > 1 || abs(sX) >= 1 && abs(sY) > 1) {
					b.x += sign(sX.toFloat()).toInt()
				}
				if (abs(sY) > 1 || abs(sY) >= 1 && abs(sX) > 1) {
					b.y += sign(sY.toFloat()).toInt()
				}
			}
			visitedPoints.add(Point(rope.last()))
		}
	}

	println(this + ": " + visitedPoints.size)
}
