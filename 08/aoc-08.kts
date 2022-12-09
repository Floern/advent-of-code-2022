import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()//.first().toCharArray().asList()

/// Part A

val h = input.size
val w = input[0].length
var visibleCount = h * 2 + (w - 2) * 2

for (y in 1..h - 2) {
	for (x in 1..w - 2) {
		val curr = input[y][x].digitToInt()
		if (input[y].substring(0, x).map { it.digitToInt() }.max() < curr
			|| input[y].substring(x + 1).map { it.digitToInt() }.max() < curr
			|| input.subList(0, y).map { it[x].digitToInt() }.max() < curr
			|| input.subList(y + 1, h).map { it[x].digitToInt() }.max() < curr
		) {
			visibleCount++
		}
	}
}

println("A: " + visibleCount)

/// Part B

var maxScore = 0

for (y in 1..input.size - 2) {
	for (x in 1..input[0].length - 2) {
		val curr = input[y][x].digitToInt()
		val scoreLeft = input[y].substring(0, x).map { it.digitToInt() }.indexOfLast { it >= curr }.let {
			if (it == -1) x else x - it
		}
		val scoreRight = input[y].substring(x + 1).map { it.digitToInt() }.indexOfFirst { it >= curr }.let {
			if (it == -1) input[0].length - x - 1 else it + 1
		}
		val scoreUp = input.subList(0, y).map { it[x].digitToInt() }.indexOfLast { it >= curr }.let {
			if (it == -1) y else y - it
		}
		val scoreDown = input.subList(y + 1, input.size).map { it[x].digitToInt() }.indexOfFirst { it >= curr }.let {
			if (it == -1) input.size - y - 1 else it + 1
		}
		maxScore = maxScore.coerceAtLeast(scoreLeft * scoreRight * scoreUp * scoreDown)
	}
}

println("B: " + maxScore)