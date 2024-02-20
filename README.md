# BinLib

BinLib is a Kotlin library designed for flexible manipulation of binary data. 
It provides a robust set of tools for reading, writing, and managing structured binary data, 
making it ideal for applications involving binary file parsing, network protocol implementation, 
and data serialization/deserialization with precise control over the binary format.

## Features

- **Type-Safe Binary Structures**: Define complex data structures with various primitive and custom types.
- **Bit Field Manipulation**: Precise control over individual bits or groups of bits within bytes.
- **Dynamic and Exact String Handling**: Support for strings with variable or fixed lengths.
- **Nested Structures and Arrays**: Facilitate complex data representations with nested structures and arrays of types.
- **Endianess Support**: Compatible with both Big Endian and Little Endian byte orders.
- **Type Decorators**: Customize serialization/deserialization logic for advanced type conversions.

## Not a Feature
- Object Mapping
## Kotlin example

Define custom structures.


```kotlin
    // sub structure
    val int3Struct: Structure = struct(
        "i32" to Int32,
        "i16" to Int16,
        "i8" to Int8,
    )
    
    // parent structure
    val parentStruct = struct(
        "i8" to Int8,
        "struct" to int3Struct,
    )
```

Write data to the buffer.

```kotlin
    // allocate 4 +2 +1 +1 = 8 byte
    val buffer = ByteBuffer.allocate(8)
    // write data
    parentStruct.write(buffer, mapOf(
        "struct" to mapOf<String, Any>(
            "i32" to 0x0FFFFFF1,
            "i16" to 0x0FF1,
            "i8" to 0x01
        ),
        "i8" to 0x10))
```
Read data from the buffer.

```kotlin
    // prepare buffer for reading
    buffer.flip()
    // read data and flatten the map
    val map = parentStruct.read(buffer).flatten(".")
    // asserts
    assertEquals(0x0FFFFFF1, map["struct.i32"])
    assertEquals(0x0FF1, map["struct.i16"])
    assertEquals(0x01, map["struct.i8"])
    assertEquals(0x10, map["i8"])
```