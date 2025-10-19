# Vanadium&reg; Avo&trade;

![Image](logo/full/avo_full.png)

---

### Syntax Sample

```kotlin
fun sin(d: float) -> float {
    return internal sin(d)
}

print("sin(3.5) = " + sin(3.5))
```

### API Example
```kotlin
fun main() {
    Interpreter {
        sourcePath { "input.avo" }
        functionLoaderSource(InternalConsoleFunctions::class)
        functionLoaderSource(InternalMathFunctions::class)
        errorHandling {
            exitOnError()
            handlerImplementation { MordantErrorHandler }
        }
    }.exists {
        run()
    }
}
```
