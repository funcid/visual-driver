package adapter

import com.mongodb.ClientSessionOptions
import com.mongodb.async.SingleResultCallback
import com.mongodb.async.client.MongoClients
import com.mongodb.async.client.MongoCollection
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import com.mongodb.session.ClientSession
import me.func.protocol.Unique
import org.bson.Document
import ru.cristalix.core.GlobalSerializers
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MongoAdapter(val url: String, val database: String, val collection: String) {

    val UPSERT = UpdateOptions().upsert(true)
    var info: MongoCollection<Document>
    var session: ClientSession

    private fun connect(): CompletableFuture<ClientSession> {
        val client = MongoClients.create(url)
        info = client.getDatabase(database).getCollection(collection)

        val future = CompletableFuture<ClientSession>()

        client.startSession(
            ClientSessionOptions.builder()
                .causallyConsistent(true)
                .build()
        ) { response, throwable: Throwable? ->
            if (throwable != null) future.completeExceptionally(throwable)
            else future.complete(response)
        }

        return future
    }

    init {
        val client = MongoClients.create(url)
        info = client
            .getDatabase(database)
            .getCollection(collection)

        session = connect().get(10, TimeUnit.SECONDS)
    }

    inline fun <reified T : Unique> find(uuid: UUID): CompletableFuture<T> {
        val future = CompletableFuture<T>()

        info.find(session, Filters.eq("uuid", uuid.toString()))
            .first { result: Document?, throwable: Throwable? ->
                throwable.apply {
                    throwable?.printStackTrace()
                    future.complete(readDocument(result))
                }
            }
        return future
    }

    inline fun <reified T : Unique> save(unique: T) {
        save(listOf(unique))
    }

    inline fun <reified T : Unique> save(uniques: List<T>) {
        val models: MutableList<WriteModel<Document>> = ArrayList()
        for (unique in uniques) {
            val model: WriteModel<Document> = UpdateOneModel(
                Filters.eq("uuid", unique.getUuid().toString()),
                Document("\$set", Document.parse(GlobalSerializers.toJson(unique))),
                UPSERT
            )
            models.add(model)
        }
        if (models.isNotEmpty()) info.bulkWrite(session, models) { _, throwable: Throwable? ->
            throwable?.printStackTrace()
        }
    }

    inline fun <reified T : Unique> readDocument(document: Document?): T? {
        return if (document == null) null else GlobalSerializers.fromJson(document.toJson(), T::class.java)
    }
}