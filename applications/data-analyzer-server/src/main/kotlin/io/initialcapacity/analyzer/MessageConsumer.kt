import com.rabbitmq.client.*
import java.io.IOException
import java.util.concurrent.TimeoutException

object MessageConsumer {
    private const val QUEUE_NAME = "stock-data-queue"

    fun consume() {
        val factory = ConnectionFactory()
        factory.host = "localhost"
        factory.port = 5672

        try {
            val connection = factory.newConnection()
            val channel = connection.createChannel()

            // Declare the queue as durable
            channel.queueDeclare(QUEUE_NAME, true, false, false, null)
            println(" [*] Waiting for messages. To exit press CTRL+C")

            val deliverCallback = DeliverCallback { _, delivery ->
                try {
                    val message = String(delivery.body)
                    println(" [x] Received '$message'")
                    // Add data analysis logic here
                } catch (e: Exception) {
                    println(" [!] Error processing message: ${e.message}")
                }
            }

            channel.basicConsume(QUEUE_NAME, true, deliverCallback) { _ -> }

        } catch (e: IOException) {
            println(" [!] Failed to connect or consume messages due to IO error: ${e.message}")
        } catch (e: TimeoutException) {
            println(" [!] Connection timed out: ${e.message}")
        } catch (e: Exception) {
            println(" [!] Unexpected error: ${e.message}")
        }
    }
}
