
```markdown
```
*Lire en français: Français[readme.fr.md]*
<div align="center">
  <!-- Generic cover image (Unsplash: abstract, network, fluid) -->
  <h1>🌊 Percolation Threshold Estimation</h1>
  
  <p>
    <b>Monte Carlo simulation and algorithmic optimization (Union-Find) in Java.</b>
  </p>

  <!-- Technical badges -->
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/Algorithm-Union--Find-blue.svg" alt="Union-Find">
  <img src="https://img.shields.io/badge/Method-Monte--Carlo-brightgreen.svg" alt="Monte-Carlo">
</div>

<br/>

This project explores the percolation phenomenon through a two-dimensional grid and aims to estimate its critical threshold using the Monte Carlo method. 

The main objective is not only to obtain this mathematical result but also to **progressively optimize the underlying algorithm**. We will transition from a naive pathfinding approach (highly time-consuming) to an optimized data structure (`Union-Find` with path compression).

---

## ☕ The Intuition behind Percolation

Imagine a porous stone, or a coffee filter. If water is poured on top, will it manage to flow through the structure and come out at the bottom? 

The grid is initially full of solid matter (white cells). We randomly remove matter (black cells). At what exact percentage of empty space does the water find a continuous path from top to bottom? This percentage is the **percolation threshold**.

```text
  [ Water ]
     |
     v
  - * * - -      (Legend: '-' solid, '*' empty)
  - - * * -
  * - - * *
  * * - - *
  - * * * *
     |
     v
[ Outlet ] -> Successful percolation!
```
---

## 🛤️ Implementation Evolution

### 🐢 1. Modeling and Naive Approach

**The concept:** 
We model an $N \times N$ grid using a 1-dimensional array of size $N^2$ for memory contiguity purposes. At each step of a Monte Carlo simulation, we blacken a random cell.

**The naive algorithm:**
To check if the system percolates, we perform a recursive Depth First Search (DFS) from the top row. If we reach the bottom row by stepping only on black cells, the system percolates.

```java
// Snippet from the recursive DFS approach
static boolean detectPath(boolean[] seen, int n, boolean up) {
    // ... if cell n is already on the target row ...
    if (n / size == targetRow) return true;

    // Exploration in the 4 directions
    if ((n - size >= 0) && ...) if (detectPath(seen, n - size, up)) return true;
    if ((n - 1 >= 0) && (n-1)/size == n / size)  if (detectPath(seen, n - 1, up)) return true;
    // ...
}
```

> **⚠️ The problem:** The time complexity is poor. Rerunning a full grid traversal for every newly blackened cell can quickly overflow the call stack for large grids. We need a data structure to keep track of the system's state.

### 🔗 2. The Union-Find Structure

**The concept:** 
Instead of searching for a path from scratch at each iteration, we update **equivalence classes**. Each connected set of black cells forms an equivalence class. When a cell is blackened, we unite it with its black neighbors. The system percolates if a cell on the top row belongs to the same class as a cell on the bottom row.

**The method:**
Each cell points to a "parent". The parent of a group is its representative (the root).
- `find(x)`: finds the root of cell `x`'s group.
- `union(x, y)`: connects the root of `x` to the root of `y`.

```java
// Quick union (but potentially unbalanced trees)
static int fastUnion(int x, int y) {
    equiv[find(x)] = find(y);
    return find(y);
}
```

> **⚠️ The problem:** Although faster than the naive method, consecutive unions can create very deep "trees" (resembling linked lists). The `find()` function then takes linear time.

### ⚡ 3. Weighted Trees and Path Compression

**The concept:** 
To prevent connection trees from becoming too deep, we apply two major optimizations:
1. **Weighting by height:** During a union, we always attach the smaller tree under the root of the larger tree. This way, the resulting tree's depth will not increase but will be equal to that of the larger tree.
2. **Path compression:** When searching for the root of an element, we take the opportunity to flatten the tree by attaching the element directly to its grandparent.

```java
// Search with path compression
static int logFind(int x) {
    int j = x;
    while (j != equiv[j]) {
        equiv[j] = equiv[equiv[j]]; // Shortcut to the grandparent
        j = equiv[j];
    }
    return j;
}
```
*The search and union time then becomes near-constant (logarithmic).*

### 🚀 4. The Final Trick: Virtual Nodes

**The concept:** 
Even with an optimized search algorithm, checking for percolation still requires comparing every cell on the top row with every cell on the bottom row (which means $N \times N$ comparisons).

**The solution:**
We increase the size of our `UnionFind` array by 2 cells.
- Index $N^2$ represents a **top virtual node**, automatically connected to any blackened cell on the first row.
- Index $N^2 + 1$ represents a **bottom virtual node**, automatically connected to any blackened cell on the last row.

Now, verifying percolation across millions of cells comes down to **a single $O(1)$ comparison**:

```java
// The ultimate percolation check
static boolean isLogPercolation(int n) {
    return UnionFind.find(length - 1) == UnionFind.find(length - 2);
}
```

---

## 🛠️ Running the Simulation

### Prerequisites
- Java Development Kit (JDK) 8 or higher.

### Execution
1. Clone this repository.
2. Compile the Java files from the root:
   ```bash
   javac Percolation/*.java
   ```
3. Run the simulation by specifying the desired number of Monte Carlo iterations (e.g., 10,000):
   ```bash
   java Percolation.Percolation 10000
   ```

**Expected output:** The program will return the estimated percolation threshold (which theoretically converges to `~0.592`) as well as the execution time.
```
