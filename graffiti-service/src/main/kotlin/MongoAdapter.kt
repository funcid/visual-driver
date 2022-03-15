@file:Suppress("UNCHECKED_CAST")

import com.mongodb.ClientSessionOptions
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes
import com.mongodb.reactivestreams.client.ClientSession
import data.FeatureUserStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import me.func.protocol.Unique
import me.func.protocol.personalization.FeatureUserData
import org.bson.Document
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.replaceUpsert
import java.util.*
import kotlin.reflect.KClass

class CollectionTypeNotRegisteredException(name: String) : RuntimeException(name)

class MongoAdapter(private val url: String, private val databaseName: String, private val collectionName: String) {

    val collections: MutableMap<KClass<out Unique>, CoroutineCollection<out Unique>> = hashMapOf()
    private var database: CoroutineDatabase
    var session: ClientSession

    init {
        runBlocking {
            withTimeout(10000L) {
                val client = KMongo.createClient(
                    MongoClientSettings.builder()
                        .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                        .applyConnectionString(ConnectionString(url))
                        .build()
                ).coroutine
                session = client.startSession(ClientSessionOptions.builder().causallyConsistent(true).build())
                database = client.getDatabase(databaseName)

                // Регистрирует типы, которые могут быть в коллекции $collectionName
                registerCollection<FeatureUserStorage>()

                // Создание индекса UUID для быстрого поиска
                collections.forEach { (_, value) ->
                    value.createIndex(Indexes.ascending("uuid"))
                    println("Created index for collection.")
                    println("Documents total: ${value.countDocuments()}")
                }
            }
        }
    }

    private inline fun <reified T : Unique> registerCollection() =
        collections.put(T::class, database.getCollection<T>(collectionName))

    inline fun <reified T : Unique> findCollection(): CoroutineCollection<T> = collections[T::class]?.run {
        return this as CoroutineCollection<T>
    } ?: throw CollectionTypeNotRegisteredException(T::class.simpleName ?: "null")

    suspend inline fun <reified T : Unique> find(uuid: UUID) =
        findCollection<T>().findOne(session, Filters.eq(uuid.toString()))

    suspend inline fun <reified T : Unique> save(unique: T) =
        findCollection<T>().replaceOne(session, Filters.eq(unique.uuid.toString()), unique, replaceUpsert())
}
