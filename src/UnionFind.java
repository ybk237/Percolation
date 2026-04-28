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
        int temp = naiveFind(x);
        for (int i = 0; i < length; i++) {
            if (equiv[i] == temp)
                equiv[i] = naiveFind(y);
        }
        return naiveFind(y);
    }

    static int fastUnion(int x, int y) {
        equiv[find(x)] = find(y);
        return find(y);
    }

    static int fastFind(int x) {
        int y = x;
        while (equiv[y] != y)
            y = equiv[y];
        return y;
    }

    static int logUnion(int x, int y) {
        if (height[find(x)] > height[find(y)]) {
            equiv[find(y)] = find(x);
            height[find(x)]++;
            return find(x);
        } else {
            equiv[find(x)] = find(y);
            height[find(y)]++;
            return find(y);
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
