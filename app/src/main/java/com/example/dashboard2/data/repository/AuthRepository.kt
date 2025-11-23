package com.example.dashboard2.data.repository

import android.net.Uri
import android.util.Log
import com.example.dashboard2.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val courseRepository = CourseRepository()

    companion object {
        private const val TAG = "AUTH_REPO"
        private const val COLLECTION_USERS = "users"
    }

    // ✅ SIGN UP - DENGAN AUTO-ENROLL DEFAULT COURSE
    suspend fun signUp(name: String, email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "========== STARTING SIGN UP ==========")
            Log.d(TAG, "Email: $email, Name: $name")

            // Validasi
            if (!email.contains("@")) {
                Log.e(TAG, "ERROR: Email validation failed")
                return@withContext Result.failure(Exception("Email harus menggunakan @"))
            }

            if (password.length < 6) {
                Log.e(TAG, "ERROR: Password validation failed")
                return@withContext Result.failure(Exception("Password minimal 6 karakter"))
            }

            // Buat akun di Firebase Authentication
            Log.d(TAG, "Creating Firebase Auth user...")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal membuat akun")

            Log.d(TAG, "✓ Firebase Auth user created: ${firebaseUser.uid}")

            // Buat object User
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                institution = "",
                photoUrl = "",
                location = "",
                latitude = 0.0,
                longitude = 0.0,
                createdAt = System.currentTimeMillis()
            )

            // Simpan ke Firestore
            Log.d(TAG, "Saving user to Firestore...")
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Log.d(TAG, "✓ User saved to Firestore")

            // ✅ AUTO-ENROLL DEFAULT COURSE (Beginner Investor)
            Log.d(TAG, "Auto-enrolling default course...")
            val enrollResult = courseRepository.enrollDefaultCourse()

            enrollResult.onSuccess {
                Log.d(TAG, "✓ Default course enrolled successfully")
            }.onFailure { error ->
                Log.e(TAG, "⚠️ Failed to enroll default course: ${error.message}")
                // Tidak gagalkan sign up meski enroll gagal
            }

            Log.d(TAG, "========== SIGN UP COMPLETED ==========")
            Result.success(user)

        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e(TAG, "ERROR: Weak password", e)
            Result.failure(Exception("Password terlalu lemah"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "ERROR: Invalid credentials", e)
            Result.failure(Exception("Format email tidak valid"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "ERROR: User collision", e)
            Result.failure(Exception("Email sudah terdaftar"))
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Exception during sign up", e)
            Result.failure(Exception("Gagal mendaftar: ${e.message}"))
        }
    }

    // ✅ SIGN IN
    suspend fun signIn(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "========== STARTING SIGN IN ==========")
            Log.d(TAG, "Email: $email")

            if (!email.contains("@")) {
                Log.e(TAG, "ERROR: Email validation failed")
                return@withContext Result.failure(Exception("Email harus menggunakan @"))
            }

            Log.d(TAG, "Signing in with Firebase...")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User tidak ditemukan")

            Log.d(TAG, "✓ Firebase Auth sign in successful: ${firebaseUser.uid}")

            Log.d(TAG, "Fetching user data from Firestore...")
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Data user tidak ditemukan")

            Log.d(TAG, "✓ User data fetched successfully")
            Log.d(TAG, "  User Name: ${user.name}")
            Log.d(TAG, "  User Email: ${user.email}")
            Log.d(TAG, "========== SIGN IN COMPLETED ==========")

            Result.success(user)

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, "ERROR: Invalid credentials", e)
            Result.failure(Exception("Email atau password salah"))
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Exception during sign in", e)
            Result.failure(Exception("Gagal login: ${e.message}"))
        }
    }

    // ✅ GET CURRENT USER
    suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        return@withContext try {
            val firebaseUser = auth.currentUser

            Log.d(TAG, "========== GET CURRENT USER ==========")
            Log.d(TAG, "Firebase User: ${firebaseUser?.uid}")
            Log.d(TAG, "Firebase Email: ${firebaseUser?.email}")

            if (firebaseUser == null) {
                Log.e(TAG, "❌ No user logged in")
                return@withContext null
            }

            Log.d(TAG, "Fetching user data from Firestore...")
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!userDoc.exists()) {
                Log.e(TAG, "❌ User document not found in Firestore")
                Log.e(TAG, "Document path: users/${firebaseUser.uid}")
                return@withContext null
            }

            Log.d(TAG, "✓ User document found")
            Log.d(TAG, "Document data: ${userDoc.data}")

            val user = userDoc.toObject(User::class.java)

            if (user != null) {
                Log.d(TAG, "✅ User data loaded:")
                Log.d(TAG, "  UID: ${user.uid}")
                Log.d(TAG, "  Name: ${user.name}")
                Log.d(TAG, "  Email: ${user.email}")
                Log.d(TAG, "  Institution: ${user.institution}")
            } else {
                Log.e(TAG, "❌ Failed to parse user data")
            }

            Log.d(TAG, "====================================")

            user

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting current user", e)
            e.printStackTrace()
            null
        }
    }

    // ✅ UPDATE PROFILE dengan PHOTO UPLOAD
    suspend fun updateProfile(
        name: String,
        institution: String,
        password: String? = null,
        photoUri: Uri? = null
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "❌ No user logged in")
                return@withContext false
            }

            val userId = currentUser.uid

            Log.d(TAG, "========== STARTING UPDATE PROFILE ==========")
            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Name: $name, Institution: $institution")
            Log.d(TAG, "Password change: ${password != null}")
            Log.d(TAG, "Photo URI: ${photoUri ?: "NULL"}")

            var photoUrl: String? = null

            // ✅ Upload photo to Firebase Storage if provided
            if (photoUri != null) {
                Log.d(TAG, "========== UPLOADING PHOTO ==========")
                try {
                    val timestamp = System.currentTimeMillis()
                    val storageRef = storage.reference
                        .child("profile_photos/$userId/$timestamp.jpg")

                    Log.d(TAG, "Storage path: ${storageRef.path}")
                    Log.d(TAG, "Uploading photo...")

                    val uploadTask = storageRef.putFile(photoUri).await()
                    Log.d(TAG, "✓ Photo uploaded successfully")
                    Log.d(TAG, "Upload metadata: ${uploadTask.metadata?.path}")

                    Log.d(TAG, "Getting download URL...")
                    photoUrl = storageRef.downloadUrl.await().toString()
                    Log.d(TAG, "✓ Photo URL obtained: $photoUrl")

                } catch (e: Exception) {
                    Log.e(TAG, "❌ ERROR uploading photo", e)
                    Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
                    Log.e(TAG, "Error message: ${e.message}")
                    e.printStackTrace()
                    // Continue without photo update if upload fails
                    Log.w(TAG, "⚠️ Continuing profile update without photo")
                }
                Log.d(TAG, "=====================================")
            }

            // ✅ Update Firestore document
            Log.d(TAG, "Updating Firestore document...")
            val updates = mutableMapOf<String, Any>(
                "name" to name,
                "institution" to institution,
                "updatedAt" to System.currentTimeMillis()
            )

            // Add photoUrl if photo was uploaded successfully
            if (photoUrl != null) {
                updates["photoUrl"] = photoUrl
                Log.d(TAG, "Adding photoUrl to Firestore updates")
            }

            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .await()

            Log.d(TAG, "✓ Firestore document updated successfully")

            // ✅ Update password di Firebase Auth jika ada password baru
            if (password != null && password.isNotBlank()) {
                Log.d(TAG, "Updating Firebase Auth password...")
                currentUser.updatePassword(password).await()
                Log.d(TAG, "✓ Password updated successfully")
            }

            Log.d(TAG, "========== UPDATE PROFILE COMPLETED ==========")
            true

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR: Exception during update profile", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // ✅ UPDATE USER LOCATION
    suspend fun updateUserLocation(
        address: String,
        latitude: Double,
        longitude: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "No user logged in")
                return@withContext Result.failure(Exception("Silakan login terlebih dahulu"))
            }

            Log.d(TAG, "========== UPDATING USER LOCATION ==========")
            Log.d(TAG, "User ID: ${currentUser.uid}")
            Log.d(TAG, "Address: $address")
            Log.d(TAG, "Coordinates: $latitude, $longitude")

            firestore.collection(COLLECTION_USERS)
                .document(currentUser.uid)
                .update(
                    mapOf(
                        "location" to address,
                        "latitude" to latitude,
                        "longitude" to longitude
                    )
                )
                .await()

            Log.d(TAG, "✓ User location updated successfully")
            Log.d(TAG, "===========================================")

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to update user location", e)
            Result.failure(Exception("Gagal menyimpan lokasi: ${e.message}"))
        }
    }

    // ✅ GET USER LOCATION
    suspend fun getUserLocation(): Result<Triple<String, Double, Double>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("User not logged in"))
            }

            val document = firestore.collection(COLLECTION_USERS)
                .document(currentUser.uid)
                .get()
                .await()

            val location = document.getString("location") ?: ""
            val latitude = document.getDouble("latitude") ?: 0.0
            val longitude = document.getDouble("longitude") ?: 0.0

            Result.success(Triple(location, latitude, longitude))

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to get user location", e)
            Result.failure(e)
        }
    }

    // ✅ SIGN OUT
    fun signOut() {
        Log.d(TAG, "Signing out user...")
        auth.signOut()
        Log.d(TAG, "✓ User signed out")
    }

    // ✅ CHECK IF LOGGED IN
    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        if (isLoggedIn) {
            Log.d(TAG, "User logged in: ${auth.currentUser?.uid}")
        } else {
            Log.d(TAG, "No user logged in")
        }
        return isLoggedIn
    }

    // ✅ GET USER ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // ✅ GET USER EMAIL
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}