import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.ClassCastException

class PaymentDetailsTest {

    @Test
    fun `de-serializing payment details`() {
        val standardPaymentDetails = """{"id":1,"amount":"1.0","date":"02.03.2022"}"""
        val refundedPaymentDetails = """{"id":2,"amount":"1.0","date":"02.03.2022","cardName":"Scrooge McDuck"}"""

        assertDoesNotThrow {
            Json.decodeFromString(PaymentDetailsSerializer, standardPaymentDetails) as StandardPaymentDetails
        }

        assertThrows<ClassCastException> {
            Json.decodeFromString(PaymentDetailsSerializer, refundedPaymentDetails) as StandardPaymentDetails
        }

        assertDoesNotThrow {
            Json.decodeFromString(PaymentDetailsSerializer, refundedPaymentDetails) as CreditCardPaymentDetails
        }

        assertThrows<ClassCastException> {
            Json.decodeFromString(PaymentDetailsSerializer, standardPaymentDetails) as CreditCardPaymentDetails
        }
    }

    @Test
    fun `serializing payment details`() {
        getPaymentDetails().forEach { pd ->
            Json.encodeToString(PaymentDetailsSerializer, pd).also { json ->
                if (pd.javaClass != StandardPaymentDetails::class.java) {
                    assertTrue(json.contains("cardName"))
                } else {
                    assertFalse(json.contains("cardName"))
                }
            }
        }
    }
}
