# BouncyScroll

A Jetpack Compose library that adds a spring-based overscroll (bounce) effect to scrollable containers. When the user reaches the edge of the list, the content stretches and snaps back with a natural spring animation.

## Usage

Replace a regular `Column` with `BouncyColumn`:

```kotlin
BouncyColumn {
    repeat(50) { index ->
        Text(text = "Item $index")
    }
}
```

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `overscrollLimit` | `Float` | `300f` | Maximum displacement in pixels the content can be pulled beyond the edge |
| `bounceDamping` | `Float` | `0.4f` | Fraction of the overscroll delta applied to the offset (lower = less stretch) |
| `stiffness` | `Float` | `Spring.StiffnessLow` | Spring stiffness controlling how fast the content snaps back |
| `dampingRatio` | `Float` | `Spring.DampingRatioMediumBouncy` | Spring damping ratio controlling how much the content oscillates on snap-back |
| `verticalArrangement` | `Arrangement.Vertical` | `Arrangement.Top` | Vertical arrangement of the column content |
| `horizontalAlignment` | `Alignment.Horizontal` | `Alignment.Start` | Horizontal alignment of the column content |

### Customising the spring feel

```kotlin
BouncyColumn(
    overscrollLimit = 200f,
    bounceDamping = 0.3f,
    stiffness = Spring.StiffnessMedium,
    dampingRatio = Spring.DampingRatioHighBouncy
) {
    // content
}
```

---

## Technical deep-dive: Compose Nested Scroll

BouncyScroll is built on top of the Compose **Nested Scroll** system. Understanding it is key to understanding how the bounce effect works.

### What is Nested Scroll?

Nested Scroll is a protocol in Compose that allows a hierarchy of scrollable nodes to coordinate scroll events. Each node can inspect, consume, or pass along scroll deltas before and after its children process them.

A node participates by attaching a `NestedScrollConnection` via the `nestedScroll` modifier:

```kotlin
Modifier.nestedScroll(connection)
```

### The scroll dispatch chain

When the user drags their finger, scroll events travel through the hierarchy in a fixed order:

```
1. onPreScroll  → parent connection (top-down, before children)
2.              → child scrollable (e.g. verticalScroll)
3. onPostScroll → parent connection (bottom-up, after children)
```

On fling (finger lift with velocity):

```
1. onPreFling   → parent connection
2.              → child scrollable
3. onPostFling  → parent connection
```

### NestedScrollConnection methods

#### `onPreScroll(available, source): Offset`

Called **before** the child scrollable gets the delta. The parent can consume part or all of `available` here.

- `available: Offset` — the raw drag delta for this frame (pixels moved since last frame, not accumulated)
- `source: NestedScrollSource` — `UserInput` for drag, `Fling` for momentum scroll
- **Returns** how much of `available` was consumed. The remainder is forwarded to the child.

In BouncyScroll, `onPreScroll` is used to **reduce an existing overscroll offset** before letting the child scroll. If the content is already displaced (e.g. pulled 150px down), and the user scrolls back up, this method absorbs just enough delta to bring the displacement back to zero — passing any leftover to the list so it can scroll normally.

```kotlin
// Clamp to [min(0, current), max(0, current)] so the offset can only
// move toward zero here, never cross it and create overscroll the other way.
val newOffset = (currentOffset + available.y)
    .coerceIn(minOf(0f, currentOffset), maxOf(0f, currentOffset))
val consumed = newOffset - currentOffset
```

#### `onPostScroll(consumed, available, source): Offset`

Called **after** the child has processed the delta.

- `consumed: Offset` — what the child actually consumed
- `available: Offset` — what the child could **not** consume (it hit an edge)
- **Returns** how much of the leftover `available` this node consumed.

In BouncyScroll, `onPostScroll` is where overscroll **begins**. When `available.y != 0` it means the list is at its edge and could not scroll further — this remainder is multiplied by `bounceDamping` and added to the displacement offset, stretching the content.

```kotlin
if (available.y != 0f) {
    val newOffset = (offsetY.value + available.y * bounceDamping)
        .coerceIn(-overscrollLimit, overscrollLimit)
    scope.launch { offsetY.snapTo(newOffset) }
}
```

#### `onPreFling(available): Velocity`

Called before the child handles the fling velocity. Rarely needed; return `Velocity.Zero` to let the child handle it fully.

#### `onPostFling(consumed, available): Velocity`

Called after the fling has settled. `available` is any leftover velocity the child could not use.

In BouncyScroll, this is where the **spring animation** runs — regardless of remaining velocity, the displacement is animated back to zero with a spring spec:

```kotlin
offsetY.animateTo(
    targetValue = 0f,
    animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness)
)
```

### Why `graphicsLayer` for the translation

The displacement is applied via `graphicsLayer { translationY = offsetY.value }` rather than `offset()`. `graphicsLayer` runs on the render thread and skips recomposition on every animation frame, making the spring animation smooth even under heavy load.

### Why `Animatable` instead of `animateFloatAsState`

`Animatable` gives imperative control: you can call `snapTo` during a drag (instant, no animation) and `animateTo` on release (spring animation), and switching between them cancels the previous operation automatically. `animateFloatAsState` is declarative and not suited for interruptible, gesture-driven animations.
