import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

val input = File("input.txt").readLines()

val digitMap = mapOf(
	'=' to -2,
	'-' to -1,
	'0' to 0,
	'1' to 1,
	'2' to 2,
)

fun parseSNAFU(value: String): Long {
	return value.reversed()
		.mapIndexed { index, c ->
			5.0.pow(index).roundToLong() * digitMap[c]!!
		}
		.sum()
}

fun Long.toSNAFU(): String {
	return ("0" + toString(5))
		.foldRight("" to 0) { digit, (result, carry) ->
			val value = digit.digitToInt() + carry
			if (value <= 2) (value.toString() + result) to 0
			else if (value == 3) ("=" + result) to 1
			else if (value == 4) ("-" + result) to 1
			else (value.minus(5).toString() + result) to 1
		}
		.first.trimStart('0')
}

input.forEach {
	if (it != parseSNAFU(it).toSNAFU()) {
		error("snafu conversion incorrect")
	}
}



with("Part A") {
	val sum = input.map { parseSNAFU(it) }.sum()
	val sumSNAFU = sum.toSNAFU()

	println("$this: $sumSNAFU")
}


with("Part B") {

	println("$this: ")
}
