import java.io.File

val input = File("input.txt").readLines()


with("Part A") {
	data class Element(
		val value: Int,
		var hasBeenMoved: Boolean = false
	)

	val list = input.map { Element(it.toInt()) }.toMutableList()

	var i = 0
	while (i < list.size) {
		val elem = list[i]
		if (!elem.hasBeenMoved) {
			var newIndex = Math.floorMod(i + elem.value, list.size - 1)
			list.removeAt(i)
			elem.hasBeenMoved = true
			list.add(newIndex, elem)
			if (newIndex <= i) {
				i++
			}
		} else {
			i++
		}
	}

	val zero = list.indexOfFirst { it.value == 0 }
	val coordinates = (1..3).sumOf { list[(zero + it * 1000) % list.size].value }

	println("$this: $coordinates")
}


with("Part B") {
	data class Element(
		val originalIndex: Int,
		val value: Long
	)

	val list = input.mapIndexed { i, v -> Element(i, v.toLong() * 811589153L) }.toMutableList()

	repeat(10) {
		for (i in list.indices) {
			val oldIndex = list.indexOfFirst { it.originalIndex == i }
			val elem = list[oldIndex]
			var newIndex = Math.floorMod(oldIndex + elem.value, list.size - 1)
			list.removeAt(oldIndex)
			list.add(newIndex, elem)
		}
	}

	val zero = list.indexOfFirst { it.value == 0L }
	val coordinates = (1..3).sumOf { list[(zero + it * 1000) % list.size].value }

	println("$this: $coordinates")
}
