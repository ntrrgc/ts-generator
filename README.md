#TypeScript definition generator.

Generates the content of a TypeScript definition file (.d.ts) that
covers a set of Kotlin and Java classes.

This is useful when data classes are serialized to JSON and
handled in a JS or TypeScript web frontend.

Supports:
 * Primitive types, with explicit int
 * Kotlin and Java classes
 * Data classes
 * Enums
 * Any type
 * Generic classes, without type erasure
 * Generic constraints
 * Class inheritance
 * Abstract classes
 * Lists as JS arrays
 * Maps as JS objects
 * Null safety, even inside composite types
 * Java beans
 * Mapping types
 * Customizing class definitions via transformers
 * Parenthesis are placed only when they are needed to disambiguate
 
## Installation

This library requires Kotlin 1.1, which is EAP at time of writing. This library has been tested with 1.1-M04. 

This library cannot work with Kotlin 1.0 as its reflection library is not powerful enough to do this transformation. 

See [this post](https://blog.jetbrains.com/kotlin/2016/12/kotlin-1-1-m04-is-here/) to see how install Kotlin 1.1 EAP.

Then you need to include this library in your project.


## Basic usage

First you need is your Kotlin or Java classes or interfaces, for instance:

```kotlin
enum class Rarity(val abbreviation: String) {
    Normal("N"),
    Rare("R"),
    SuperRare("SR"),
}

data class Card(
    val ref: String,
    val rarity: Rarity,
    val name: String,
    val description: String,
    val command: String?,
    val playCard: (() -> Unit)?
) {
    val generatedTitleLine = "*$name* [$rarity]"
}

data class Inventory(
    val cards: List<Card> = listOf()
)

data class Player(
    val name: String,
    val inventory: Inventory = Inventory(),
    val achievementsProgress: List<AchievementCompletionState> = listOf(),
    val notices: List<Notice> = listOf()
)

data class Notice(
    val dateTime: LocalDateTime,
    val text: String
)

data class Achievement(
    val ref: String,
    val title: String,
    val description: String,
    val measuredProperty: (player: Player) -> Int,
    val neededValue: Int
)

data class AchievementCompletionState(
    val achievementRef: String,
    val reachedValue: Int
)
```

Then use `TypeScriptGenerator` to generate the TypeScript definitions, like this:

```kotlin
fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            Player::class
        ),
        mappings = mapOf(
            LocalDateTime::class to "Date",
            LocalDate::class to "Date"
        )
    ).definitionsText)
}
```

You will get an output like this:

```typescript
interface AchievementCompletionState {
    achievementRef: string;
    reachedValue: int;
}

type Rarity = "Normal" | "Rare" | "SuperRare";

interface Card {
    command: string | null;
    description: string;
    generatedTitleLine: string;
    name: string;
    rarity: Rarity;
    ref: string;
}

interface Inventory {
    cards: Card[];
}

interface Notice {
    dateTime: Date;
    text: string;
}

interface Player {
    achievementsProgress: AchievementCompletionState[];
    inventory: Inventory;
    name: string;
    notices: Notice[];
}
```

## Advanced features

This generator can handle more complex data types. Some examples are shown below:

### Mapping types

Sometimes you want to map certain Kotlin or Java classes to native JS types, like `Date`. 

This can be done with the `mappings` argument of `TypeScriptGenerator`, as show in the first example. 

Note the types mapped with this feature are emitted as they were written without any further processing. This is intended to support native JS types not defined in the Kotlin or Java backend.

### Inheritance support

```kotlin
open class BaseClass(val a: Int)

class DerivedClass(val b: List<String>): BaseClass(4)

fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            DerivedClass::class
        )
    ).definitionsText)
}
```

The output is:

```typescript
interface BaseClass {
    a: int;
}

interface DerivedClass extends BaseClass {
    b: string[];
}
```

By default `Serializable` and `Comparable` are not emitted.

### Generics

```kotlin
class ContrivedExample<A, out B, out C: List<Any>>(
    private val a: A, 
    val b: B, 
    val c: C,
    val listOfPairs: List<Pair<Int, B>>)
    
fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            ContrivedExample::class
        )
    ).definitionsText)
}
```

The output is:

```typescript
interface Pair<A, B> {
    first: A;
    second: B;
}

interface ContrivedExample<A, B, C extends any[]> {
    b: B;
    c: C;
    listOfPairs: Pair<int, B>[];
}
```

### Maps as JS objects

```kotlin
data class CardRepository(
    val cardsByRef: Map<String, Card>)
```

The output is:

```typescript
type Rarity = "Normal" | "Rare" | "SuperRare";

interface Card {
    command: string | null;
    description: string;
    generatedTitleLine: string;
    name: string;
    rarity: Rarity;
    ref: string;
}

interface CardRepository {
    cardsByRef: { [key: string]: Card };
}
```

### Java beans

Sometimes you want to work with long boring Java classes like this one:

```java
public class JavaClass {
    private String name;
    private int[] results;
    private boolean finished;
    private char[][] multidimensional;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getResults() {
        return results;
    }

    public void setResults(int[] results) {
        this.results = results;
    }

    public boolean isFinished() {
        return finished;
    }

    public char[][] getMultidimensional() {
        return multidimensional;
    }

    public void setMultidimensional(char[][] multidimensional) {
        this.multidimensional = multidimensional;
    }
}
```

Even though its fields are private, they are accessible through getter methods. The generator knows this, so they are included in the definition:

```typescript
interface JavaClass {
    name: string;
    results: int[];
    multidimensional: string[][];
    finished: boolean;
}
```

### Transformers

Sometimes they objects you use in TypeScript or JavaScript are not exactly the same you use in your backend, but have some differences, for instance:

* You may transform one type into another.
* Your classes may use camelCase in the backend but being turned into snake_case in the frontend by the JSON serializer.
* Some properties of some classes may be not be sent to the frontend.

To support cases like these, `TypeScriptGenerator` supports class transformers. They are objects implementing the `ClassTransformer` interface, arranged in a pipeline. They can be used to customize the list of properties of a class and their name and type.

Below are some examples:

#### Filtering unwanted properties

In the following example, assume we don't want to emit `ref`:

```kotlin
data class Achievement(
    val ref: String,
    val title: String,
    val description: String,
    val measuredProperty: (player: Player) -> Int,
    val neededValue: Int
)
```

We can use the `transformPropertyList()` to remove it.

```kotlin
fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            Achievement::class
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyList(
                    properties: List<KProperty<*>>,
                    klass: KClass<*>
                ): List<KProperty<*>> {
                    return properties.filter { property ->
                        property.name != "ref"
                    }
                }
            }
        )
    ).definitionsText)
}
```

The output is:

```typescript
interface Achievement {
    description: string;
    neededValue: int;
    title: string;
}
```

#### Renaming to snake_case

You can use `transformPropertyName()` to rename any property.

The functions `camelCaseToSnakeCase()` and `snakeCaseToCamelCase()` are included in this library.

```kotlin
data class AchievementCompletionState(
    val achievementRef: String,
    val reachedValue: Int)

fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            AchievementCompletionState::class
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(
                    propertyName: String,
                    property: KProperty<*>,
                    klass: KClass<*>
                ): String {
                    return camelCaseToSnakeCase(propertyName)
                }
            }
        )
    ).definitionsText)
}
```

The output is:

```typescript
interface AchievementCompletionState {
    achievement_ref: string;
    reached_value: int;
}
```

#### Replacing types for some properties

Imagine in our previous example we don't want to emit `achievement_ref` with type `string`, but rather `achievement`, with type `Achievement`. 

We can use a combination of `transformPropertyName()` and `transformPropertyType()` for this purpose:

```typescript
fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            AchievementCompletionState::class
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyName(
                    propertyName: String, 
                    property: KProperty<*>, 
                    klass: KClass<*>
                ): String {
                    if (propertyName == "achievementRef") {
                        return "achievement"
                    } else {
                        return propertyName
                    }
                }

                override fun transformPropertyType(
                    type: KType, 
                    property: KProperty<*>, 
                    klass: KClass<*>
                ): KType {
                    // Note: property is the actual property from the class
                    // (unless replaced in transformPropertyList()), so
                    // it maintains the original property name declared
                    // in the code.
                    if (property.name == "achievementRef") {
                        return Achievement::class.createType(nullable = false)
                    } else {
                        return type
                    }
                }
            }
        )
    ).definitionsText)
}
```

The output is:

```typescript
interface Achievement {
    description: string;
    neededValue: int;
    ref: string;
    title: string;
}

interface AchievementCompletionState {
    achievement: Achievement;
    reachedValue: int;
}
```

Note how `Achievement` class is emitted recursively after the transformation has taken place, even though it was not declared in the original `AchievementCompletionState` class nor specified in `rootClasses`.

### Applying transformers only to some classes

Transformers are applied to all classes by default. If you want your transformers to apply only to classes matching a certain predicate, you can wrap them in an instance of `FilteredClassTransformer`. This is its definition:
 
 ```kotlin
class FilteredClassTransformer(
    val wrappedTransformer: ClassTransformer,
    val filter: (klass: KClass<*>) -> Boolean
): ClassTransformer
```

For the common case of applying a transformer only on a class and its subclasses if any, an extension method is provided, `.onlyOnSubclassesOf()`:
 
```kotlin
fun main(args: Array<String>) {
    println(TypeScriptGenerator(
        rootClasses = setOf(
            Achievement::class
        ),
        classTransformers = listOf(
            object : ClassTransformer {
                override fun transformPropertyList(
                    properties: List<KProperty<*>>,
                    klass: KClass<*>
                ): List<KProperty<*>> {
                    return properties.filter { property ->
                        property.name != "ref"
                    }
                }
            }.onlyOnSubclassesOf(Achievement::class)
        )
    ).definitionsText)
}
```