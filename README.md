# Vanadium&reg; Avo&trade;

![Image](logo/full/avo_full.png)

---

### Syntax Sample

```kotlin
complex Greeting {
        name: string
}

fun greet(g: Greeting) {
    print("Hello, " + g.name + "!")
}

greet(new Greeting {
    name = "World"
})
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
