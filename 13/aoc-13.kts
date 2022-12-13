import java.io.File
import java.util.Stack

val input = File("input.txt").readLines()

fun String.parse(): L {
	var i = 0
	val stack = Stack<L>()
	while (i < length) {
		when {
			get(i) == '[' -> stack.push(L())
			get(i) == ']' -> {
				val e = stack.pop()
				if (stack.isNotEmpty()) stack.peek().l.add(e) else return e
			}
			get(i).isDigit() -> {
				val digits = substring(i, indexOfAny(",[]".toCharArray(), i))
				stack.peek().l.add(V(digits.toInt()))
				i += digits.length - 1
			}
			else -> { }
		}
		i++
	}
	error("never gets here")
}

sealed interface E : Comparable<E> {
	val size: Int
	operator fun get(i: Int): E
	override fun compareTo(other: E): Int {
		if (this is V && other is V) {
			return this.v - other.v
		} else {
			for (i in 0 until minOf(this.size, other.size)) {
				this[i].compareTo(other[i]).let { if (it != 0) return it }
			}
			return this.size - other.size
		}
	}
}

data class V(val v: Int) : E {
	override val size = 1
	override fun get(i: Int) = this
}

data class L(val l: MutableList<E> = mutableListOf()) : E {
	override val size get() = l.size
	override fun get(i: Int) = l[i]
}


with("Part A") {
	val idxSum = input.chunked(3)
		.map { (l, r) -> l.parse() < r.parse() }
		.mapIndexed { i, ok -> if (ok) i + 1 else 0 }
		.sum()

	println("$this: $idxSum")
}


with("Part B") {
	val divPackets = listOf("[[2]]", "[[6]]").map { it.parse() }
	val divKey = input.filter { it.isNotEmpty() }
		.map { it.parse() }
		.plus(divPackets)
		.sorted()
		.mapIndexed { i, e -> if (e in divPackets) i + 1 else 1 }
		.fold(1) { acc, v -> acc * v }

	println("$this: $divKey")
}
