import com.github.gameinventory.data.local.Game
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//https://694811221ee66d04a44ea00f.mockapi.io/games
@Serializable
data class RemoteGame (
    val id: String?,
    val slug: String,
    val name: String,
    val released: String? = null,
    val rating: Double = 0.0,
    @SerialName("metacritic")val metacriticScore: Int? = null,
    @SerialName("background_image")val backgroundImage: String? = null,
)
fun RemoteGame.toLocal(isFromSteam: Boolean = false): Game {
    return Game(
        id = id ?: "local_${System.nanoTime()}",
        slug = slug,
        name = name,
        released = released,
        rating = rating,
        metacriticScore = metacriticScore,
        backgroundImage = backgroundImage,
        pendingSync = isFromSteam,
        pendingDelete = false
    )
}