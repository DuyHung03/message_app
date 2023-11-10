package com.example.message.util

class GenerateRandomID {
    companion object {
        fun generateRandomId(length: Int): String {
            val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val random = java.util.Random()
            val stringBuilder = StringBuilder(length)

            repeat(length) {
                val index = random.nextInt(characters.length)
                val character = characters[index]
                stringBuilder.append(character)
            }

            return stringBuilder.toString()
        }
    }

}
