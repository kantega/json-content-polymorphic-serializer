# JSON Content Polymorphic Serializer #

## A brief introduction to polymorphism ##

Polymorphism is the provision of a single interface to entities of various types, which means applying multiple meanings or behaviours (implementations) to a single object type depending on the context.
In real life, one may think of a man implementing various behaviours as a father, a husband, a co-worker etc. depending on the situation.
- When I first learned object-oriented programming, the classical image of polymorphism portrayed the interface of a drawable object (a shape), where the various implementations would render e.g. a rectangle, triangle, circle etc.

The strengths of polymorphism come to play when whoever is using the object doesn't really care about the final shape or rendering of said object - We just want the damn drawing to render its final shape.
To do this, we call the interface's draw() function, rather than asking the object 'What shape are you?', before trying to call the appropriate drawTriangle(), drawRectangle(), drawCircle(), or any of the other implementations. 

## Polymorphism and JSON with Kotlinx ##

Traditionally, JSON polymorphic serialization requires the presence of a 'type' property.
Various implementation of object mappers have provided different approaches to handling polymorphic JSON, and most of them rely on this property.
[Jackson Annotations](https://www.tutorialspoint.com/jackson_annotations/jackson_annotations_jsontypeinfo.htm), for instance, will allow use of the '@JsonTypeInfo' to specify type information used for serialization and de-serialization. 

However, you may find yourself in a situation where you receive a JSON which lacks this 'type' property, and you need to interpret the content of the object before you de-serialize it.

Kotlinx allows for custom serialization without the presence of a dedicated discriminator, such as 'type', by use of the base class [JsonContentPolymorphicSerializer](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-content-polymorphic-serializer/index.html)

```kotlin
object PaymentDetailsSerializer : JsonContentPolymorphicSerializer<PaymentDetails>(PaymentDetails::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "cardName" in element.jsonObject -> CreditCardPaymentDetails.serializer()
        else -> StandardPaymentDetails.serializer()
    }
}
```

Here we will choose serializer based on the presence of the 'cardName' property.

```kotlin
Json.decodeFromString("""{"amount":"1.0","date":"02.03.2022"}""")
Json.decodeFromString("""{"amount":"1.0","date":"02.03.2022","cardName":"Scrooge McDuck"}""")
```

For this, we provide a simple, serializable, polymorphic class hierarchy by marking the abstract base class _sealed_, to prevent subclasses to be defined at runtime.
We also annotate the class _serializable_, and specify our custom serializer to allow for implicit (de-)serialization.
```kotlin
@Serializable(with = PaymentDetailsSerializer::class)
sealed class PaymentDetails {
    abstract val amount: Double
    abstract val date: String
    abstract val details: String
}
```

We implement our card details class as a serializable subclass.
```kotlin
@Serializable
class CardPaymentDetails (
    override val amount: Double,
    override val date: String,
    override val details: String,
    val cardName: String,
) : PaymentDetails()
```

For a closer look at the polymorphic serialization and de-serialization, you may find a simple web application and unit tests included.

The web application provides a get and post endpoint, which serializes and de-serializes json output/input.
The unit tests asserts that the json encoding/decoding behaves as expected.

## Caveat ##
Since JSON content is represented by JsonElement class and could be read only with [JsonDecoder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-decoder/index.html), this class only works with [Json](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json/index.html) format
