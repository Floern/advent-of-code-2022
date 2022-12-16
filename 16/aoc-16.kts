import java.awt.Point
import java.io.File
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

val input = File("input.txt").readLines()

val regex = """Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)""".toRegex()
val cave = input.map { line ->
	regex.matchEntire(line)!!.let {
		val room = it.groups[1]!!.value
		val flowRate = it.groups[2]!!.value.toInt()
		val tunnels = it.groups[3]!!.value.split(", ")
		room to Room(room, flowRate, tunnels)
	}
}.toMap()

val graph = cave.values
	.filter { it.flowRate > 0 || it.name == "AA" }
	.map { RoomNode(it.name, it.flowRate, mutableMapOf()) }
graph.forEach { node ->
	val visited = mutableSetOf<String>(node.name)
	val depthMarker = "depthMarker"
	val queue = LinkedList<String>().apply { add(node.name); add(depthMarker); }
	var depth = 0
	while (queue.size > 1) {
		val roomName = queue.removeFirst()
		if (roomName == depthMarker) {
			depth++
			queue.add(depthMarker)
		} else {
			val targetNode = graph.firstOrNull { it.name == roomName }?.takeIf { it.name != node.name }
			if (targetNode != null) {
				node.tunnels.put(targetNode, Weight(depth + 1, targetNode.flowRate))
			}
			cave[roomName]!!.tunnels.filter { it !in visited }.forEach {
				visited.add(it)
				queue.add(it)
			}
		}
	}
}
graph.forEach { node ->
	println(node)
}
println()


with("Part A") {
	val MINUTES = 30

	fun step(
		currentNode: RoomNode,
		opened: Set<String>,
		currentFlowRate: Int = 0,
		minutesLeft: Int = MINUTES
	): Int {
		val results = currentNode.tunnels
			.filter { (target, _) -> target.name !in opened }
			.filter { (target, weight) -> weight.raw <= minutesLeft }
			.map { (target, weight) ->
				val remaningCumulativeFlow = step(target, opened + target.name, currentFlowRate + target.flowRate, minutesLeft - weight.raw) +
						currentFlowRate * weight.raw
				remaningCumulativeFlow
			}
		val bestRemaningCumulativeFlow = results.maxOrNull() ?: (minutesLeft * currentFlowRate)
		return bestRemaningCumulativeFlow
	}

	var startNode = graph.first { it.name == "AA" }
	val cumulativeFlow = step(startNode, setOf(startNode.name))

	println("$this: $cumulativeFlow")
}


with("Part B") {

}


data class Room(
	val name: String,
	val flowRate: Int,
	val tunnels: List<String>,
)

data class RoomNode(
	val name: String,
	val flowRate: Int,
	val tunnels: MutableMap<RoomNode, Weight>,
) {
	override fun equals(other: Any?): Boolean = (other as? RoomNode)?.name == name
	override fun hashCode(): Int = name.hashCode()
	override fun toString(): String {
		return "$name ($flowRate) -> " + tunnels.map { (n, w) -> "${w.raw}>" + n.name }.sorted().joinToString()
	}
}

data class Weight(val raw: Int, val targetFlowRate: Int) {
	fun adjusted(minutesRemaining: Int): Int {
		return raw - targetFlowRate * (minutesRemaining - raw)
	}
}
