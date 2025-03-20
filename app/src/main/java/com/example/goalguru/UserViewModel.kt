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

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _profilePicture = MutableLiveData<String>()
    val profilePicture: LiveData<String> get() = _profilePicture

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    init {
        _user.value = auth.currentUser
        _user.value?.uid?.let { userId ->
            firebaseModel.getUserByID(userId) { user ->
                _username.value = user?.username ?: "unknown"
                _profilePicture.value = user?.profilePicture ?: ""
                _email.value = user?.email ?: ""
            }
        }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _user.value?.uid?.let { userId ->
                        firebaseModel.getUserByID(userId) { user ->
                            _username.value = user?.username ?: "unknown"
                            _profilePicture.value = user?.profilePicture ?: ""
                            _email.value = user?.email ?: ""
                        }
                    }
                    Toast.makeText(MyApplication.Globals.context, "Login successful", Toast.LENGTH_SHORT).show()
                    Log.d("UserViewModel", _user.value.toString())
                } else {
                    _user.value = null
                    Toast.makeText(MyApplication.Globals.context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun updateUserData() {
        firebaseModel.getUserByID(getCurrentUserId() ?: return) { user ->
            _username.value = user?.username ?: "unknown"
            _profilePicture.value = user?.profilePicture ?: ""
            _email.value = user?.email ?: ""
        }
    }

    fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    firebaseModel.saveUserToFirestore(_user.value?.uid, email, username, "")
                    _username.value = username
                    _profilePicture.value = ""
                } else {
                    _user.value = null
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
        _user.value = null
        _username.value = ""
        _profilePicture.value = ""
        _email.value = ""
    }

    fun getCurrentUserId(): String? {
        return _user.value?.uid
    }

    fun getUser(): FirebaseUser? {
        return _user.value
    }
}