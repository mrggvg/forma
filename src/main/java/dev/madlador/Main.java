package dev.madlador;

import java.util.*;

public class Main {
    public static void main(String[] args) {


//        StateO state = new StateO();
//        state.occupy(0, 2);
//        state.occupy(0, 3);
//        state.occupy(1, 3);
//        state.occupy(1, 2);
//        state.occupy(2, 3);
//        state.occupy(3, 2);
//        state.occupy(2, 1);
//        state.occupy(1, 1);
//        state.occupy(1, 0);
//        state.occupy(0, 0);
//
//
//        System.out.println(state);
//
//        state.getNextValidMoves();


        State s0 = States.place(new Move(0, 1));
        States.dump(s0);

        State s1 = States.place(s0, new Move(0, 8));
        States.dump(s1);





    }
}

class StateO {

    public final String RESET = "\u001B[38;5;0m";
    public final String BLUE = "\u001B[38;5;27m";
    public final String MAGENTA = "\u001B[38;5;201m";
    public static final String TILE = "⬛ ";

    private boolean isBlueNext = false;
    private long blue = 0;
    private long pink = 0;

    private int lmi = -1; // last move index

    public void occupy(int row, int col) {
        // todo: should check and make sure it is not out of bounds for 7x7 board

        lmi = row * 7 + col;

        if (isBlueNext) blue = blue | 1L << lmi;
        else pink = pink | 1L << lmi;

        isBlueNext = !isBlueNext;
    }


    public void getNextValidMoves() {

        if (lmi == -1) {
            // any square is available as next move
            return;
        }

        // --------------------------------------------------------------------------

        long outline = getOutlineMask(lmi);
        long occupied = blue | pink;
        long available = outline & ~occupied;
        Utils.print(available);

        // --------------------------------------------------------------------------

        // check if only one move available?
        // if dead end after applying that one, then it's that mighty edge case

        boolean isEdgeCase = false;

        // Just to check one move in the future, if dead end then edge case
        if (Long.bitCount(available) == 1) {
            int nmi = Long.numberOfTrailingZeros(available); // nmi -> next move index

            long checkOutline = getOutlineMask(nmi);
            long checkAvailable = checkOutline & ~occupied;
            if (checkAvailable == 0L) isEdgeCase = true;

            Utils.print(checkAvailable);
        }

        // In case that it is not edge case simply return available (not checkAvailable)
        if (!isEdgeCase) {

            // todo
            return;
        }


        // If we came here well... edge
        System.out.println("edge");
        Utils.print(occupied);

        // idea: for each occupied bit index, do call outline func, and merge results
        // good enough for now

        long allOccupiedOutline = 0L;
        for (int bi : getAllBitIndexes(occupied)) {
            allOccupiedOutline |= getOutlineMask(bi);
        }

        System.out.println("All occupied");
        Utils.print(allOccupiedOutline);

        System.out.println("Shifted up by 1");

        long island = allOccupiedOutline;

        long up = Utils.shiftBitboard(island, 'u', 1);
        long down = Utils.shiftBitboard(island, 'd', 1);
        long left = Utils.shiftBitboard(island, 'l', 1);
        long right = Utils.shiftBitboard(island, 'r', 1);

        // For diagonals
        long upLeft = Utils.shiftBitboard(Utils.shiftBitboard(island, 'u', 1), 'l', 1);
        long upRight = Utils.shiftBitboard(Utils.shiftBitboard(island, 'u', 1), 'r', 1);
        long downLeft = Utils.shiftBitboard(Utils.shiftBitboard(island, 'd', 1), 'l', 1);
        long downRight = Utils.shiftBitboard(Utils.shiftBitboard(island, 'd', 1), 'r', 1);

        // Combine all shifted versions (this is the "interior" - cells that have neighbors in all directions)
        long allShifts = up & down & left & right & upLeft & upRight & downLeft & downRight;

        // Border = island minus the fully interior cells
        long border = island & ~allShifts;
        Utils.print(border);

        // exclude that one available that leads to dead end, and also all occupied

        long multiPath = allOccupiedOutline ^ occupied ^ available;

        long candidate1 = multiPath & border;

        Utils.print(candidate1);
        Utils.print(multiPath ^ candidate1);


        // todo: now last step pretty much is to pick the shortest path sort of

        // pick a path that is closer to the lmi (last move index)
        int[][] multiPathGrid = bitboardToGrid(multiPath);
        Utils.printGrid(multiPathGrid);
        Utils.printNumberedGrid(multiPathGrid);


        int[][] graph = new int[49][49];

        int[][] orthogonal = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int[][] diagonal = new int[][]{{-1, -1}, {1, 1}, {1, -1}, {-1, 1}};

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {

                if (multiPathGrid[i][j] == 1) {

                    // orthogonal first
                    for (int k = 0; k < orthogonal.length; k++) {

                        int ci = i + orthogonal[k][0];
                        int cj = j + orthogonal[k][1];

                        if (ci < 0 || ci >= 7 || cj < 0 || cj >= 7) continue;

                        if (multiPathGrid[ci][cj] == 1) {
                            int org = i * 7 + j;
                            int dst = ci * 7 + cj;
                            graph[org][dst] = 1;
                        }
                    }

                    // diagonal: only add if the neighbor itself has no orthogonal
                    // connection back to this cell's orthogonal neighbors
                    // i.e., add diagonal edge if the neighbor is "isolated" orthogonally from us
                    for (int k = 0; k < diagonal.length; k++) {

                        int ci = i + diagonal[k][0];
                        int cj = j + diagonal[k][1];

                        if (ci < 0 || ci >= 7 || cj < 0 || cj >= 7) continue;

                        if (multiPathGrid[ci][cj] == 1) {
                            // check if this diagonal neighbor has any orthogonal neighbor that is also 1
                            boolean neighborHasOrt = false;
                            for (int m = 0; m < orthogonal.length; m++) {
                                int oi = ci + orthogonal[m][0];
                                int oj = cj + orthogonal[m][1];
                                if (oi < 0 || oi >= 7 || oj < 0 || oj >= 7) continue;
                                if (multiPathGrid[oi][oj] == 1) {
                                    neighborHasOrt = true;
                                    break;
                                }
                            }

                            // also check if current cell has any orthogonal neighbor
                            boolean thisHasOrt = false;
                            for (int m = 0; m < orthogonal.length; m++) {
                                int oi = i + orthogonal[m][0];
                                int oj = j + orthogonal[m][1];
                                if (oi < 0 || oi >= 7 || oj < 0 || oj >= 7) continue;
                                if (multiPathGrid[oi][oj] == 1) {
                                    thisHasOrt = true;
                                    break;
                                }
                            }

                            // add diagonal only if at least one side has no orthogonal connections
                            if (!thisHasOrt || !neighborHasOrt) {
                                int org = i * 7 + j;
                                int dst = ci * 7 + cj;
                                graph[org][dst] = 1;
                            }
                        }
                    }
                }
            }
        }
        Utils.debugGraph(graph);

        int[] path = Utils.shortestPath(graph, 14, 4);
        System.out.println(Arrays.toString(path));


        long valid = pathToBitboard(path);
        Utils.print(valid);
    }


    private long pathToBitboard(int[] path) {
        long bb = 0L;
        for (int bi : path) {
            bb |= 1L << bi;
        }
        return bb;
    }

    private int[][] bitboardToGrid(long bitboard) {
        int[][] grid = new int[7][7];
        for (int bi : getAllBitIndexes(bitboard)) grid[bi / 7][bi % 7] = 1;
        return grid;
    }


    private double distance(int x1, int y1, int x2, int y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }


    private List<Integer> getAllBitIndexes(long bitboard) {
        ArrayList<Integer> obis = new ArrayList<>();
        for (int i = 0; i < 49; i++) {
            if ((bitboard & 1L) > 0) obis.add(i);
            bitboard >>= 1;
        }
        return obis;
    }


    /**
     * Makes outline mask from the pos.
     *
     * @return mask
     */
    private long getOutlineMask(int bitIndex) {
        int row = bitIndex / 7;
        int col = bitIndex % 7;

        long mask = 0L;
        // check all 8 directions with boundary checks
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // skip the center cell

                int newRow = row + dr;
                int newCol = col + dc;

                // check if the new position is within bounds
                if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                    int bi = newRow * 7 + newCol;
                    mask |= 1L << bi;
                }
            }
        }
        return mask;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;


            if ((mask & blue) > 0) {
                sb.append(BLUE);
                sb.append(TILE);
                if ((i + 1) % 7 == 0) sb.append("\n");
                continue;
            }

            if ((mask & pink) > 0) {
                sb.append(MAGENTA);
                sb.append(TILE);
                if ((i + 1) % 7 == 0) sb.append("\n");
                continue;
            }

            sb.append(RESET);
            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }

        return sb.toString();
    }
}

class Utils {

    public static void print(long board) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;

            if ((mask & board) > 0) {
                sb.append("\u001B[38;5;46m").append("⬛ ");
            } else {
                sb.append("\u001B[38;5;0m").append("⬛ ");
            }

            if ((i + 1) % 7 == 0) sb.append("\n");
        }

        System.out.println(sb.append("\u001B[38;0m"));
    }


    public static void printGrid(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (grid[i][j] == 1) {
                    sb.append("🟩");
                } else {
                    sb.append("⬜");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public static void printNumberedGrid(int[][] grid) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int n = i * grid.length + j;
                if (n < 10) sb.append(" ");
                sb.append(n).append(" ");
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }


    public static long shiftBitboard(long bitboard, char direction, int s) {
        if (s <= 0 || s >= 7) return 0L; // invalid shift amount
        if (bitboard == 0L) return 0L; // nothing to shift

        long result = 0L;

        for (int i = 0; i < 49; i++) {
            if ((bitboard & (1L << i)) == 0) continue; // bit not set

            int row = i / 7;
            int col = i % 7;
            int newRow = row;
            int newCol = col;

            switch (direction) {
                case 'u':
                    newRow = row - s;
                    break;
                case 'd':
                    newRow = row + s;
                    break;
                case 'l':
                    newCol = col - s;
                    break;
                case 'r':
                    newCol = col + s;
                    break;
                default:
                    return 0L; // invalid direction
            }

            // Only add the bit if it's within bounds (clip out-of-bounds bits)
            if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                int newIndex = newRow * 7 + newCol;
                result |= (1L << newIndex);
            }
            // If out of bounds, the bit is simply clipped (not added to result)
        }

        return result;
    }



    public static int[] shortestPath(int[][] graph, int src, int dst) {
        int n = graph.length;

        if (src == dst) return new int[]{src};

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];

        // Initialize
        for (int i = 0; i < n; i++) parent[i] = -1;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(src);
        visited[src] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (graph[current][neighbor] == 1 && !visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;

                    if (neighbor == dst) {
                        return buildPath(parent, src, dst);
                    }

                    queue.add(neighbor);
                }
            }
        }

        return new int[0]; // no path found
    }

    private static int[] buildPath(int[] parent, int src, int dst) {
        // First pass: count path length
        int length = 0;
        int node = dst;
        while (node != -1) {
            length++;
            node = parent[node];
        }

        // Second pass: fill array from end to start
        int[] path = new int[length];
        node = dst;
        for (int i = length - 1; i >= 0; i--) {
            path[i] = node;
            node = parent[node];
        }

        return path;
    }


    public static void debugGraph(int[][] graph) {
        for (int i = 0; i < graph.length; i++) {
            List<Integer> neighbors = new ArrayList<>();
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] == 1) {
                    neighbors.add(j);
                }
            }
            if (!neighbors.isEmpty()) {
                System.out.println(i + " -> " + neighbors);
            }
        }
    }


}