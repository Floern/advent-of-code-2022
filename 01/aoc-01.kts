import java.io.File
import java.net.URL

val input = File("input.txt").readText()

/// Part A

val maxSum = input.splitToSequence("\n\n")
	.maxOf { section ->
		section.lineSequence()
			.filter { it.isNotBlank() }
			.map { it.toInt() }
			.sum()
	}

println("A: " + maxSum)

/// Part B

val sumTop3 = input.splitToSequence("\n\n")
	.map { section ->
		section.lineSequence()
			.filter { it.isNotBlank() }
			.map { it.toInt() }
			.sum()
	}
	.sortedDescending()
	.take(3)
	.sum()

println("B: " + sumTop3)
