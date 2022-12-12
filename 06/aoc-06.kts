import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines().first().toCharArray().asList()

with("Part A") {
	val s = 4
	val result = input.windowed(s).indexOfFirst { it.distinct().size == s } + s
	println("$this: $result")
}

with("Part B") {
	val s = 14
	val result = input.windowed(s).indexOfFirst { it.distinct().size == s } + s
	println("$this: $result")
}
