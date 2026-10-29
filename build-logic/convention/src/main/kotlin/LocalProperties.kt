import java.util.Properties
import org.gradle.api.GradleException
import org.gradle.api.Project

/** gitignore된 local.properties에서 비밀값을 읽음. 빠진 키가 있으면 설정 단계에서 실패 */
fun Project.localProperties(keys: List<String>): Map<String, String> {
    val properties = Properties()
    val file = rootProject.file("local.properties")
    if (file.exists()) file.reader().use(properties::load)

    val missing = keys.filter { properties.getProperty(it).isNullOrBlank() }
    if (missing.isNotEmpty()) {
        throw GradleException("local.properties에 다음 값이 없습니다: ${missing.joinToString()}")
    }
    return keys.associateWith(properties::getProperty)
}
