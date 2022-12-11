import java.io.File
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.util.LinkedList

val input = File("input.txt").readLines()

with("Part A") {
	data class Monkey(
		val items: LinkedList<Int>,
		val operation: (Int) -> Int,
		val testTarget: (Int) -> Int,
		var inspectCount: Int = 0
	)

	val monkeys = input.chunked(7).map { monkeyLines ->
		val items = monkeyLines[1].substring(18).split(", ").map { it.toInt() }
		val (op, param) = monkeyLines[2].substring(19 + 4).split(" ")
		val operation: (Int) -> Int = when (op) {
			"+" -> if (param == "old") { { lv -> lv + lv } } else { { lv -> lv + param.toInt() } }
			"*" -> if (param == "old") { { lv -> lv * lv } } else { { lv -> lv * param.toInt() } }
			else -> error("unknown op")
		}
		val testDivider = monkeyLines[3].substring(21).toInt()
		val testTrueMonkey = monkeyLines[4].substring(29).toInt()
		val testFalseMonkey = monkeyLines[5].substring(30).toInt()
		val testTarget: (Int) -> Int = { lv -> if (lv % testDivider == 0) testTrueMonkey else testFalseMonkey }

		Monkey(items.toCollection(LinkedList()), operation, testTarget)
	}

	repeat(20) { round ->
		monkeys.forEach { monkey ->
			while (monkey.items.isNotEmpty()) {
				val lv = monkey.items.removeFirst()
				val newLv = monkey.operation(lv) / 3
				val targetMonkey = monkey.testTarget(newLv)
				monkeys[targetMonkey].items.add(newLv)
				monkey.inspectCount++
			}
		}
	}

	val monkeyBiz = monkeys
		.sortedByDescending { monkey -> monkey.inspectCount }
		.take(2)
		.fold(1) { acc, monkey -> acc * monkey.inspectCount }

	println(this + ": " + monkeyBiz)
}


with("Part B") {
	data class Monkey(
		val items: LinkedList<BigInteger>,
		val operation: (BigInteger) -> BigInteger,
		val testTarget: (BigInteger) -> Int,
		val testDivider: BigInteger,
		var inspectCount: Int = 0
	)

	val monkeys = input.chunked(7).map { monkeyLines ->
		val items = monkeyLines[1].substring(18).split(", ").map { BigInteger(it) }
		val (op, param) = monkeyLines[2].substring(19 + 4).split(" ")
		val operation: (BigInteger) -> BigInteger = when (op) {
			"+" -> if (param == "old") { { lv -> lv + lv } } else { { lv -> lv + BigInteger(param) } }
			"*" -> if (param == "old") { { lv -> lv * lv } } else { { lv -> lv * BigInteger(param) } }
			else -> error("unknown op")
		}
		val testDivider = BigInteger(monkeyLines[3].substring(21))
		val testTrueMonkey = monkeyLines[4].substring(29).toInt()
		val testFalseMonkey = monkeyLines[5].substring(30).toInt()
		val testTarget: (BigInteger) -> Int = { lv -> if (lv % testDivider == ZERO) testTrueMonkey else testFalseMonkey }

		Monkey(items.toCollection(LinkedList()), operation, testTarget, testDivider)
	}

	val globalMod = monkeys
		.map { it.testDivider }
		.fold(BigInteger.ONE) { acc, div -> acc * div }

	repeat(10_000) { round ->
		monkeys.forEach { monkey ->
			while (monkey.items.isNotEmpty()) {
				val lv = monkey.items.removeFirst()
				val newLv = monkey.operation(lv) % globalMod
				val targetMonkey = monkey.testTarget(newLv)
				monkeys[targetMonkey].items.add(newLv)
				monkey.inspectCount++
			}
		}
	}

	val monkeyBiz = monkeys
		.sortedByDescending { monkey -> monkey.inspectCount }
		.take(2)
		.fold(BigInteger.ONE) { acc, monkey -> acc * monkey.inspectCount.toBigInteger() }

	println(this + ": " + monkeyBiz)
}
