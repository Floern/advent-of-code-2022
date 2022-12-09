import java.io.File
import java.net.URL

val input = File("input.txt").readLines()

/// Part A

val prioSum = input
	.map {
		if (it.length % 2 != 0) {
			error("odd length: " + it)
		}
		val a = it.substring(0, it.length / 2)
		val b = it.substring(it.length / 2)

		val intersect = a.toCharArray().intersect(b.toCharArray().asList()).first()
		//print(intersect + " ")
		val prio = if (intersect in 'a'..'z') (intersect - 'a' + 1)
		else intersect - 'A' + 27
		//println(prio)
		return@map prio
	}
	.sum()

println("A: " + prioSum)

/// Part B

val prioSumB = input
	.chunked(3)
	.map { (a, b, c) ->
		val intersect = a.toCharArray().intersect(b.toCharArray().asList()).intersect(c.toCharArray().asList()).first()
		if (intersect in 'a'..'z') (intersect - 'a' + 1)
		else intersect - 'A' + 27
	}
	.sum()

println("B: " + prioSumB)
