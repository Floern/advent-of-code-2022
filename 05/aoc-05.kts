import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()

/// Part A
/*
val stacks = mutableMapOf<Int, LinkedList<String>>()

input.takeWhile { it.isNotBlank() }
	.reversed()
	.drop(1)
	.map { line ->
		line.chunked(4).map { it.trim(' ', '[', ']') }
			.forEachIndexed { i, crate -> if (crate.isNotBlank()) { stacks.getOrPut(i) { LinkedList<String>() }.add(crate) } }
	}

input.takeLastWhile { it.isNotBlank() }
	.forEach {
		println(stacks.toString())
		println("-" + it)
		val splits = it.split(' ')
		val c = splits[1].toInt()
		val f = splits[3].toInt() - 1
		val t = splits[5].toInt() - 1
		repeat(c) {
			stacks.get(t)!!.add(stacks.get(f)!!.removeLast())
		}
	}
println(stacks.toString())

val resultA = stacks.map { it.value.last }.joinToString("")
println("A: " + resultA)*/

/// Part B

val stacks = mutableMapOf<Int, LinkedList<String>>()

input.takeWhile { it.isNotBlank() }
	.reversed()
	.drop(1)
	.map { line ->
		line.chunked(4).map { it.trim(' ', '[', ']') }
			.forEachIndexed { i, crate -> if (crate.isNotBlank()) { stacks.getOrPut(i) { LinkedList<String>() }.add(crate) } }
	}

input.takeLastWhile { it.isNotBlank() }
	.forEach {
		println(stacks.toString())
		println("-" + it)
		val splits = it.split(' ')
		val c = splits[1].toInt()
		val f = splits[3].toInt() - 1
		val t = splits[5].toInt() - 1
		val crane = LinkedList<String>()
		repeat(c) {
			crane.add(stacks.get(f)!!.removeLast())
		}
		repeat(c) {
			stacks.get(t)!!.add(crane.removeLast())
		}
	}
println(stacks.toString())

val resultB = stacks.map { it.value.last }.joinToString("")
println("A: " + resultB)
