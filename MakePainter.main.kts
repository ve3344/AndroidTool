import java.io.File

val targetPackage = "me.lwb.androidtool"
val targetResDir = "desktop/src/main/resources/painter"
val targetClass = "desktop/src/main/java/${targetPackage.replace('.', '/')}/Painters.kt"


fun process(sb: StringBuilder, resDir: File, relativeDirName: String, objectName: String) {
    val painter: Array<File> = resDir.listFiles() ?: emptyArray()

    val vars = painter.filter { it.isFile }.map {
        var vName = it.nameWithoutExtension.replace("-", "_")
        if (vName.firstOrNull()?.isJavaIdentifierStart() == false) {
            vName = "_${vName}"
        }
        val path = relativeDirName + it.name
        """val $vName: Painter @Composable get() = painterResource("$path")"""
    }

    val text = vars.joinToString("\n").prependIndent(" ".repeat(4))

    sb.append("""
object $objectName{
$text
""")

    val dirs = painter.filter { it.isDirectory }
    if (dirs.isNotEmpty()) {
        val sbInner = StringBuilder()
        for (dir in dirs) {
            process(sbInner, dir, relativeDirName + dir.name + "/", dir.nameWithoutExtension.capitalize())
        }
        sb.append(sbInner.toString().prependIndent(" ".repeat(4)))
    }

    sb.append("\n}")
}

val sb = StringBuilder()
sb.append("""
package ${targetPackage}
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource


""".trimIndent())
process(sb, File(targetResDir), "painter/", "Painters")

val file = File(targetClass)
file.writeText(sb.toString())