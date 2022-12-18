import java.io.File
import java.util.LinkedList

val input = File("input.txt").readLines()


with("Part A") {
	val voxels = input.map { it.split(",") }.map { (x, y, z) -> Voxel(x.toInt(), y.toInt(), z.toInt()) }
	val totalSize = Voxel(voxels.maxOf { it.x } + 1, voxels.maxOf { it.y } + 1, voxels.maxOf { it.z } + 1)

	val occupancyGrid: List<List<MutableList<Boolean>>> = List(totalSize.x) {
		List(totalSize.y) {
			MutableList(totalSize.z) { false }
		}
	}
	voxels.forEach { (x, y, z) ->
		occupancyGrid[x][y][z] = true
	}

	fun List<List<MutableList<Boolean>>>.isOccupied(x: Int, y: Int, z: Int): Boolean {
		if (x < 0 || size <= x) return false
		if (y < 0 || this[x].size <= y) return false
		if (z < 0 || this[x][y].size <= z) return false
		return this[x][y][z]
	}

	var surfaceArea = 0
	for (x in occupancyGrid.indices) {
		for (y in occupancyGrid[0].indices) {
			for (z in occupancyGrid[0][0].indices) {
				if (!occupancyGrid[x][y][z]) continue
				if (!occupancyGrid.isOccupied(x, y, z - 1)) surfaceArea++
				if (!occupancyGrid.isOccupied(x, y, z + 1)) surfaceArea++
				if (!occupancyGrid.isOccupied(x, y - 1, z)) surfaceArea++
				if (!occupancyGrid.isOccupied(x, y + 1, z)) surfaceArea++
				if (!occupancyGrid.isOccupied(x - 1, y, z)) surfaceArea++
				if (!occupancyGrid.isOccupied(x + 1, y, z)) surfaceArea++
			}
		}
	}

	println("$this: $surfaceArea")
}


with("Part B") {
	val voxels = input.map { it.split(",") }.map { (x, y, z) -> Voxel(x.toInt(), y.toInt(), z.toInt()) }
	val totalSize = Voxel(voxels.maxOf { it.x } + 1, voxels.maxOf { it.y } + 1, voxels.maxOf { it.z } + 1)

	val lava = '#'
	val air = '.'
	val outside = ' '

	val occupancyGrid: List<List<MutableList<Char>>> = List(totalSize.x) {
		List(totalSize.y) {
			MutableList(totalSize.z) { air }
		}
	}
	voxels.forEach { (x, y, z) ->
		occupancyGrid[x][y][z] = lava
	}

	fun List<List<MutableList<Char>>>.get(x: Int, y: Int, z: Int): Char {
		if (x < 0 || size <= x) return outside
		if (y < 0 || this[x].size <= y) return outside
		if (z < 0 || this[x][y].size <= z) return outside
		return this[x][y][z]
	}

	// floodfill outside
	val queue = LinkedList<Voxel>()
	occupancyGrid[0][0][0] = outside // starting point 0,0,0 happens to be outside lava
	queue += Voxel(0, 0, 0)
	while (queue.isNotEmpty()) {
		queue.removeFirst().let { (x, y, z) ->
			queue += buildList {
				if (occupancyGrid.get(x, y, z - 1) == air) add(Voxel(x, y, z - 1))
				if (occupancyGrid.get(x, y, z + 1) == air) add(Voxel(x, y, z + 1))
				if (occupancyGrid.get(x, y - 1, z) == air) add(Voxel(x, y - 1, z))
				if (occupancyGrid.get(x, y + 1, z) == air) add(Voxel(x, y + 1, z))
				if (occupancyGrid.get(x - 1, y, z) == air) add(Voxel(x - 1, y, z))
				if (occupancyGrid.get(x + 1, y, z) == air) add(Voxel(x + 1, y, z))
			}.onEach { (x, y, z) ->
				occupancyGrid[x][y][z] = outside
			}
		}
	}

	var surfaceArea = 0
	for (x in occupancyGrid.indices) {
		for (y in occupancyGrid[0].indices) {
			for (z in occupancyGrid[0][0].indices) {
				if (occupancyGrid[x][y][z] != lava) continue
				if (occupancyGrid.get(x, y, z - 1) == outside) surfaceArea++
				if (occupancyGrid.get(x, y, z + 1) == outside) surfaceArea++
				if (occupancyGrid.get(x, y - 1, z) == outside) surfaceArea++
				if (occupancyGrid.get(x, y + 1, z) == outside) surfaceArea++
				if (occupancyGrid.get(x - 1, y, z) == outside) surfaceArea++
				if (occupancyGrid.get(x + 1, y, z) == outside) surfaceArea++
			}
		}
	}

	println("$this: $surfaceArea")
}


data class Voxel(val x: Int, val y: Int, val z: Int)
