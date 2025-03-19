import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goalguru.base.MyApplication
import com.example.goalguru.model.FirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserViewModel : ViewModel() {
    private val firebaseModel = FirebaseModel(this)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user
    private var username: String? = null
    private var profileImage :String = ""

    init {
        initializeUserData(auth)
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //get user data from firestore
                    initializeUserData(auth)

                    Toast.makeText(MyApplication.Globals.context, "Login successful", Toast.LENGTH_SHORT).show()
                    Log.d("UserViewModel", _user.value.toString())
                } else {
                    _user.value = null
                    Toast.makeText(MyApplication.Globals.context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    firebaseModel.saveUserToFirestore(_user.value?.uid, email, username, "")
                } else {
                    _user.value = null
                }
            }
    }

    fun initializeUserData(auth: FirebaseAuth) {
        _user.value = auth.currentUser
        firebaseModel.getUserByID(_user.value?.uid ?: "") { user ->
            username = user?.username
            profileImage = user?.profilePicture ?: ""
        }
    }

    fun logoutUser() {
        auth.signOut()
        _user.value = null
    }

    fun getCurrentUserId(): String? {
        return _user.value?.uid
    }

    fun getCurrentUserUsername(): String? {
        return username
    }

    fun getCurrentUserProfileImage(): String {
        return profileImage
    }

}