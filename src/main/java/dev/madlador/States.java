package dev.madlador;

import java.util.*;

final public class States {

    /**
     * Precomputed neighbor masks for all 49 cells.
     * OUTLINES[i] contains bits set for all valid adjacent cells (up to 8) around bit index i.
     */
    private static final long[] OUTLINES = new long[49];

    static {
        for (int i = 0; i < 49; i++) {
            int row = i / 7;
            int col = i % 7;
            long mask = 0L;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = row + dr;
                    int nc = col + dc;
                    if (nr >= 0 && nr < 7 && nc >= 0 && nc < 7)
                        mask |= 1L << (nr * 7 + nc);
                }
            }
            OUTLINES[i] = mask;
        }
    }


    /**
     * Takes previous state and applies move, returning new state.
     *
     * @param previous state to apply move to
     * @param move     move to apply
     * @return new state with move applied
     * @throws IllegalArgumentException if previous is null
     */
    public static State place(State previous, Move move) {
        if (previous == null) throw new IllegalArgumentException("Previous state cannot be null.");

        // todo: validate that move can indeed be placed (in set of valid next moves)

        long b1 = previous.b1();
        long b2 = previous.b2();
        int next = previous.nextPlayer();

        if (next == 0) b1 = previous.b1() | move.toMask();
        else b2 = previous.b2() | move.toMask();

        return new State(b1, b2, move.toBitIndex());
    }

    /**
     * Creates initial state with first player's opening move.
     *
     * @param move first move to apply
     * @return new initial state
     */
    public static State place(Move move) {
        return new State(move.toMask(), 0L, move.toBitIndex());
    }

    public static boolean isStalemate(State state) {
        return moves(state).isEmpty();
    }

    /**
     * Returns all moves that can be applied to transition to the next state.
     *
     * @param state current game state
     * @return list of all valid and available moves given current state
     */
    public static List<Move> moves(State state) {

        long outline = outlineMask(state.lm());
        long occupied = state.b1() | state.b2();
        long available = outline & ~occupied;

        if (Long.bitCount(available) > 1) {
            List<Move> moves = extractMoves(available);
            List<Move> valid = new ArrayList<>();

            for (Move move : moves) {
                long checkOutline = outlineMask(move.toBitIndex());
                if ((checkOutline & ~occupied) != 0L) {
                    valid.add(move);
                }
            }

            // if filtering left us with valid moves, return them
            if (!valid.isEmpty()) return valid;

            // otherwise fall through to edge case handling below
        }

        if (Long.bitCount(available) >= 1) {

            // Check that there will really be dead end
            long checkOutline = outlineMask(Long.numberOfTrailingZeros(available));
            long checkAvailable = checkOutline & ~occupied;

            // Return early, now we know that it won't end in dead state
            if (checkAvailable > 0L) return extractMoves(available);

            // ---------------------------------------------- edge case
            occupied = occupied | available;
            long occupiedOutlined = outlineMask(getAllSetBitIndexes(occupied));

            long candidates = occupiedOutlined ^ occupied;

            int[][] graph = toGraph(toGrid(candidates));
            removeCycles(graph);
            return extractMoves(graphToBitboard(graph));
        }

        return new ArrayList<>();
    }

    private static long graphToBitboard(int[][] graph) {
        long bb = 0L;
        for (int i = 0; i < 49; i++) {
            for (int j = 0; j < 49; j++) {
                if (graph[i][j] == 1) {
                    bb |= (1L << i);
                    break; // no need to check other neighbors
                }
            }
        }
        return bb;
    }


    private static double distance(int x1, int y1, int x2, int y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    private static long toBitboard(int[] path) {
        long bb = 0L;
        for (int bi : path) {
            bb |= 1L << bi;
        }
        return bb;
    }

    private static int[] shortestPath(int[][] graph, int src, int dst) {
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


    // Returns graph as adj matrix
    private static int[][] toGraph(int[][] candidates) {
        int[][] graph = new int[49][49];

        int[][] orthogonal = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int[][] diagonal = new int[][]{{-1, -1}, {1, 1}, {1, -1}, {-1, 1}};

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {

                if (candidates[i][j] == 1) {

                    // orthogonal first
                    for (int k = 0; k < orthogonal.length; k++) {

                        int ci = i + orthogonal[k][0];
                        int cj = j + orthogonal[k][1];

                        if (ci < 0 || ci >= 7 || cj < 0 || cj >= 7) continue;

                        if (candidates[ci][cj] == 1) {
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

                        if (candidates[ci][cj] == 1) {
                            // check if this diagonal neighbor has any orthogonal neighbor that is also 1
                            boolean neighborHasOrt = false;
                            for (int m = 0; m < orthogonal.length; m++) {
                                int oi = ci + orthogonal[m][0];
                                int oj = cj + orthogonal[m][1];
                                if (oi < 0 || oi >= 7 || oj < 0 || oj >= 7) continue;
                                if (candidates[oi][oj] == 1) {
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
                                if (candidates[oi][oj] == 1) {
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
        return graph;
    }


    private static int[][] toGrid(long bitboard) {
        int[][] grid = new int[7][7];
        for (int bi : getAllSetBitIndexes(bitboard)) grid[bi / 7][bi % 7] = 1;
        return grid;
    }

    /**
     * Extracts set bits from bitboard and returns them as list of moves.
     *
     * @param bitboard bitboard to extract moves from
     * @return list of moves corresponding to set bits
     */
    private static List<Move> extractMoves(long bitboard) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 49; i++) {
            if ((bitboard & 1L) > 0) {
                moves.add(new Move(i / 7, i % 7));
            }
            bitboard >>= 1;
        }
        return moves;
    }

    private static long outlineMask(List<Integer> bitIndexes) {
        long result = 0L;
        for (int bi : bitIndexes) result |= outlineMask(bi);
        return result;
    }

    private static List<Integer> getAllSetBitIndexes(long bitboard) {
        ArrayList<Integer> obis = new ArrayList<>();
        for (int i = 0; i < 49; i++) {
            if ((bitboard & 1L) > 0) obis.add(i);
            bitboard >>= 1;
        }
        return obis;
    }

    // todo: don't really need this function
    private static long translateBitboard(long bitboard, char direction, int s) {
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

            if (newRow >= 0 && newRow < 7 && newCol >= 0 && newCol < 7) {
                int newIndex = newRow * 7 + newCol;
                result |= (1L << newIndex);
            }
            // If out of bounds, the bit is simply clipped (not added to result)
        }

        return result;
    }

    /**
     * Returns bitmask of all 8 surrounding cells for given bit index.
     *
     * @param bitIndex index of the cell (0-48)
     * @return mask with bits set for all valid neighbors
     */
    private static long outlineMask(int bitIndex) {
        return OUTLINES[bitIndex];
    }

    /**
     * Prints colored board representation of the state to console.
     * Blue = first player, Magenta = second player.
     */
    public static void dump(State state) {
        final String RESET = "\u001B[38;5;0m";
        final String BLUE = "\u001B[38;5;27m";
        final String BLUE_LIGHT = "\u001B[38;5;39m";
        final String MAGENTA = "\u001B[38;5;207m";
        final String MAGENTA_LIGHT = "\u001B[38;5;219m";
        final String TILE = "⬛ ";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            long mask = 1L << i;
            boolean isLast = mask == state.lastMoveMask();


            if ((state.b1() & mask) != 0) {
                if (isLast) {
                    sb.append(MAGENTA_LIGHT);
                } else {
                    sb.append(MAGENTA);
                }
            } else {
                if ((state.b2() & mask) != 0) {
                    if (isLast) {
                        sb.append(BLUE_LIGHT);
                    } else {
                        sb.append(BLUE);
                    }
                } else {
                    sb.append(RESET);
                }
            }

            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }

    /**
     * Prints colored board representation of a single bitboard to console.
     * Green = set bit, dark = unset.
     */
    private static void dump(long bitboard) {
        final String RESET = "\u001B[38;5;0m";
        final String GREEN = "\u001B[38;5;46m";
        final String TILE = "⬛ ";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 49; i++) {
            sb.append((bitboard & (1L << i)) != 0 ? GREEN : RESET);
            sb.append(TILE);
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }




    private static void dumpIndexed(long bitboard) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 49; i++) {
            sb.append(i).append(" ");
            if ((i + 1) % 7 == 0) sb.append("\n");
        }
        System.out.println(sb);
    }






    /**
     * Removes cycles from graph by removing the longer arc between pivot vertices.
     * Pivot vertices are those with connections outside the cycle.
     * Modifies graph in place.
     */
    private static void removeCycles(int[][] graph) {
        while (true) {
            int[] cycle = findCycle(graph);
            if (cycle == null) break;

            Set<Integer> cycleSet = new HashSet<>();
            for (int n : cycle) cycleSet.add(n);

            List<Integer> pivots = new ArrayList<>();
            for (int n : cycle) {
                if (hasExternalEdge(graph, n, cycleSet)) pivots.add(n);
            }

            // Skip cycles with fewer than two pivots
            if (pivots.size() < 2) {
                System.out.println("Skipping cycle with fewer than two pivots.");
                System.out.println("Cycle: " + Arrays.toString(cycle));
                System.out.println("Pivots: " + pivots);
                return;
            }

            // Now we are safe to access pivots.get(0) and pivots.get(1)
            int p1 = pivots.get(0);
            int p2 = pivots.get(1);

            List<Integer> arc1 = getArc(cycle, p1, p2);
            List<Integer> arc2 = getArc(cycle, p2, p1);

            List<Integer> longer = arc1.size() >= arc2.size() ? arc1 : arc2;
            for (int i = 0; i < longer.size() - 1; i++) {
                int a = longer.get(i);
                int b = longer.get(i + 1);
                graph[a][b] = 0;
                graph[b][a] = 0;
            }
        }
    }


    /**
     * Finds a cycle in the graph using DFS.
     * @return array of nodes forming the cycle, or null if none
     */
    private static int[] findCycle(int[][] graph) {
        boolean[] visited = new boolean[49];
        int[] parent = new int[49];
        Arrays.fill(parent, -1);

        for (int i = 0; i < 49; i++) {
            if (!visited[i] && hasEdge(graph, i)) {
                int[] cycle = findCycleDFS(graph, i, visited, parent);
                if (cycle != null) return cycle;
            }
        }
        return null;
    }

    /**
     * DFS cycle detection. Returns cycle nodes from detected node back to cycle start.
     */
    private static int[] findCycleDFS(int[][] graph, int node, boolean[] visited, int[] parent) {
        visited[node] = true;

        for (int neighbor = 0; neighbor < 49; neighbor++) {
            if (graph[node][neighbor] == 0) continue;

            if (!visited[neighbor]) {
                parent[neighbor] = node;
                int[] cycle = findCycleDFS(graph, neighbor, visited, parent);
                if (cycle != null) return cycle;
            } else if (neighbor != parent[node]) {
                List<Integer> cycleNodes = new ArrayList<>();
                int n = node;
                while (n != neighbor) {
                    cycleNodes.add(n);
                    n = parent[n];
                }
                cycleNodes.add(neighbor);
                return cycleNodes.stream().mapToInt(Integer::intValue).toArray();
            }
        }
        return null;
    }

    /**
     * Extracts arc from 'from' to 'to' following cycle order.
     */
    private static List<Integer> getArc(int[] cycle, int from, int to) {
        List<Integer> arc = new ArrayList<>();
        int i = 0;
        while (cycle[i] != from) i++;
        while (true) {
            arc.add(cycle[i % cycle.length]);
            if (cycle[i % cycle.length] == to) break;
            i++;
        }
        return arc;
    }

    /**
     * Checks if node has any edge to a vertex outside the cycle.
     */
    private static boolean hasExternalEdge(int[][] graph, int node, Set<Integer> cycleSet) {
        for (int i = 0; i < 49; i++) {
            if (graph[node][i] == 1 && !cycleSet.contains(i)) return true;
        }
        return false;
    }

    private static boolean hasEdge(int[][] graph, int node) {
        for (int i = 0; i < 49; i++)
            if (graph[node][i] == 1) return true;
        return false;
    }

    private static String dumpEdges(int[][] graph) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 49; i++)
            for (int j = i + 1; j < 49; j++)
                if (graph[i][j] == 1)
                    sb.append(i).append(" <-> ").append(j).append("\n");
        return sb.toString();
    }

}
