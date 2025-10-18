# Vanadium&reg; Avo&trade;

![Image](logo/full/avo_full.png)

---

### Syntax Sample

```kotlin
fun foo -> string {
    var str: string
    loop i 0 -> 10 {
    if i == 5 {
        str = str + "? "
        continue
    }
    if i > 8 {
        break
    }
    str = str + i + " "
}
    return str
}

foo()
```
