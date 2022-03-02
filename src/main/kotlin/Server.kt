import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
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
                    getPaymentDetails().forEach {
                        tr {
                            td {
                                a("/${it.id}") {
                                    +"${it.id}"
                                }
                            }
                            td {
                                +it.date
                            }
                            td {
                                +it.amount.toString()
                            }
                            td {
                                +it.details
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getPaymentDetails(): List<PaymentDetails> {
    return listOf(
        StandardPaymentDetails(1, 1.0, "02.03.2022"),
        StandardPaymentDetails(2, 3.0, "02.03.2022"),
        CreditCardPaymentDetails(3, 1.0, "02.03.2022", "Scrooge McDuck"),
    )
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
        }

        routing {
            get("/{id}") {
                val id = call.parameters["id"]
                val paymentDetails = getPaymentDetails().find { it.id == id?.toInt() }
                call.respondHtml {
                    body {
                        div {
                            if (paymentDetails != null) {
                                +Json.encodeToString(PaymentDetailsSerializer, paymentDetails)
                            } else {
                                +"Could not find payment details for id '$id'"
                            }
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
