import java.io.File

val input = File("input.txt").readLines()

with("Part A") {
	val checkcycles = mutableMapOf<Int, Int>(
		20 to Int.MIN_VALUE,
		60 to Int.MIN_VALUE,
		100 to Int.MIN_VALUE,
		140 to Int.MIN_VALUE,
		180 to Int.MIN_VALUE,
		220 to Int.MIN_VALUE,
	)

	input.fold(State()) { state, cmd ->
		state.apply {
			if (cmd == "noop") {
				if (checkcycles.containsKey(cycle + 1)) checkcycles.put(cycle + 1, x)
				cycle += 1
			} else {
				if (checkcycles.containsKey(cycle + 1)) checkcycles.put(cycle + 1, x)
				else if (checkcycles.containsKey(cycle + 2)) checkcycles.put(cycle + 2, x)
				cycle += 2
				x += cmd.substring(5).toInt()
			}
		}
	}

	val signalStrength = checkcycles.entries.sumOf { (k, v) -> k * v }
	println(this + ": " + signalStrength)
}


with("Part B") {
	val width = 40
	val crt = MutableList(6 * width) { _ -> ' ' } // using space insteaad of dot

	input.fold(State()) { state, cmd ->
		state.apply {
			if (cmd == "noop") {
				if ((cycle % width) in sprite) crt[cycle] = '#'
				cycle += 1
			} else {
				if ((cycle % width) in sprite) crt[cycle] = '#'
				if ((cycle + 1) % width in sprite) crt[cycle + 1] = '#'
				cycle += 2
				x += cmd.substring(5).toInt()
			}
		}
	}

	println(this + ":")
	crt.chunked(width)
		.map { String(it.toCharArray()) }
		.forEach {
			println(it)
		}
}


data class State(
	var cycle: Int = 0,
	var x: Int = 1
) {
	val sprite: IntRange
		get() = (x - 1)..(x + 1)
}
