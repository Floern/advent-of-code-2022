import java.io.File
import java.util.LinkedList

val input = File("input-small.txt").readLines()

val regex =
	"""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
val blueprints = input.map { line ->
	regex.matchEntire(line)!!.let {
		Blueprint(
			it.groupValues[1].toInt(),
			mapOf(
				Resources(ore = it.groupValues[2].toInt()) to OreRobot,
				Resources(ore = it.groupValues[3].toInt()) to ClayRobot,
				Resources(ore = it.groupValues[4].toInt(), clay = it.groupValues[5].toInt()) to ObsidianRobot,
				Resources(ore = it.groupValues[6].toInt(), obsidian = it.groupValues[7].toInt()) to GeodeRobot
			)
		)
	}
}

data class Blueprint(
	val id: Int,
	val robotPlans: Map<Resources, Robot>
)

abstract class Robot(val producingResources: Resources) {
	override fun toString() = javaClass.simpleName
}
object OreRobot : Robot(Resources(ore = 1))
object ClayRobot : Robot(Resources(clay = 1))
object ObsidianRobot : Robot(Resources(obsidian = 1))
object GeodeRobot : Robot(Resources(geode = 1))

data class Resources(
	val ore: Int = 0,
	val clay: Int = 0,
	val obsidian: Int = 0,
	val geode: Int = 0
) {
	companion object {
		val NONE = Resources()
	}

	operator fun plus(other: Resources) = Resources(
		ore + other.ore,
		clay + other.clay,
		obsidian + other.obsidian,
		geode + other.geode,
	)

	operator fun minus(other: Resources) = Resources(
		ore - other.ore,
		clay - other.clay,
		obsidian - other.obsidian,
		geode - other.geode,
	)

	operator fun contains(other: Resources): Boolean {
		return other.obsidian <= obsidian && other.clay <= clay && other.ore <= ore && other.geode <= geode
	}

	override fun toString() = buildList {
		if (ore != 0) add("ore=$ore")
		if (clay != 0) add("clay=$clay")
		if (obsidian != 0) add("obsidian=$obsidian")
		if (geode != 0) add("geode=$geode")
	}.joinToString(", ", "{", "}")
}

data class State(
	val minutesLeft: Int,
	val activeRobots: List<Robot>,
	val resources: Resources = Resources.NONE,
	val newResources: Resources = Resources.NONE,
	val justBuildARobot: Boolean = false,
	val prevState: State? = null
)


with("Part A") {
	val totalQuality = blueprints.map { blueprint ->
		println(blueprint)
		println("#${blueprint.id}...")

		val startState = State(24, listOf(OreRobot))
		val queue = LinkedList<State>()
		queue += startState;

		var bestRun = startState

		while (queue.isNotEmpty()) {
			val state = queue.removeFirst()
			val availableResources = state.resources + state.newResources

			val builtRobots = mutableMapOf<Robot?, Resources>()
			if (state.minutesLeft > 1) {
				// TODO: may build multiple robots at once if the new resources allow it

				// only build a robot if we can build it with $resources + $newResources), but not with $resources only, since otherwise we could have built it earlier
				blueprint.robotPlans.forEach { cost, robot ->
					if ((cost !in state.resources || state.justBuildARobot) && cost in availableResources) {
						builtRobots.put(robot, cost)
					}
				}
				if (builtRobots.size < blueprint.robotPlans.size) {
					// can't build all robots, maybe wait and build none
					builtRobots.put(null, Resources.NONE)
				}
			}

			// collect resources from active robots
			val newResources = Resources(
				ore = state.activeRobots.sumOf { it.producingResources.ore },
				clay = state.activeRobots.sumOf { it.producingResources.clay },
				obsidian = state.activeRobots.sumOf { it.producingResources.obsidian },
				geode = state.activeRobots.sumOf { it.producingResources.geode }
			)

			if (bestRun.resources.geode < newResources.geode + availableResources.geode) {
				bestRun = State(
					minutesLeft = state.minutesLeft - 1,
					activeRobots = state.activeRobots,
					resources = availableResources + newResources,
					newResources = Resources.NONE,
					justBuildARobot = false,
					prevState = state
				)
			}

			if (state.minutesLeft > 1) {
				// generate new states
				queue += builtRobots.map { (newRobot, cost) ->
					State(
						minutesLeft = state.minutesLeft - 1,
						activeRobots = state.activeRobots.let { if (newRobot != null) it + newRobot else it },
						resources = availableResources,
						newResources = newResources - cost,
						justBuildARobot = newRobot != null,
						prevState = state
					)
				}
			}
		}

		val geodeCount = bestRun.resources.geode

		fun State.printState() {
			prevState?.printState()
			println(copy(prevState = null))
		}
		bestRun.printState()
		println("#${blueprint.id} -> $geodeCount geodes")

		val qualityLevel = blueprint.id * geodeCount
		qualityLevel
	}.sum()

	println("$this: $totalQuality")
}


with("Part B") {

	println("$this: ")
}

