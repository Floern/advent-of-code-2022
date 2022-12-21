import java.io.File

val input = File("input.txt").readLines()


with("Part A") {
	val monkeys = input.associate { it.split(": ").let { it[0] to it[1] } }

	fun computeResult(monkey: String): Long {
		val expr = monkeys[monkey]!!
		if (expr.all { it.isDigit() }) {
			return expr.toLong()
		} else {
			return expr.split(" ").let { (m1, op, m2) ->
				val l = computeResult(m1)
				val r = computeResult(m2)
				when (op) {
					"+" -> l + r
					"-" -> l - r
					"*" -> l * r
					"/" -> l / r
					else -> error("unknown op")
				}
			}
		}
	}

	val root = computeResult("root")

	println("$this: $root")
}


with("Part B") {
	val monkeys = input.associate { it.split(": ").let { it[0] to it[1] } }.toMap(HashMap<String, String?>())

	fun computeResult(monkey: String): Long? {
		val expr = monkeys[monkey] ?: return null
		if (expr.all { it.isDigit() }) {
			return expr.toLong()
		} else {
			expr.split(" ").let { (m1, op, m2) ->
				val l = computeResult(m1) ?: return null
				val r = computeResult(m2) ?: return null
				return when (op) {
					"+" -> l + r
					"-" -> l - r
					"*" -> l * r
					"/" -> l / r
					else -> error("unknown op")
				}
			}
		}
	}

	fun solve(monkey: String, equality: Long): Long {
		val expr = monkeys[monkey] ?: return equality
		expr.split(" ").let { (m1, op, m2) ->
			val l = computeResult(m1)
			val r = computeResult(m2)

			if (l == null && r != null) {
				// solve for l
				return solve(
					m1,
					when (op) {
						"+" -> equality - r
						"-" -> equality + r
						"*" -> equality / r
						"/" -> equality * r
						else -> error("unknown op")
					}
				)
			} else if (l != null && r == null) {
				// solve for r
				return solve(
					m2,
					when (op) {
						"+" -> equality - l
						"-" -> l - equality
						"*" -> equality / l
						"/" -> l / equality
						else -> error("unknown op")
					}
				)
			} else {
				error("welp..")
			}
		}
	}

	monkeys["root"] = monkeys["root"]!!.let { it.split(" ").let { (m1, _, m2) -> "$m1 - $m2" } }
	monkeys["humn"] = null
	val humnValue = solve("root", 0)

	println("$this: $humnValue")
}
