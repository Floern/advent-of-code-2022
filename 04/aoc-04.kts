import java.io.File

val input = File("input.txt").readLines()

/// Part A

val result = input
	.map {
		val (a, b) = it.split(",")
			.map {
				val (f, t) = it.split("-")
				.map { it.toInt() }
				f .. t
			}
		a.first in b && a.last in b || b.first in a && b.last in a
	}
	.count { it }

println("A: " + result)

/// Part B

val resultB = input
	.map {
		val (a, b) = it.split(",")
			.map {
				val (f, t) = it.split("-")
					.map { it.toInt() }
				f .. t
			}
		a.first in b || a.last in b || b.first in a || b.last in a
	}
	.count { it }

println("B: " + resultB)
