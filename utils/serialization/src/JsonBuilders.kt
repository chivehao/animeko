package me.him188.animationgarden.utils.serialization

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray

@JvmName("toJsonArrayString")
fun List<String>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}

@JvmName("toJsonArrayInt")
fun List<Int>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}

@JvmName("toJsonArrayLong")
fun List<Long>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}

@JvmName("toJsonArrayFloat")
fun List<Float>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}

@JvmName("toJsonArrayDouble")
fun List<Double>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}

@JvmName("toJsonArrayBoolean")
fun List<Boolean>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (element in this@toJsonArray) {
            add(JsonPrimitive(element))
        }
    }
}
