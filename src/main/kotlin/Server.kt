import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.reflect.*
import kotlinx.html.*
import kotlinx.serialization.json.Json

fun HTML.index() {
    head {
        title("Payment Details")
    }
    body {
        style {
            +"""
                table, th, td {
                  padding: 5px;
                  border: 1px solid black;
                  border-collapse: collapse;
                }
            """.trimIndent()
        }

        div {
            table {
                thead {
                    tr {
                        th {
                            +"#"
                        }
                        th {
                            +"Date"
                        }
                        th {
                            +"Amount"
                        }
                        th {
                            +"Details"
                        }
                    }
                }
                tbody {
                    paymentDetails.forEachIndexed { index, item ->
                        tr {
                            td {
                                a("/payment/$index") {
                                    +"$index"
                                }
                            }
                            td {
                                +item.date
                            }
                            td {
                                +item.amount.toString()
                            }
                            td {
                                +item.details
                            }
                        }
                    }
                }
            }
        }
    }
}

var paymentDetails = mutableListOf(
    StandardPaymentDetails(1.0, "02.03.2022"),
    StandardPaymentDetails(3.0, "02.03.2022"),
    CreditCardPaymentDetails(1.0, "02.03.2022", "Scrooge McDuck"),
)

fun main() {
    embeddedServer(Netty, port = 5000, host = "127.0.0.1") {

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
        }

        routing {
            get("/payment/{idx}") {
                try {
                    val idx = call.parameters["idx"]
                    val paymentDetails = paymentDetails[idx?.toInt()!!]
                    call.respondHtml {
                        body {
                            div {
                                +Json.encodeToString(PaymentDetailsSerializer, paymentDetails)
                            }
                        }
                    }
                } catch (e: Exception) {
                    call.respondHtml(HttpStatusCode.NotFound) {
                        body {
                            div {
                                +"Could not find payment details"
                            }
                        }
                    }
                }
            }

            post("/payment") {
                try {
                    val details = call.receive<PaymentDetails>()
                    val type = if (paymentDetails.instanceOf(CreditCardPaymentDetails::class)) { "credit card" } else { "ordinary" }
                    if (paymentDetails.add(details)) {
                        call.respondHtml {
                            body {
                                div {
                                    +"Received $type payment details"
                                }
                            }
                        }
                    } else {
                        call.respondHtml(HttpStatusCode.BadRequest) {
                            body {
                                div {
                                    +"Could not add payment details"
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    call.respondHtml(HttpStatusCode.BadRequest) {
                        body {
                            div {
                                +"Could not read payment details"
                            }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
