import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines().first().toCharArray().asList()

/// Part A

var resultA = 0
val s = 4
for (i in (s)..input.size) {
	if (input.subList(i - s, i).toSet().size == s) {
		resultA = i
		break
	}
}

println("A: " + resultA)

/// Part B

var resultB = 0
val sB = 14
for (i in (sB)..input.size) {
	if (input.subList(i - sB, i).toSet().size == sB) {
		resultB = i
		break
	}
}

println("B: " + resultB)
