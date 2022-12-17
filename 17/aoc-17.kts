import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines().first().toCharArray()

val chamberWidth = 7

val inputRockShapes = listOf(
	RockShape.parse("####"),
	RockShape.parse(".#.\n###\n.#."),
	RockShape.parse("..#\n..#\n###"),
	RockShape.parse("#\n#\n#\n#"),
	RockShape.parse("##\n##")
)


with("Part A") {
	val rockShapes = RingBuffer(inputRockShapes)
	val maxRockCount = 2022

	val chamber: Chamber = mutableListOf<MutableList<Char>>()

	var currRock = rockShapes.next()
	var currRockBottom = 3
	var currRockLeft = 2

	var rockCount = 0
	var flowIndex = 0
	while (rockCount < maxRockCount) {
		val flow = input[flowIndex]
		flowIndex = (flowIndex + 1) % input.size
		when (flow) {
			'>' -> if (chamber.canRockMoveRight(currRock, currRockLeft, currRockBottom)) currRockLeft++
			'<' -> if (chamber.canRockMoveLeft(currRock, currRockLeft, currRockBottom)) currRockLeft--
			else -> error("invalid flow")
		}

		if (!chamber.canRockMoveDown(currRock, currRockLeft, currRockBottom)) {
			chamber.addRockToStack(currRock, currRockLeft, currRockBottom)
			currRock = rockShapes.next()
			currRockLeft = 2
			currRockBottom = chamber.size + 4
			rockCount++
		}

		currRockBottom--
	}

	println("$this: ${chamber.size}")
}


with("Part B") {
	val rockShapes = RingBuffer(inputRockShapes)
	val maxRockCount = 1_000_000_000_000L

	val chamber: Chamber = mutableListOf<MutableList<Char>>()

	var currRock = rockShapes.next()
	var currRockBottom = 3
	var currRockLeft = 2

	var firstInputIterationRockCount = -1L
	var repeatingInputIterationRockCount = -1L
	var firstInputIterationChamberHeight = -1L
	var repeatingInputIterationChamberHeight = -1L
	var skippedInputIterations = -1L

	var rockCount = 0L
	var flowIndex = 0
	while (rockCount < maxRockCount) {
		val flow = input[flowIndex % input.size]
		flowIndex++
		when (flow) {
			'>' -> if (chamber.canRockMoveRight(currRock, currRockLeft, currRockBottom)) currRockLeft++
			'<' -> if (chamber.canRockMoveLeft(currRock, currRockLeft, currRockBottom)) currRockLeft--
			else -> error("invalid flow")
		}

		if (!chamber.canRockMoveDown(currRock, currRockLeft, currRockBottom)) {
			chamber.addRockToStack(currRock, currRockLeft, currRockBottom)
			currRock = rockShapes.next()
			currRockLeft = 2
			currRockBottom = chamber.size + 4
			rockCount++
		}

		currRockBottom--

		// important assumption:
		// after the second complete input iteration we're in the exact same state each time we completed any following input iteration.
		// also: the example input has to be extended to 5x for this to work.
		if (flowIndex == input.size) {
			firstInputIterationRockCount = rockCount
			firstInputIterationChamberHeight = chamber.size.toLong()
		} else if (flowIndex == input.size * 2) {
			repeatingInputIterationRockCount = rockCount - firstInputIterationRockCount
			repeatingInputIterationChamberHeight = chamber.size.toLong() - firstInputIterationChamberHeight
			skippedInputIterations = (maxRockCount - firstInputIterationRockCount) / repeatingInputIterationRockCount
			val remainingRocksToFall = (maxRockCount - firstInputIterationRockCount) % repeatingInputIterationRockCount
			rockCount = maxRockCount - remainingRocksToFall
		} else if (flowIndex == input.size * 3) {
			error("never getting so far")
		}
	}

	// at this point chamber.size = $firstInputIterationChamberHeight + heightOf(remainingRocksToFall) + 1 * repeatingInputIterationCamberHeight

	val skippedChamberHeight = repeatingInputIterationChamberHeight * (skippedInputIterations - 1)
	val totalChamberHeight = chamber.size + skippedChamberHeight

	println("$this: $totalChamberHeight")
}


/// utilities

typealias Chamber = MutableList<MutableList<Char>>

fun Chamber.addRockToStack(rock: RockShape, left: Int, bottom: Int) {
	val newStackHeight = bottom + rock.height
	val missing = (newStackHeight - size).coerceAtLeast(0)
	this.addAll(MutableList(missing) { MutableList(chamberWidth) { '.' } })
	for (y in 0 until rock.height) {
		val chamberY = bottom + y
		for (x in 0 until rock.width) {
			if (rock.matrix[y][x] == '#') {
				this[chamberY][left + x] = rock.matrix[y][x]
			}
		}
	}
}

fun Chamber.fitsRock(rock: RockShape, left: Int, bottom: Int): Boolean {
	if (bottom < 0) return false
	if (left < 0 || left + rock.width > chamberWidth) return false
	for (y in 0 until rock.height) {
		val chamberY = bottom + y
		if (chamberY >= size) break
		for (x in 0 until rock.width) {
			val chamberX = left + x
			if (this[chamberY][chamberX] == '#' && rock.matrix[y][x] == '#') {
				return false
			}
		}
	}
	return true
}

fun Chamber.canRockMoveDown(rock: RockShape, left: Int, bottom: Int) = fitsRock(rock, left, bottom - 1)
fun Chamber.canRockMoveRight(rock: RockShape, left: Int, bottom: Int) = fitsRock(rock, left + 1, bottom)
fun Chamber.canRockMoveLeft(rock: RockShape, left: Int, bottom: Int) = fitsRock(rock, left - 1, bottom)

fun Chamber.debugPrint(rock: RockShape? = null, rockLeft: Int = 0, rockBottom: Int = 0) {
	println()
	val debugChamber = this.map { it.toMutableList() }.toMutableList()
	if (rock != null) debugChamber.addRockToStack(rock, rockLeft, rockBottom)
	debugChamber.reversed().forEach { println(it.joinToString("", "|", "|")) }
	println("+-------+")
}

data class RockShape(
	// rocks are defined bottom-up, i.e. bottom line has index 0
	val matrix: List<List<Char>>,
) {
	val width = matrix[0].size
	val height = matrix.size

	companion object {
		fun parse(input: String) = RockShape(input.lines().map { it.toCharArray().toList() }.reversed())
	}
}

typealias RingBuffer<T> = LinkedList<T>

fun <T> RingBuffer<T>.next(): T {
	removeFirst().also {
		add(it)
		return it
	}
}
