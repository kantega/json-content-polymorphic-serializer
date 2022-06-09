import kotlinx.serialization.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject


@Serializable(with = PaymentDetailsSerializer::class)
sealed class PaymentDetails {
    abstract val amount: Double
    abstract val date: String
    abstract val details: String
}

@Serializable
data class StandardPaymentDetails(
    override val amount: Double,
    override val date: String,
    override val details: String = "Payment of £$amount was completed."
    ) : PaymentDetails()

@Serializable
data class CreditCardPaymentDetails(
    override val amount: Double,
    override val date: String,
    val cardName: String,
    override val details: String = "Payment of £$amount was charged credit card: $cardName"
    ) : PaymentDetails()

object PaymentDetailsSerializer : JsonContentPolymorphicSerializer<PaymentDetails>(PaymentDetails::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "cardName" in element.jsonObject -> CreditCardPaymentDetails.serializer()
        else -> StandardPaymentDetails.serializer()
    }
}
