import androidx.lifecycle.MutableLiveData
import com.example.quit.UserData

object UserDataRepository {
    var userData: UserData? = null
        private set // 避免直接更改

    val userDataLiveData = MutableLiveData<UserData?>()

    fun updateUserData(newUserData: UserData?) {
        if (userData != newUserData) {
            userData = newUserData
            userDataLiveData.value = userData // 通知所有觀察者數據已更新
        }
    }
}

