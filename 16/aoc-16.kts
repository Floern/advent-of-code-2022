import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()

val regex = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
val inputGraph = input.map { line ->
	regex.matchEntire(line)!!.let {
		val valveName = it.groups[1]!!.value
		val flowRate = it.groups[2]!!.value.toInt()
		val tunnels = it.groups[3]!!.value.split(", ")
		valveName to RawValve(valveName, flowRate, tunnels)
	}
}.toMap()

// generate a simplified graph: complete graph only containing the functional valves (and AA) and the distances between them
val graph = inputGraph.values
	.filter { it.flowRate > 0 || it.name == "AA" }
	.map { Valve(it.name, it.flowRate, mutableMapOf()) }
val BFS_LEVEL_MARKER = "$"
graph.forEach { valve ->
	val visited = mutableSetOf<String>(valve.name)
	val queue = LinkedList<String>().apply { add(valve.name); add(BFS_LEVEL_MARKER); }
	var depth = 0
	while (queue.size > 1) {
		val valveName = queue.removeFirst()
		if (valveName == BFS_LEVEL_MARKER) {
			depth++
			queue.add(BFS_LEVEL_MARKER)
		} else {
			val targetValve = graph.firstOrNull { it.name == valveName }?.takeIf { it.name != valve.name }
			if (targetValve != null) {
				valve.tunnels.put(targetValve, depth + 1)
			}
			inputGraph[valveName]!!.tunnels.filter { it !in visited }.forEach {
				visited.add(it)
				queue.add(it)
			}
		}
	}
}
// print graph
graph.forEach { node ->
	println(node)
}
println()


data class RawValve(
	val name: String,
	val flowRate: Int,
	val tunnels: List<String>,
)

data class Valve(
	val name: String,
	val flowRate: Int,
	val tunnels: MutableMap<Valve, Weight>,
) {
	override fun equals(other: Any?): Boolean = (other as? Valve)?.name == name
	override fun hashCode(): Int = name.hashCode()
	override fun toString(): String {
		return "$name (flr=$flowRate) -> " + tunnels.map { (n, w) -> "$w>" + n.name }.sorted().joinToString()
	}
	fun shortString() = "$name (flr=$flowRate)"
}

typealias Weight = Int

inline fun <K, V> Map<K, V>.addIfEmpty(value: () -> Pair<K, V>): Map<K, V> = if (isEmpty()) {
	this + value()
} else {
	this
}


with("Part A") {
	fun step(
		minutesLeft: Int,
		currentNode: Valve,
		opened: Set<String>,
		currentFlowRate: Int = 0
	): Int {
		val results = currentNode.tunnels
			.filter { (target, _) -> target.name !in opened }
			.filter { (target, weight) -> weight <= minutesLeft }
			.map { (target, weight) ->
				step(minutesLeft - weight, target, opened + target.name, currentFlowRate + target.flowRate) +
						currentFlowRate * weight
			}
		val bestRemaningCumulativeFlow = results.maxOrNull() ?: (minutesLeft * currentFlowRate)
		return bestRemaningCumulativeFlow
	}

	var startNode = graph.first { it.name == "AA" }
	val cumulativeFlow = step(30, startNode, setOf(startNode.name))

	println("$this: $cumulativeFlow")
}


val NO_VALVE_LEFT = Valve("", 0, mutableMapOf())

// takes a few minutes...
with("Part B") {
	var startNode = graph.first { it.name == "AA" }
	val cumulativeFlow = stepMe(26, startNode, startNode)

	println("$this: $cumulativeFlow")
}

fun stepMe(
	minutesLeft: Int,
	targetValveMe: Valve,
	targetValvePhant: Valve,
	opened: Set<Valve> = emptySet(),
	currentFlowRate: Int = 0,
	distanceToTargetMe: Int = 0,
	distanceToTargetPhant: Int = 0,
	cumulativeFlow: Int = 0
): Int {
	if (minutesLeft == 0) return cumulativeFlow

	if (distanceToTargetMe > 1) {
		return stepPhant(
			minutesLeft,
			targetValveMe,
			targetValvePhant,
			opened,
			currentFlowRate,
			distanceToTargetMe - 1,
			distanceToTargetPhant,
			cumulativeFlow
		)
	} else {
		// open valve
		val newFlowRate = currentFlowRate + targetValveMe.flowRate
		val newOpened = opened + targetValveMe
		return targetValveMe.tunnels
			.filter { (target, _) -> target !in opened && target != targetValvePhant }
			.filter { (target, weight) -> weight <= minutesLeft }
			.addIfEmpty { noValveLeft() to 999 }
			.map { (newTarget, weight) ->
				stepPhant(
					minutesLeft,
					newTarget,
					targetValvePhant,
					newOpened,
					newFlowRate,
					weight,
					distanceToTargetPhant,
					cumulativeFlow
				)
			}
			.max()
	}
}

fun stepPhant(
	minutesLeft: Int,
	targetValveMe: Valve,
	targetValvePhant: Valve,
	opened: Set<Valve>,
	currentFlowRate: Int,
	distanceToTargetMe: Int,
	distanceToTargetPhant: Int,
	cumulativeFlow: Int
): Int {
	if (distanceToTargetPhant > 1) {
		return stepMe(
			minutesLeft - 1,
			targetValveMe,
			targetValvePhant,
			opened,
			currentFlowRate,
			distanceToTargetMe,
			distanceToTargetPhant - 1,
			cumulativeFlow + currentFlowRate
		)
	} else {
		// open valve
		val newFlowRate = currentFlowRate + targetValvePhant.flowRate
		val newOpened = opened + targetValvePhant
		return targetValvePhant.tunnels
			.filter { (target, _) -> target !in opened && target != targetValveMe }
			.filter { (target, weight) -> weight <= minutesLeft }
			.addIfEmpty { noValveLeft() to 999 }
			.map { (newTarget, weight) ->
				val result = stepMe(
					minutesLeft - 1,
					targetValveMe,
					newTarget,
					newOpened,
					newFlowRate,
					distanceToTargetMe,
					weight,
					cumulativeFlow + newFlowRate
				)
				result
			}
			.max()
	}
}

inline fun noValveLeft() = Valve("", 0, mutableMapOf())
