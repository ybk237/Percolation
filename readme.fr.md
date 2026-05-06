
```markdown
```
*English version: English[readme.md]*
<div align="center">
  <!-- Image de couverture générique (Unsplash : abstrait, réseau, fluide) -->
  <h1>🌊 Estimation du Seuil de Percolation</h1>
  
  <p>
    <b>Simulation de Monte-Carlo et optimisation algorithmique (Union-Find) en Java.</b>
  </p>

  <!-- Badges techniques -->
  <img src="https://img.shields.io/badge/Language-Java-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/Algorithm-Union--Find-blue.svg" alt="Union-Find">
  <img src="https://img.shields.io/badge/Method-Monte--Carlo-brightgreen.svg" alt="Monte-Carlo">
</div>

<br/>

Ce projet explore le phénomène de percolation à travers une grille en deux dimensions et cherche à en estimer le seuil critique via la méthode de Monte-Carlo. 

L'objectif principal n'est pas seulement d'obtenir ce résultat mathématique, mais aussi d'**optimiser progressivement l'algorithme** sous-jacent. Nous passerons d'une recherche de chemin naïve (très coûteuse en temps) à une structure de données optimisée (`Union-Find` avec compression de chemin).

---

## ☕ L'intuition de la Percolation

Imaginez une pierre poreuse, ou un filtre à café. Si l'on verse de l'eau au sommet, parviendra-t-elle à traverser la structure pour s'écouler par le bas ? 

La grille est initialement pleine de matière (cases blanches). Nous retirons de la matière aléatoirement (cases noires). À quel pourcentage exact de vide l'eau trouve-t-elle un chemin continu du haut vers le bas ? Ce pourcentage est le **seuil de percolation**.

```text
  [ Eau ]
     |
     v
  - * * - -      (Légende : '-' matière, '*' vide)
  - - * * -
  * - - * *
  * * - - *
  - * * * *
     |
     v
[ Sortie ] -> Percolation réussie !
```
---

## 🛤️ Évolution de l'implémentation

### 🐢 1. Modélisation et Approche Naïve

**Le principe :** 
Nous modélisons une grille de taille $N \times N$ à l'aide d'un tableau à une dimension de taille $N^2$ pour des raisons de contiguïté mémoire. À chaque étape d'une simulation de Monte-Carlo, nous noircissons une case aléatoire.

**L'algorithme naïf :**
Pour vérifier si le système percole, on effectue un parcours en profondeur (DFS - *Depth First Search*) récursif depuis la ligne du haut. Si l'on atteint la ligne du bas en ne marchant que sur des cases noires, le système percole.

```java
// Extrait de l'approche DFS récursive
static boolean detectPath(boolean[] seen, int n, boolean up) {
    // ... si la case n est déjà sur la ligne qu'on veut rejoindre ...
    if (n / size == targetRow) return true;

    // Exploration dans les 4 directions
    if ((n - size >= 0) && ...) if (detectPath(seen, n - size, up)) return true;
    if ((n - 1 >= 0) && (n-1)/size == n / size)  if (detectPath(seen, n - 1, up)) return true;
    // ...
}
```

> **⚠️ Le problème :** La complexité n'est pas bonne. Relancer un parcours complet de la grille à chaque nouvelle case noircie peut rapidement remplir la pile pour des grilles de grande taille. Il nous faut une structure de données pour garder une mémoire de l'état du système.

### 🔗 2. La structure Union-Find

**Le principe :** 
Plutôt que de chercher un chemin de zéro à chaque itération, nous mettons à jour les **classes d'équivalence**. Chaque ensemble connexe de cases noires forme une classe d'équivalence. Lorsqu'on noircit une case, on l'unit à ses voisines noires. Le système percole si une case de la ligne du haut appartient à la même classe qu'une case de la ligne du bas.

**La méthode :**
Chaque case pointe vers un "parent". Le parent d'un groupe est son représentant (la racine).
- `find(x)` : trouve la racine du groupe de la case `x`.
- `union(x, y)` : relie la racine de `x` à la racine de `y`.

```java
// Union rapide (mais arbres potentiellement déséquilibrés)
static int fastUnion(int x, int y) {
    equiv[find(x)] = find(y);
    return find(y);
}
```

> **⚠️ Le problème :** Bien que plus rapide que la méthode naïve, les enchaînements d'unions peuvent créer des "arbres" très profonds (ressemblant à des listes chaînées). La fonction `find()` prend alors un temps linéaire.

### ⚡ 3. Arbres pondérés et Compression de chemin

**Le principe :** 
Pour éviter que les arbres de connexion ne deviennent trop profonds, nous appliquons deux optimisations majeures :
1. **Pondération par la hauteur :** Lors d'une union, on attache toujours l'arbre le plus petit sous la racine de l'arbre le plus grand. Ainsi la profondeur de l'arbre résultant n'augmentera pas mais sera égale à celle de l'arbre le plus grand
2. **Compression de chemin :** Lorsqu'on cherche la racine d'un élément, on en profite pour aplatir l'arbre en rattachant directement l'élément à son grand-parent.

```java
// Recherche avec compression de chemin
static int logFind(int x) {
    int j = x;
    while (j != equiv[j]) {
        equiv[j] = equiv[equiv[j]]; // Raccourci vers le grand-parent
        j = equiv[j];
    }
    return j;
}
```
*Le temps de recherche et d'union devient alors quasi-constant (logarithmique).*

### 🚀 4. L'astuce finale : Les Noeuds Virtuels

**Le principe :** 
Même avec un algorithme de recherche optimisé, vérifier la percolation demande toujours de comparer chaque case de la ligne du haut avec chaque case de la ligne du bas (soit $N \times N$ comparaisons).

**La solution :**
Nous agrandissons la taille de notre tableau `UnionFind` de 2 cases.
- L'indice $N^2$ représente un **nœud virtuel haut**, connecté automatiquement à toute case noircie sur la première ligne.
- L'indice $N^2 + 1$ représente un **nœud virtuel bas**, connecté à toute case de la dernière ligne.

Désormais, vérifier la percolation à travers des millions de cases se résume à **une seule comparaison en $O(1)$** :

```java
// La vérification ultime de percolation
static boolean isLogPercolation(int n) {
    return UnionFind.find(length - 1) == UnionFind.find(length - 2);
}
```

---

## 🛠️ Lancer la simulation

### Prérequis
- Java Development Kit (JDK) 8 ou supérieur.

### Exécution
1. Clonez ce dépôt.
2. Compilez les fichiers Java depuis la racine :
   ```bash
   javac Percolation/*.java
   ```
3. Lancez la simulation en précisant le nombre d'itérations Monte-Carlo souhaitées (par exemple, 10 000) :
   ```bash
   java Percolation.Percolation 10000
   ```

**Sortie attendue :** Le programme retournera l'estimation du seuil de percolation (qui converge théoriquement vers `~0.592`) ainsi que le temps d'exécution.
```
