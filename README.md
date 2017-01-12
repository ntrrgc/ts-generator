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
 * Mapping types
 * Customizing class definitions via transformers
 * Parenthesis are placed only when they are needed to disambiguate
