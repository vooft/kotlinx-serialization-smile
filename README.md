# kotlinx-serialization-smile
Kotlin Multiplatform library to support [Smile](https://en.wikipedia.org/wiki/Smile_(data_interchange_format)) format for kotlinx.serialization.

### Features to support

* Extra types
    * [ ] BigInteger
    * [ ] BigDecimal
* Extra configuration flags
    * [ ] Optional header 
    * [x] Shared property name flag
    * [ ] Raw binary flag (instead of 7-bit encoding)
    * [x] Shared String value flag
* Some expected behavior
    * [ ] Stop reading when 0xFF is found, except when it raw binary mode 
