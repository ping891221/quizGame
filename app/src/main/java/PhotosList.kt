import android.os.Parcelable
import java.io.Serializable
import kotlinx.parcelize.Parcelize
@Parcelize
data class PhotosList(
    val answer: String = "",
    val audioUrl: String = "",
    val chinese: String = "",
    val english: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val photoUrl: String = "",
    @Transient var userSelectAnswer: String = ""
): Parcelable
