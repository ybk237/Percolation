package Percolation;

public class UnionFind {
    static int length;
    static int size;
    static int[] equiv;
    static int[] height;

    static void init(int len) {
        length = len;
        equiv = new int[length];
        height = new int[length];
        size = (int) Math.sqrt(length);
        for (int i = 0; i < length; i++) {
            equiv[i] = i;
            height[i] = 1;
        }
    }

    static int naiveFind(int x) {
        return equiv[x];
    }

    static int naiveUnion(int x, int y) {
        int repX = naiveFind(x);
        int repY = naiveFind(y);
        
        if (repX != repY) {
            for (int i = 0; i < length; i++) {
                if (equiv[i] == repX) {
                    equiv[i] = repY;
                }
            }
        }
        return repY;
    }

    static int fastUnion(int x, int y) {
        int rootX = fastFind(x);
        int rootY = fastFind(y);
        
        if (rootX != rootY) {
            equiv[rootX] = rootY;
        }
        return rootY;
    }

    static int fastFind(int x) {
        int y = x;
        while (equiv[y] != y)
            y = equiv[y];
        return y;
    }

    static int logUnion(int x, int y) {
        int rootX = logFind(x);
        int rootY = logFind(y);
        
        if (rootX == rootY) {
            return rootX;
        }

        if (height[rootX] > height[rootY]) {
            equiv[rootY] = rootX;
            return rootX;
        } else if (height[rootX] < height[rootY]) {
            equiv[rootX] = rootY;
            return rootY;
        } else {
            equiv[rootX] = rootY;
            height[rootY]++;
            return rootY;
        }
    }

    static int logFind(int x) {
        int j = x;
        while (j != equiv[j]) {
            equiv[j] = equiv[equiv[j]];
            j = equiv[j];
        }
        return j;
    }

    static int find(int x) {
        return logFind(x);
    }

    static int union(int x, int y) {
        return logUnion(x, y);
    }
}
