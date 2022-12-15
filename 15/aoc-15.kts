import java.awt.Point
import java.io.File
import kotlin.math.abs

val input = File("input.txt").readLines()
val rowY = 2_000_000
val searchSpace = 4_000_000


with("Part A") {
	val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
	val sensations = input.map { line ->
		regex.matchEntire(line)!!.let {
			Point(it.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()) to
					Point(it.groups[3]!!.value.toInt(), it.groups[4]!!.value.toInt())
		}
	}.toMap()

	val ranges = sensations.mapNotNull { (snr, bcn) ->
		val radius = abs(snr.x - bcn.x) + abs(snr.y - bcn.y)
		val distToRow = abs(snr.y - rowY)
		if (distToRow <= radius) {
			val radiusAtRow = radius - distToRow
			(snr.x - radiusAtRow)..(snr.x + radiusAtRow)
		} else {
			null
		}
	}

	val objectsOnRow = sensations.flatMap { (s, b) -> listOf(s, b) }.filter { it.y == rowY }.distinct().count()
	val coveredAtRow = ranges.flatten().distinct().size - objectsOnRow

	println("$this: $coveredAtRow")
}


with("Part B") {
	val regex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
	val sensations = input.map { line ->
		regex.matchEntire(line)!!.let {
			Point(it.groups[1]!!.value.toInt(), it.groups[2]!!.value.toInt()) to
					Point(it.groups[3]!!.value.toInt(), it.groups[4]!!.value.toInt())
		}
	}.toMap()

	val tuningFreq = run {
		val L = -1
		val R = searchSpace + 1
		for (row in 0..searchSpace) {
			sensations
				.map { (snr, bcn) ->
					val radius = abs(snr.x - bcn.x) + abs(snr.y - bcn.y)
					val distToRow = abs(snr.y - row)
					val radiusAtRow = radius - distToRow
					(snr.x - radiusAtRow).coerceIn(L, R)..(snr.x + radiusAtRow).coerceIn(L, R)
				}
				.filter { !it.isEmpty() }
				.sortedBy { it.start }
				.plusElement(R..R)
				.fold(L) { scanLine, range ->
					if (scanLine < range.start - 1) return@run (scanLine + 1) * 4_000_000L + row
					Math.max(scanLine, range.last)
				}
		}
		error("no uncovered spot")
	}

	println("$this: $tuningFreq")
}
