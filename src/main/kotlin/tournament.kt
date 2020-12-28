import xerus.ktutil.helpers.Rater
import java.io.File
import java.io.FileWriter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.system.exitProcess

val root = File("src/main/resources").takeIf { it.exists() }
    ?: File(".")

fun getFile(name: String): File = root.resolve(name)

val gamesFile = getFile("games.txt")
val gamesWriter = FileWriter(gamesFile, true).buffered()
fun writeGame(text: String) {
    gamesWriter.appendLine(text)
    println(text)
}

fun main() {
    val teamsFile = getFile("teams.txt")
    val teams = teamsFile.readLines().filter { it.isNotBlank() }.let {
        if(it.size % 2 == 1) {
            it + "--"
        } else {
            it
        }
    }
    if(teams.size < 2) {
        println("Please create $teamsFile with at least 2 teams.")
        exitProcess(1)
    }
    
    var round = 1
    val games = try {
        val lines = gamesFile.readLines()
        round = (lines.lastOrNull { it.contains("Round") }?.split(" ")?.last()?.let { Integer.parseInt(it) } ?: 0) + 1
        lines.filter { it.isNotBlank() && !it.startsWith("#") }
    } catch(e: Exception) {
        emptyList()
    }
    val matchups = HashMap<String, MutableMap<String, AtomicInteger>>()
    teams.forEach {
        val m = matchups.getOrPut(it) { HashMap() }
        teams.forEach { opponent ->
            m[opponent] = AtomicInteger(1_073_741_824)
        }
    }
    games.forEach {
        val c = it.split(" vs ")
        assert(c.size == 2) { "Invalid matchup: $it" }
        if(!teams.contains(c[0]) || !teams.contains(c[1])) {
            println("Ignoring previous game $it as it contains inactive players")
        } else {
            matchups[c[1]]!![c[0]]!!.getAndUpdate { (it / 4).coerceAtLeast(1) }
            matchups[c[0]]!![c[1]]!!.getAndUpdate { (it / 4).coerceAtLeast(1) }
        }
    }
    
    println()
    writeGame("# Round $round")
    
    val result = Rater<String>(true)
    val startTime = System.currentTimeMillis()
    experiment@ do {
        var collisions = 0
        val openPlayers = teams.toMutableList()
        val output = StringBuffer()
        while(openPlayers.size > 1) {
            val p = openPlayers.removeAt(Random.nextInt(openPlayers.size - 1))
            val enemy =
                if(openPlayers.size > 1) {
                    val m = matchups[p]!!.filterKeys { openPlayers.contains(it) }
                    val sum = m.values.sumBy { it.get() }
                    var rand = Random.nextInt(sum)
                    m.entries.first {
                        rand -= it.value.get()
                        rand <= 0
                    }.key
                } else {
                    openPlayers.single()
                }
            openPlayers.remove(enemy)
            output.appendLine("$p vs $enemy")
            val previous = games.count { it.contains(p) && it.contains(enemy) }
            if(previous > 0) {
                //output.appendln("# This matchup already occured $previous time${if(previous > 1) "s" else ""}")
                collisions += previous * previous
                if(collisions >= result.points)
                    continue@experiment
            }
        }
        result.update(collisions.toDouble()) {
            output.toString()
        }
        //println("Considered: ${output.toString().replace("\n", "\n  ")}")
    } while(System.currentTimeMillis() - startTime < 500 && collisions > 0)
    writeGame(result.obj!!)
    
    gamesWriter.close()
}
