# Vanadium&reg; Avo&trade;

![Image](logo/full/avo_full.png)

---

### Syntax Sample

```kotlin
module main

complex Pixel {
    r: int,
    g: int,
    b: int
}

complex Image {
    width: int,
    height: int,
    pixels: [[Pixel]]
}

fun sqrt(d: float) -> float {
    return internal sqrt(d)
}

fun rgb(r: int, g: int, b: int) -> Pixel {
    return new Pixel {
        r = r,
        g = g,
        b = b
    }
}

fun createImage(width: int, height: int, initF: ((int, int) -> Pixel)) -> Image {
    var pixels: [[Pixel]]

    # Initialize Pixel Buffer
    loop y 0 -> excl height {
        var row: [Pixel]
        loop x 0 -> excl width {
            row + initF(x, y)
        }
        pixels + row
    }

    return new Image {
        width = width,
        height = height,
        pixels = pixels
    }
}

fun writeImage(img: Image) {
    internal println("P3")
    internal println("" + img.width + " " + img.height)
    internal println("255")
    loop y 0 -> excl img.height {
        loop x 0 -> excl img.width {
            var px = img.pixels[y][x]
            internal print(px.r + " " + px.g + " " + px.b + " ")
        }
        internal println("")
    }
}

var width = 200
var height = 200

var img = createImage(width, height, fun (x: int, y: int) -> Pixel {
    if sqrt(((x - (width/2))^2 + (y - (height/2))^2) + 0.0) < 90 {
        return rgb(255, 0, 0)
    }
    return rgb(0, 0, 0)
})

fun main() {
    writeImage(img)
}
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
