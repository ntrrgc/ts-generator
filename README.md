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

Sometimes you want to work with Java classes like this one:

