package Percolation;

public class Percolation {
    public static int size = 10;
    public static int length = size * size + 2;
    public static boolean[] grid = new boolean[length];

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int nb = Integer.parseInt(args[0]);
        print();
        System.out.println("\n" + monteCarlo(nb));
        System.out.println("Duration (ms): " + (System.currentTimeMillis() - start));
    }

    static void init() {
        for (int i = 0; i < length - 2; i++)
            grid[i] = false;
        grid[length - 2] = grid[length - 1] = true;
        UnionFind.init(length);
    }

    static void propagateUnion(int x) {
        if ((x + 1 >= 0) && (x + 1 < size * size) && ((x + 1) / size == x / size) && grid[x + 1])
            UnionFind.union(x, x + 1);
        if ((x - 1 >= 0) && (x - 1 < size * size) && ((x - 1) / size == x / size) && grid[x - 1])
            UnionFind.union(x - 1, x);
        if ((x - size >= 0) && grid[x - size])
            UnionFind.union(x - size, x);
        if ((x + size < size * size) && grid[x + size])
            UnionFind.union(x + size, x);
        if (x / size == 0)
            UnionFind.union(x, length - 2);
        if (x / size == size - 1)
            UnionFind.union(x, length - 1);
    }

    static void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i * size + j])
                    System.out.print("*");
                else
                    System.out.print("-");
            }
            System.out.println();
        }
    }

    static int randomShadow() {
        int n = (int) (Math.random() * size * size);
        while (grid[n]) {
            n = (int) (Math.random() * size * size);
        }
        grid[n] = true;
        propagateUnion(n);
        return n;
    }

    static double monteCarlo(int n) {
        double res = 0;
        for (int i = 0; i < n; i++)
            res += percolation();
        return res / n;
    }

    static double percolation() {
        init();
        int nbBlack = 0;
        boolean flag = false;
        while (!flag) {
            flag = isPercolation(randomShadow());
            nbBlack++;
        }
        return (double) nbBlack / (size * size);
    }

    static boolean isPercolation(int n) {
        return isLogPercolation(n);
    }

    static boolean isLogPercolation(int n) {
        return UnionFind.find(length - 1) == UnionFind.find(length - 2);
    }

    static boolean isFastPercolation(int n) {
        int i = 0;
        boolean flag1 = false, flag2 = false;
        
        while (i < size && !flag1) {
            flag1 = (UnionFind.find(i) == UnionFind.find(n));
            i++;
        }
        i = size * size - size;
        while (i < size * size && !flag2) {
            flag2 = (UnionFind.find(i) == UnionFind.find(n));
            i++;
        }
        return flag1 && flag2;
    }

    static boolean isNaivePercolation(int n) {
        return detectPath(new boolean[length], n, true) & detectPath(new boolean[length], n, false);
    }

    static boolean detectPath(boolean[] seen, int n, boolean up) {

        int targetRow = 0;
        if (!up)
            targetRow = size - 1;

        if (!grid[n] || seen[n])
            return false;

        seen[n] = true;

        if (n / size == targetRow)
            return true;

        if ((n - size >= 0) && (n - size < size * size)) {
            if (detectPath(seen, n - size, up))
                return true;
        }
        if ((n - 1 >= 0) && (n - 1 < size * size) && ((n - 1) / size == n / size)) {
            if (detectPath(seen, n - 1, up))
                return true;
        }
        if ((n + 1 >= 0) && (n + 1 < size * size) && ((n + 1) / size == n / size)) {
            if (detectPath(seen, n + 1, up))
                return true;
        }
        if ((n + size >= 0) && (n + size < size * size)) {
            if (detectPath(seen, n + size, up))
                return true;
        }
        return false;
    }
}
