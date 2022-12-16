import java.awt.Point
import java.io.File
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

val input = File("input.txt").readLines()

/*
with("Part A") {
	val regex = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
	val cave = input.map { line ->
		regex.matchEntire(line)!!.let {
			val room = it.groups[1]!!.value
			val flowRate = it.groups[2]!!.value.toInt()
			val tunnels = it.groups[3]!!.value.split(", ")
			room to Room(flowRate, tunnels)
		}
	}.toMap()

	fun tick(
		minutesLeft: Int,
		exitedRoom: String?,
		enteredRoom: String,
		openValves: Set<String> = emptySet(),
		usedEdges: Set<String> = emptySet()
	): Int {
		val currFlowRate = openValves.sumOf { cave[it]!!.flowRate }
		if (minutesLeft <= 1) {
			return currFlowRate
		}
		if (minutesLeft > 10) {
			println("$exitedRoom -> $enteredRoom | $minutesLeft mins: $currFlowRate released in tick")
		}
		if (minutesLeft < 20 && currFlowRate < 10) {
			return -1000
		}
		if (minutesLeft < 10 && currFlowRate < 20) {
			return -1000
		}

		val flowRate = cave[enteredRoom]!!.flowRate
		val tunnels = cave[enteredRoom]!!.tunnels

		return (buildList<Int> {
			if (flowRate > 0 && enteredRoom !in openValves) {
				add(tick(minutesLeft - 1, null, enteredRoom, openValves + enteredRoom, usedEdges))
			}
			if (minutesLeft == 30) { tunnels.parallelStream() } else { tunnels.stream() }
				.forEach { nextRoom ->
					val edge = enteredRoom + nextRoom
					if (nextRoom != exitedRoom && edge !in usedEdges) {
						add(tick(minutesLeft - 1, enteredRoom, nextRoom, openValves, usedEdges + edge))
					}
				}
		}.maxOrNull() ?: 0) + currFlowRate
	}

	val totalReleased = tick(30, null, "AA")

	println("$this: $totalReleased")
}*/


with("Part B") {
	val regex = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
	val cave = input.map { line ->
		regex.matchEntire(line)!!.let {
			val room = it.groups[1]!!.value
			val flowRate = it.groups[2]!!.value.toInt()
			val tunnels = it.groups[3]!!.value.split(", ")
			room to Room(flowRate, tunnels)
		}
	}.toMap()

	val MINUTES = 26

	val maxFlowRate = cave.map { (_, r) -> r.flowRate }.sum()
	println("max flow rate: $maxFlowRate")

	val (topValve, topValveFlowRate) = cave.maxBy { (_, r) -> r.flowRate }
	println("topValve $topValve with flowRate $topValveFlowRate")

	val visited = mutableSetOf<String>().apply { add("AA"); }
	val queue = LinkedList<String>().apply { add("AA"); add("NEXTROUND"); }
	var thresholdRound = 0
	var thresholdRoundFlowRate = 0
	var found = false
	while (queue.isNotEmpty()) {
		val room = queue.removeFirst()
		if (room == "NEXTROUND") {
			if (found) {
				break
			}
			queue.add(room)
			thresholdRound++
		} else if (room == topValve) {
			println("min rounds: $thresholdRound")
			println("got to $room: ${cave!![room]}")
			thresholdRoundFlowRate = cave[room]!!.flowRate
			found = true
		} else {
			cave[room]!!.tunnels.filter { it !in visited }.forEach {
				visited.add(it)
				queue.add(it)
			}
		}
	}
	//return@with

	fun tick(
		minutesLeft: Int,
		exitedRoomMe: String?,
		enteredRoomMe: String,
		exitedRoomPhant: String?,
		enteredRoomPhant: String,
		openValves: Set<String> = emptySet(),
		usedEdgesMe: Set<String> = emptySet(),
		usedEdgesPhant: Set<String> = emptySet()
	): Int {
		val currFlowRate = openValves.sumOf { cave[it]!!.flowRate }
		if (minutesLeft >= 25) {
			println("$exitedRoomMe -> $enteredRoomMe && $exitedRoomPhant -> $enteredRoomPhant | $minutesLeft mins: $currFlowRate released in tick")
		}
		if (minutesLeft == 1 || currFlowRate == maxFlowRate) {
			return currFlowRate * minutesLeft
		}
		if (minutesLeft < MINUTES - thresholdRound && currFlowRate < thresholdRoundFlowRate) {
			return -10000
		}
		if (minutesLeft < 10 && currFlowRate < 40) {
			return -10000
		}

		val flowRateMe = cave[enteredRoomMe]!!.flowRate
		val tunnelsMe = cave[enteredRoomMe]!!.tunnels
		val flowRatePhant = cave[enteredRoomPhant]!!.flowRate
		val tunnelsPhant = cave[enteredRoomPhant]!!.tunnels

		val options = buildList<Int> {
			if (flowRateMe > 0 && enteredRoomMe !in openValves) {
				// me open valve
				if (flowRatePhant > 0 && enteredRoomPhant !in openValves && enteredRoomPhant != enteredRoomMe) {
					// me & phant open valve
					add(
						tick(
							minutesLeft - 1,
							null,
							enteredRoomMe,
							null,
							enteredRoomPhant,
							openValves + enteredRoomMe + enteredRoomPhant,
							usedEdgesMe,
							usedEdgesPhant
						)
					)
				} else {
					// only me opens valve
					tunnelsPhant.forEach { nextRoomPhant ->
						val edgePhant = enteredRoomPhant + nextRoomPhant
						if (nextRoomPhant != exitedRoomPhant && edgePhant !in usedEdgesPhant) {
							add(
								tick(
									minutesLeft - 1,
									null,
									enteredRoomMe,
									enteredRoomPhant,
									nextRoomPhant,
									openValves + enteredRoomMe,
									usedEdgesMe,
									usedEdgesPhant + edgePhant
								)
							)
						}
					}
				}
			} else if (flowRatePhant > 0 && enteredRoomPhant !in openValves) {
				// only phant opens valve
				tunnelsMe.forEach { nextRoomMe ->
					val edgeMe = enteredRoomMe + nextRoomMe
					if (nextRoomMe != exitedRoomMe && edgeMe !in usedEdgesMe) {
						add(
							tick(
								minutesLeft - 1,
								enteredRoomMe,
								nextRoomMe,
								null,
								enteredRoomPhant,
								openValves + enteredRoomPhant,
								usedEdgesMe + edgeMe,
								usedEdgesPhant
							)
						)
					}
				}
			}

			// nobody opens valve
			val nextRoomCombos = mutableSetOf<Pair<String, String>>()
			tunnelsMe.forEach { nextRoomMe ->
				val edgeMe = enteredRoomMe + nextRoomMe
				if (nextRoomMe != exitedRoomMe && edgeMe !in usedEdgesMe) {
					tunnelsPhant.forEach { nextRoomPhant ->
						val edgePhant = enteredRoomPhant + nextRoomPhant
						if (nextRoomPhant != exitedRoomPhant && edgePhant !in usedEdgesPhant) {
							val edgeComboA = Pair(nextRoomMe, nextRoomPhant)
							val edgeComboB = Pair(nextRoomPhant, nextRoomMe)
							if (enteredRoomMe != enteredRoomPhant || (edgeComboA !in nextRoomCombos && edgeComboB !in nextRoomCombos)) {
								nextRoomCombos += edgeComboA
							}
						}
					}
				}
			}
			val completed = AtomicInteger()
			if (minutesLeft >= MINUTES - 2) {
				nextRoomCombos.parallelStream()
			} else {
				nextRoomCombos.stream()
			}
				.forEach { (nextRoomMe, nextRoomPhant) ->
					val edgeMe = enteredRoomMe + nextRoomMe
					val edgePhant = enteredRoomPhant + nextRoomPhant
					add(
						tick(
							minutesLeft - 1,
							enteredRoomMe,
							nextRoomMe,
							enteredRoomPhant,
							nextRoomPhant,
							openValves,
							usedEdgesMe + edgeMe,
							usedEdgesPhant + edgePhant
						)
					)
					if (minutesLeft == MINUTES) {
						val total = nextRoomCombos.size
						val c = completed.incrementAndGet()
						println("finished $c/$total: $enteredRoomMe -> $nextRoomMe && $enteredRoomPhant -> $nextRoomPhant")
					}
				}
		}
		val collectionFlowRate = options.filterNotNull().maxOrNull() ?: 0
		return collectionFlowRate + currFlowRate
	}

	val totalReleased = tick(MINUTES, null, "AA", null, "AA")

	println("$this: $totalReleased")
}


data class Room(
	val flowRate: Int,
	val tunnels: List<String>,
)
