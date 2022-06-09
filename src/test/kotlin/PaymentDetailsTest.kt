import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class PaymentDetailsTest {

    @Test
    fun `de-serializing payment details`() {
        val standardPaymentDetails = """{"amount":"1.0","date":"02.03.2022"}"""
        val cardPaymentDetails = """{"amount":"1.0","date":"02.03.2022","cardName":"Scrooge McDuck"}"""

        assertDoesNotThrow {
            Json.decodeFromString(standardPaymentDetails) as StandardPaymentDetails
        }

        assertFails {
            Json.decodeFromString(cardPaymentDetails) as StandardPaymentDetails
        }

        assertDoesNotThrow {
            Json.decodeFromString(cardPaymentDetails) as CreditCardPaymentDetails
        }

        assertFails {
            Json.decodeFromString(standardPaymentDetails) as CreditCardPaymentDetails
        }
    }

    @Test
    fun `serializing payment details`() {
        paymentDetails.forEach { pd ->
            Json.encodeToString(pd).also { json ->
                if (pd.javaClass != StandardPaymentDetails::class.java) {
                    assertTrue(json.contains("cardName"))
                } else {
                    assertFalse(json.contains("cardName"))
                }
            }
        }
    }
}
