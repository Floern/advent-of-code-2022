import java.io.File
import java.net.URL

val input = File("input.txt").readLines()

/// Part A

val scoreSum = input
	.map { line ->
		val (elf, me) = line.split(" ")
		val elfI = when (elf) { "A" -> 0; "B" -> 1; "C" -> 2; else -> error("elf") }
		val meI = when (me) { "X" -> 0; "Y" -> 1; "Z" -> 2; else -> error("me") }
		val score = when (meI) {
			elfI -> { 3 } // draw
			(elfI + 1) % 3 -> { 6 } // win
			else -> { 0 } // lose
		} + meI + 1
		score
	}
	.sum()

println("A: " + scoreSum)

/// Part B

val scoreSumB = input
	.map { line ->
		val (elf, me) = line.split(" ")
		val elfI = when (elf) { "A" -> 0; "B" -> 1; "C" -> 2; else -> error("elf") }
		val meI = when (me) { "X" -> 0; "Y" -> 1; "Z" -> 2; else -> error("me") }
		val score = when (meI) {
			0 -> { 0 + (((elfI + 2) % 3) + 1) } // lose
			1 -> { 3 + (elfI + 1) } // draw
			else -> { 6 + (((elfI + 1) % 3) + 1) } // win
		}
		println(score)
		score
	}
	.sum()

println("B: " + scoreSumB)
