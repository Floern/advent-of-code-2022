import java.io.File
import java.util.LinkedList
import kotlin.math.cos

val input = File("input.txt").readLines()

val regex =
	"""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
val blueprints = input.map { line ->
	regex.matchEntire(line)!!.let {
		Blueprint(
			it.groupValues[1].toInt(),
			mapOf(
				OreRobot to Resources(ore = it.groupValues[2].toInt()),
				ClayRobot to Resources(ore = it.groupValues[3].toInt()),
				ObsidianRobot to Resources(ore = it.groupValues[4].toInt(), clay = it.groupValues[5].toInt()),
				GeodeRobot to Resources(ore = it.groupValues[6].toInt(), obsidian = it.groupValues[7].toInt())
			)
		)
	}
}

data class Blueprint(
	val id: Int,
	val robotPlans: Map<Robot, Resources>
)

abstract class Robot(val productionRate: Resources)
object OreRobot : Robot(Resources(ore = 1))
object ClayRobot : Robot(Resources(clay = 1))
object ObsidianRobot : Robot(Resources(obsidian = 1))
object GeodeRobot : Robot(Resources(geode = 1))
object NoRobot : Robot(Resources.NONE)

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

	operator fun times(other: Resources) = Resources(
		ore * other.ore,
		clay * other.clay,
		obsidian * other.obsidian,
		geode * other.geode,
	)

	operator fun contains(other: Resources): Boolean {
		return other.obsidian <= obsidian && other.clay <= clay && other.ore <= ore && other.geode <= geode
	}
}

data class State(
	val minutesLeft: Int,
	val currentProductionRate: Resources,
	val resources: Resources = Resources.NONE,
	val newResources: Resources = Resources.NONE,
	val justBuiltARobot: Boolean = false,
)

fun computeBestGeodeCount(blueprint: Blueprint, state: State): Int {
	val availableResources = state.resources + state.newResources

	val robotsToBuild = mutableMapOf<Robot, Resources>()
	if (state.minutesLeft > 1) {
		blueprint.robotPlans.forEach { (robot, cost) ->
			if (cost in availableResources // can afford robot
				&& (cost !in state.resources || state.justBuiltARobot) // couldn't have built the minute before
			) {
				robotsToBuild.put(robot, cost)
			}
		}
		if (robotsToBuild.size < blueprint.robotPlans.size) {
			robotsToBuild.put(NoRobot, Resources.NONE)
		}
		robotsToBuild.get(GeodeRobot)?.let { cost ->
			robotsToBuild.clear()
			robotsToBuild.put(GeodeRobot, cost)
		}
	}

	val newMinutesLeft = state.minutesLeft - 1

	if (newMinutesLeft > 0) {
		return robotsToBuild.map { (newRobot, cost) ->
			computeBestGeodeCount(
				blueprint,
				State(
					minutesLeft = newMinutesLeft,
					currentProductionRate = state.currentProductionRate + newRobot.productionRate,
					resources = availableResources - cost,
					newResources = state.currentProductionRate,
					justBuiltARobot = newRobot !== NoRobot
				)
			)
		}.max()
	} else {
		return availableResources.geode + state.currentProductionRate.geode
	}
}


with("Part A") {
	val totalQuality = blueprints.parallelStream()
		.map { blueprint ->
			val startState = State(24, OreRobot.productionRate)
			val geodeCount = computeBestGeodeCount(blueprint, startState)

			val qualityLevel = blueprint.id * geodeCount
			qualityLevel
		}
		.iterator()
		.asSequence()
		.sum()

	println("$this: $totalQuality")
}


with("Part B") {
	val product = blueprints.take(3)
		.parallelStream()
		.map { blueprint ->
			val startState = State(32, OreRobot.productionRate)
			val geodeCount = computeBestGeodeCount(blueprint, startState)
			geodeCount
		}
		.iterator()
		.asSequence()
		.fold(1) { a, b -> a * b }

	println("$this: $product")
}

