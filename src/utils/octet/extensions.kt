package utils.octet

import utils.bool.toInt
import kotlin.experimental.and


fun Byte.toBinary() : List<Int> {

        val bit0: Byte = 0b00000001.toByte()
        val bit1: Byte = 0b00000010.toByte()
        val bit2: Byte = 0b00000100.toByte()
        val bit3: Byte = 0b00001000.toByte()
        val bit4: Byte = 0b00010000.toByte()
        val bit5: Byte = 0b00100000.toByte()
        val bit6: Byte = 0b01000000.toByte()
        val bit7: Byte = 0b10000000.toByte()

        val bit0on = (bit0 and this) != 0.toByte()
        val bit1on = (bit1 and this) != 0.toByte()
        val bit2on = (bit2 and this) != 0.toByte()
        val bit3on = (bit3 and this) != 0.toByte()
        val bit4on = (bit4 and this) != 0.toByte()
        val bit5on = (bit5 and this) != 0.toByte()
        val bit6on = (bit6 and this) != 0.toByte()
        val bit7on = (bit7 and this) != 0.toByte()

        return listOf(bit7on, bit6on, bit5on, bit4on, bit3on, bit2on, bit1on, bit0on).map { it.toInt() }
}