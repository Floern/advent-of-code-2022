import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()//.first().toCharArray().asList()

/// Part A

val root: VFile = VFile()
var cd: VFile = root
input.forEach { cmd ->
	if (cmd.startsWith("$ ")) {
		val rcmd = cmd.substring(2)
		when {
			rcmd == "ls" -> {
				// nothing
			}
			rcmd.startsWith("cd ") -> {
				val dir = rcmd.substring(3)
				when (dir) {
					"/" -> { cd = root }
					".." -> { cd = cd.parent!! }
					else -> { cd = cd.files[dir]!! }
				}
			}
			else -> {}
		}
	} else {
		val (size, name) = cmd.split(" ", limit = 2)
		if (size == "dir") {
			cd.files.put(name, VFile(parent = cd))
		} else {
			cd.files.put(name, VFile(size = size.toInt(), parent = cd))
		}
	}
}

root.print("- ")
println()

val resultA = root.flatten()
	.filter { it.size == 0 }
	.map { it.recSize() }
	.filter { it <= 100000 }
	.sum()

println("A: " + resultA)
println()
println()

/// Part B

val capacity = 70_000_000
val free = capacity - root.recSize()
val toDelete = (30_000_000 - free)
println("capacity: " + capacity)
println("free:     " + free)
println("toDelete: " + toDelete)
println()

val dirToDelete = root.flatten()
	.filter { it.recSize() >= toDelete }
	.minBy { it.recSize() }
val resultB = dirToDelete.recSize()

println("B: " + resultB)

// classes

data class VFile(
	val files: MutableMap<String, VFile> = mutableMapOf(),
	val size: Int = 0,
	val parent: VFile? = null,
) {
	fun recSize(): Int {
		if (size > 0) {
			return size
		} else {
			return files.values.sumOf { it.recSize() }
		}
	}

	fun flatten(): List<VFile> {
		return files.values.map { it.flatten() }.flatten() + this
	}

	fun print(indent: String) {
		files.forEach { (name, file) ->
			val size = if (file.size == 0) file.recSize() else file.size
			println(indent + name + " " + size)
			file.print("  " + indent)
		}
	}
}
