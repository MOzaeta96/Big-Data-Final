import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.rabbitmq.client.Channel
import java.io.IOException
import java.util.concurrent.TimeoutException

object MessagePublisher {
    private const val QUEUE_NAME = "stock-data-queue"

    fun publish(message: String) {
        val factory = ConnectionFactory()
        factory.host = "localhost"
        factory.port = 5672

        var connection: Connection? = null
        var channel: Channel? = null

        try {
            connection = factory.newConnection()
            channel = connection.createChannel()

            // Declare a durable queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null)

            // Publish a persistent message
            val messageProperties = com.rabbitmq.client.MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("", QUEUE_NAME, messageProperties, message.toByteArray())
            println(" [x] Sent '$message'")
        } catch (e: IOException) {
            println(" [!] Failed to publish message due to IO error: ${e.message}")
        } catch (e: TimeoutException) {
            println(" [!] Connection timed out: ${e.message}")
        } catch (e: Exception) {
            println(" [!] Unexpected error: ${e.message}")
        } finally {
            try {
                channel?.close()
                connection?.close()
            } catch (e: Exception) {
                println(" [!] Error closing connection or channel: ${e.message}")
            }
        }
    }
}
