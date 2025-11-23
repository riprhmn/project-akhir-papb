package com.example.dashboard2.data.repository

import android.util.Log
import com.example.dashboard2.data.model.EventDatabase
import com.example.dashboard2.data.model.EventItem
import com.example.dashboard2.data.model.EventRegistration
import com.example.dashboard2.data.model.EventWithDistance
import com.example.dashboard2.utils.DistanceCalculator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val authRepository = AuthRepository()

    companion object {
        private const val TAG = "EVENT_REPO"
        private const val COLLECTION_EVENTS = "events"
        private const val COLLECTION_EVENT_REGISTRATIONS = "event_registrations"
    }

    // ========== GET EVENTS FROM DATABASE ==========

    /**
     * Get all events from EventDatabase (no Firestore needed)
     */
    suspend fun getAllEvents(): List<EventItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "========== FETCHING EVENTS FROM DATABASE ==========")

            // ✅ EventDatabase.getAllEvents() already returns List<EventItem>
            val events = EventDatabase.getAllEvents()

            Log.d(TAG, "✓ Loaded ${events.size} events from database")
            Log.d(TAG, "====================================================")

            events

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to fetch events", e)
            emptyList()
        }
    }

    /**
     * Get specific event by ID
     */
    suspend fun getEventById(eventId: String): EventItem? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Fetching event: $eventId")

            // ✅ EventDatabase.getEventById() already returns EventItem?
            val event = EventDatabase.getEventById(eventId)

            if (event != null) {
                Log.d(TAG, "✓ Event found: ${event.title}")
            } else {
                Log.w(TAG, "Event not found: $eventId")
            }

            event

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to fetch event", e)
            null
        }
    }

    /**
     * Get events by category
     */
    suspend fun getEventsByCategory(category: String): List<EventItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Fetching events for category: $category")
            EventDatabase.getEventsByCategory(category)
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to fetch events by category", e)
            emptyList()
        }
    }

    /**
     * Search events by query
     */
    suspend fun searchEvents(query: String): List<EventItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Searching events with query: $query")
            EventDatabase.searchEvents(query)
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to search events", e)
            emptyList()
        }
    }

    // ========== DISTANCE CALCULATION ==========

    /**
     * Get events sorted by distance (nearest first)
     */
    suspend fun getEventsSortedByDistance(): List<EventWithDistance> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "========== GETTING EVENTS SORTED BY DISTANCE ==========")

            val events = getAllEvents()
            val userLocationResult = authRepository.getUserLocation()

            userLocationResult.fold(
                onSuccess = { (address, userLat, userLng) ->
                    if (address.isEmpty() || (userLat == 0.0 && userLng == 0.0)) {
                        Log.w(TAG, "User location not available")
                        events.map { EventWithDistance.fromEvent(it) }
                    } else {
                        Log.d(TAG, "User location: $userLat, $userLng")

                        val eventsWithDistance = events.map { event ->
                            val distanceKm = if (event.latitude != 0.0 && event.longitude != 0.0) {
                                DistanceCalculator.calculateDistance(
                                    lat1 = userLat,
                                    lon1 = userLng,
                                    lat2 = event.latitude,
                                    lon2 = event.longitude
                                )
                            } else {
                                Double.MAX_VALUE
                            }

                            EventWithDistance(
                                event = event,
                                distanceKm = distanceKm,
                                distanceText = if (distanceKm == Double.MAX_VALUE) {
                                    "Lokasi tidak tersedia"
                                } else {
                                    DistanceCalculator.formatDistance(distanceKm)
                                }
                            )
                        }.sortedBy { it.distanceKm }

                        Log.d(TAG, "✓ Sorted ${eventsWithDistance.size} events by distance")
                        Log.d(TAG, "=======================================================")

                        eventsWithDistance
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "ERROR: Failed to get user location", error)
                    events.map { EventWithDistance.fromEvent(it) }
                }
            )

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to get events sorted by distance", e)
            emptyList()
        }
    }

    /**
     * Calculate distance to specific event
     */
    suspend fun calculateDistanceToEvent(event: EventItem): String {
        return calculateDistanceToEvent(event.latitude, event.longitude)
    }

    /**
     * Calculate distance to event coordinates
     */
    suspend fun calculateDistanceToEvent(
        eventLatitude: Double,
        eventLongitude: Double
    ): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val userLocationResult = authRepository.getUserLocation()

            userLocationResult.fold(
                onSuccess = { (address, userLat, userLng) ->
                    if (address.isEmpty() || (userLat == 0.0 && userLng == 0.0)) {
                        "Lokasi tidak tersedia"
                    } else if (eventLatitude == 0.0 && eventLongitude == 0.0) {
                        "Lokasi event tidak tersedia"
                    } else {
                        val distanceKm = DistanceCalculator.calculateDistance(
                            lat1 = userLat,
                            lon1 = userLng,
                            lat2 = eventLatitude,
                            lon2 = eventLongitude
                        )
                        DistanceCalculator.formatDistance(distanceKm)
                    }
                },
                onFailure = {
                    "Lokasi tidak tersedia"
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating distance", e)
            "Lokasi tidak tersedia"
        }
    }

    // ========== EVENT REGISTRATION (Firestore) ==========

    /**
     * Register user for event
     */
    suspend fun registerEvent(
        eventId: String,
        eventTitle: String,
        eventDate: String,
        eventLocation: String,
        eventLatitude: Double,
        eventLongitude: Double,
        userName: String
    ): Result<EventRegistration> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser

            Log.d(TAG, "========== REGISTERING EVENT ==========")
            Log.d(TAG, "Current User: ${currentUser?.uid ?: "NULL"}")
            Log.d(TAG, "Current User Email: ${currentUser?.email ?: "NULL"}")

            if (currentUser == null) {
                Log.e(TAG, "❌ ERROR: No user logged in")
                return@withContext Result.failure(Exception("Silakan login terlebih dahulu"))
            }

            Log.d(TAG, "Event Details:")
            Log.d(TAG, "  - Event ID: $eventId")
            Log.d(TAG, "  - Event Title: $eventTitle")
            Log.d(TAG, "  - Event Date: $eventDate")
            Log.d(TAG, "  - Event Location: $eventLocation")
            Log.d(TAG, "  - User Name: $userName")

            // Check if already registered
            Log.d(TAG, "Checking if already registered...")
            val existingRegistration = getEventRegistration(eventId)
            if (existingRegistration != null) {
                Log.d(TAG, "❌ User already registered for this event")
                return@withContext Result.failure(Exception("Anda sudah terdaftar untuk event ini"))
            }
            Log.d(TAG, "✓ No existing registration found")

            // Create registration object
            val registration = EventRegistration(
                eventId = eventId,
                eventTitle = eventTitle,
                eventDate = eventDate,
                eventLocation = eventLocation,
                eventLatitude = eventLatitude,
                eventLongitude = eventLongitude,
                userId = currentUser.uid,
                userEmail = currentUser.email ?: "",
                userName = userName,
                registeredAt = System.currentTimeMillis(),
                status = "registered"
            )

            Log.d(TAG, "Registration object created:")
            Log.d(TAG, "  - Document Path: users/${currentUser.uid}/event_registrations/$eventId")
            Log.d(TAG, "  - Registration: $registration")

            // Save to Firestore
            Log.d(TAG, "Attempting to save to Firestore...")

            val docRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("event_registrations")
                .document(eventId)

            Log.d(TAG, "Document reference: ${docRef.path}")

            docRef.set(registration).await()

            Log.d(TAG, "✅ Event registration saved successfully to Firestore")
            Log.d(TAG, "=========================================")

            Result.success(registration)

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR: Failed to register event")
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Exception message: ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Log.d(TAG, "=========================================")

            Result.failure(Exception("Gagal mendaftar event: ${e.message}"))
        }
    }

    /**
     * Get specific event registration
     */
    suspend fun getEventRegistration(eventId: String): EventRegistration? = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "No user logged in")
                return@withContext null
            }

            val doc = firestore.collection("users")
                .document(currentUser.uid)
                .collection("event_registrations")
                .document(eventId)
                .get()
                .await()

            doc.toObject(EventRegistration::class.java)

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to get event registration", e)
            null
        }
    }

    /**
     * Get specific user event registration (for ticket display)
     */
    suspend fun getUserEventRegistration(userId: String, eventId: String): EventRegistration? = withContext(Dispatchers.IO) {
        return@withContext try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("event_registrations")
                .document(eventId)
                .get()
                .await()

            doc.toObject(EventRegistration::class.java)

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to get user event registration", e)
            null
        }
    }

    /**
     * Get all user's event registrations (real-time)
     */
    fun getUserEventRegistrations(): Flow<List<EventRegistration>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "No user logged in")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Listening to user event registrations: ${currentUser.uid}")

        val listener = firestore.collection("users")
            .document(currentUser.uid)
            .collection("event_registrations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "ERROR: Listening to registrations failed", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val registrations = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(EventRegistration::class.java)
                        } catch (e: Exception) {
                            Log.e(TAG, "ERROR: Failed to parse registration", e)
                            null
                        }
                    }.sortedByDescending { it.registeredAt }

                    Log.d(TAG, "Received ${registrations.size} event registrations")
                    trySend(registrations)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose {
            Log.d(TAG, "Removing event registrations listener")
            listener.remove()
        }
    }

    /**
     * Cancel event registration
     */
    suspend fun cancelEventRegistration(eventId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.failure(Exception("Silakan login terlebih dahulu"))
            }

            firestore.collection("users")
                .document(currentUser.uid)
                .collection("event_registrations")
                .document(eventId)
                .update("status", "cancelled")
                .await()

            Log.d(TAG, "✓ Event registration cancelled: $eventId")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to cancel event", e)
            Result.failure(Exception("Gagal membatalkan event: ${e.message}"))
        }
    }

    suspend fun markEventAsCompleted(qrContent: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "❌ No user logged in")
                return@withContext Result.failure(Exception("Silakan login terlebih dahulu"))
            }

            Log.d(TAG, "========== MARKING EVENT AS COMPLETED ==========")
            Log.d(TAG, "QR Content: '$qrContent'")
            Log.d(TAG, "Current User ID: '${currentUser.uid}'")

            // ✅ Parse QR content: userId_eventId
            val parts = qrContent.split("_", limit = 2)

            if (parts.size < 2) {
                Log.e(TAG, "❌ Invalid QR format. Expected: userId_eventId")
                Log.e(TAG, "Received: $qrContent")
                return@withContext Result.failure(Exception("QR code format tidak valid"))
            }

            val qrUserId = parts[0]
            val eventId = parts[1]

            Log.d(TAG, "Parsed QR:")
            Log.d(TAG, "  User ID from QR: '$qrUserId'")
            Log.d(TAG, "  Event ID from QR: '$eventId'")

            // ✅ Self check-in validation: user must own the ticket
            if (qrUserId != currentUser.uid) {
                Log.e(TAG, "❌ User ID mismatch!")
                Log.e(TAG, "  QR User: '$qrUserId'")
                Log.e(TAG, "  Current User: '${currentUser.uid}'")
                return@withContext Result.failure(Exception("QR code ini bukan milik Anda"))
            }

            Log.d(TAG, "✓ User ID match verified")

            // ✅ Build document path
            val docPath = "users/${currentUser.uid}/event_registrations/$eventId"
            Log.d(TAG, "Document path: '$docPath'")

            val docRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("event_registrations")
                .document(eventId)

            // ✅ Check if document exists
            Log.d(TAG, "Checking if document exists...")
            val docSnapshot = docRef.get().await()

            if (!docSnapshot.exists()) {
                Log.e(TAG, "❌ Document NOT FOUND!")
                Log.e(TAG, "Searched path: '$docPath'")
                Log.e(TAG, "Event ID used: '$eventId'")

                // ✅ Debug: List all documents in collection
                val allDocs = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("event_registrations")
                    .get()
                    .await()

                Log.d(TAG, "Available documents in event_registrations:")
                allDocs.documents.forEach { doc ->
                    Log.d(TAG, "  - Document ID: '${doc.id}'")
                }

                return@withContext Result.failure(
                    Exception("Registrasi event tidak ditemukan. Pastikan Anda sudah terdaftar untuk event ini.")
                )
            }

            Log.d(TAG, "✓ Document found!")

            // ✅ Get current status
            val currentStatus = docSnapshot.getString("status")
            val eventTitle = docSnapshot.getString("eventTitle")

            Log.d(TAG, "Document data:")
            Log.d(TAG, "  Event Title: '$eventTitle'")
            Log.d(TAG, "  Current Status: '$currentStatus'")

            if (currentStatus == "completed") {
                Log.d(TAG, "⚠️ Event already completed")
                return@withContext Result.failure(Exception("Tiket sudah di-scan sebelumnya"))
            }

            // ✅ Update to completed
            Log.d(TAG, "Updating status to 'completed'...")
            docRef.update(
                mapOf(
                    "status" to "completed",
                    "completedAt" to System.currentTimeMillis()
                )
            ).await()

            Log.d(TAG, "✅ Event marked as completed successfully!")
            Log.d(TAG, "  Event ID: '$eventId'")
            Log.d(TAG, "  Event Title: '$eventTitle'")
            Log.d(TAG, "===============================================")

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR: Failed to mark event as completed")
            Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Exception message: ${e.message}")
            e.printStackTrace()
            Log.d(TAG, "===============================================")

            Result.failure(Exception("Gagal memverifikasi tiket: ${e.message}"))
        }
    }

    suspend fun isUserRegistered(eventId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val registration = getEventRegistration(eventId)
            registration != null && registration.status == "registered"
        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to check registration status", e)
            false
        }
    }

    /**
     * Calculate distance for EventRegistration
     */
    suspend fun calculateDistanceToRegistration(registration: EventRegistration): String {
        return calculateDistanceToEvent(registration.eventLatitude, registration.eventLongitude)
    }

    /**
     * Get registration count for event (for capacity management)
     */
    suspend fun getEventRegistrationCount(eventId: String): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collectionGroup("event_registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "registered")
                .get()
                .await()

            snapshot.size()

        } catch (e: Exception) {
            Log.e(TAG, "ERROR: Failed to get registration count", e)
        }
    }

    // Add to EventRepository.kt
    suspend fun debugListUserRegistrations(): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e(TAG, "No user logged in")
                return@withContext emptyList()
            }

            Log.d(TAG, "========== LISTING USER REGISTRATIONS ==========")
            Log.d(TAG, "User ID: ${currentUser.uid}")

            val snapshot = firestore.collection("users")
                .document(currentUser.uid)
                .collection("event_registrations")
                .get()
                .await()

            val docIds = snapshot.documents.map { it.id }

            Log.d(TAG, "Found ${docIds.size} registrations:")
            docIds.forEach { id ->
                Log.d(TAG, "  - Document ID: '$id'")
            }
            Log.d(TAG, "================================================")

            docIds

        } catch (e: Exception) {
            Log.e(TAG, "Error listing registrations", e)
            emptyList()
        }
    }
}