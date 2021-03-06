import java.awt.*;
import java.util.*;

public class MinesweeperPlayer {
	/**
	 * Attempts to identify the locations of all mine tiles on a
	 * Minesweeper board.  If the method can deduce the locations of all the
	 * hidden mines without revealing any mine tiles, it should return a
	 * collection of all the hidden mine locations.  If the method reveals a
	 * mine tile during the course of its operation, it should return a
	 * collection of all the hidden mine locations deduced up to that point.
	 *
	 * @param board A Minesweeper board.
	 * @return A collection of locations on the specified board that contain
	 * mine tiles. Note: {@link MinesweeperBoard} uses a row/column
	 * coordinate system while {@link Point} uses an x/y coordinate system.
	 */

	public static Collection<Point> solve(MinesweeperBoard board) {    //x: column number, y: total number of rows - row number - 1
		int m, n, r_m, nm, i, j, s, N = board.getMines();
		final int h = board.getHeight(), w = board.getWidth(); 
		double score, min_score;
		Point p = null, pm = null;
		int[][] state = new int[h][w], sweeped = new int[h][w]; 
		Point[] neighbors = new Point[8], r_neighbors = new Point[8]; 
		LinkedList<Point> secure = new LinkedList<Point>();
		ArrayList<Point> mines = new ArrayList<Point>();
		HashSet<Point> rc = new HashSet<Point>();
		Iterator<Point> iter;
		for (i = 0; i < h; ++i){
			for (j = 0; j < w; ++j){
				rc.add(new Point(j, i));
				state[i][j] = MinesweeperBoard.HIDDEN;
				sweeped[i][j] = 0;
			}
		}
		while (N > 0 && !board.hasRevealedMine()){
			if (secure.isEmpty()){
				N -= sweep_board(w, h, state, sweeped, rc, mines, secure, board);
			}
			if (N > 0 && secure.isEmpty()){
				findPatterns(w, h, state, sweeped, rc, mines, secure, board);
			}
			if (N > 0 && secure.isEmpty()){
//System.out.println("guessing...");
				iter = rc.iterator();
				min_score = 1.0;
				i = 0;
				ArrayList<Point> min_pt = new ArrayList<Point>();
				while (iter.hasNext()){ 
					p = iter.next();
					if ((score = getScore(w, h, p, state)) < min_score){
						min_score = score;
						min_pt.clear(); 
						min_pt.add(p);
					}else if (score == min_score){
						min_pt.add(p);
					}
				}
				pm = min_pt.get(new Random().nextInt(min_pt.size())); 
				board.revealTile(pm.y, pm.x);
				rc.remove(pm);
				if ((state[pm.y][pm.x] = board.getTile(pm.y, pm.x)) == 0){	
					secure.add(pm);
				}
//if (!board.hasRevealedMine()){			
//printState(w, h, state);
//}
			}else if (N > 0){
				n = secure.size();
				while (n-- > 0){
					p = secure.remove();
					m = getUnknownNeighbors(w, h, state, p, neighbors/*, revealed*/);
					for (i = 0; i < m; ++i){
						board.revealTile(neighbors[i].y, neighbors[i].x);
						rc.remove(neighbors[i]);
						nm = board.getTile(neighbors[i].y, neighbors[i].x);
						state[neighbors[i].y][neighbors[i].x] = nm;
						if (nm == 0){
							secure.add(neighbors[i]);
						}
					}
				}
			}else{
				return mines;
			}
		}
//System.out.println("\nscore == " + mines.size());
		return mines;
	}

	private static int getNeighbors(final int w, final int h, Point p, Point[] neighbors){
		int n = 0;
		if (p.x > 0){
			neighbors[n++] = new Point(p.x - 1, p.y);
		}
		if (p.y > 0){
			neighbors[n++] = new Point(p.x, p.y - 1);
		}
		if (p.x > 0 && p.y > 0){
			neighbors[n++] = new Point(p.x - 1, p.y - 1);
		}
		if (p.x < w - 1){
			neighbors[n++] = new Point(p.x + 1, p.y);
		}
		if (p.y < h - 1){
			neighbors[n++] = new Point(p.x, p.y + 1);
		}
		if (p.x < w - 1 && p.y < h - 1){
			neighbors[n++] = new Point(p.x + 1, p.y + 1);
		}
		if (p.x < w - 1 && p.y > 0){
			neighbors[n++] = new Point(p.x + 1, p.y - 1);
		}
		if (p.x > 0 && p.y < h - 1){
			neighbors[n++] = new Point(p.x - 1, p.y + 1);
		}
		return n;
	}

	private static int getUnknownNeighbors(final int w, final int h, int[][] state, Point p, Point[] neighbors){
		int n = 0;
		if (p.x > 0){
			if (state[p.y][p.x - 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x - 1, p.y);
			}
		}
		if (p.y > 0){
			if (state[p.y - 1][p.x] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x, p.y - 1);
			}
		}
		if (p.x > 0 && p.y > 0){
			if (state[p.y - 1][p.x - 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x - 1, p.y - 1);
			}
		}
		if (p.x < w - 1){
			if (state[p.y][p.x + 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x + 1, p.y); 
			}
		}
		if (p.y < h - 1){
			if (state[p.y + 1][p.x] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x, p.y + 1);
			}
		}
		if (p.x < w - 1 && p.y < h - 1){
			if (state[p.y + 1][p.x + 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x + 1, p.y + 1);
			}
		}
		if (p.x < w - 1 && p.y > 0){
			if (state[p.y - 1][p.x + 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x + 1, p.y - 1);
			}
		}
		if (p.x > 0 && p.y < h - 1){
			if (state[p.y + 1][p.x - 1] == MinesweeperBoard.HIDDEN){
				neighbors[n++] = new Point(p.x - 1, p.y + 1);
			}
		}
		return n;
	}

	private static int sweep_board(final int w, final int h, int[][] state, int[][] sweeped, HashSet<Point> rc, ArrayList<Point> mines, LinkedList<Point> secure, MinesweeperBoard board){
		boolean end;
		int i, j, s, total = 0;
		int[] resweep = new int[1];
//System.out.println("sweeping...");
		do{
			s = 0;	
			resweep[0] = 0;
			end = true;
			for (i = 0; i < h; ++i){
				for (j = 0; j < w; ++j){
					if (state[i][j] > 0 && sweeped[i][j] == 0){
						s = sweep_cell(w, h, i, j, state, sweeped, resweep, rc, mines, secure, board);
						if (s != 0){ 
							total += s;
							end = false;
						}
					}
				}
			}
		}while (!end || resweep[0] == 1);
//if (total > 0){
//printState(w, h, state);
//}else{
//System.out.println("unable to make deduction");
//}
		return total;
	}

	private static int sweep_cell(final int w, final int h, final int row, final int col, int[][] state, int[][] sweeped, int[] resweep, HashSet<Point> rc, ArrayList<Point> mines, LinkedList<Point> secure, MinesweeperBoard board){
		int i, N = state[row][col], hidden = 0;
		Point[] s = new Point[8];
		if (col > 0){
			if (state[row][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row][col - 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col - 1, row);
			}
		}
		if (row > 0){
			if (state[row - 1][col] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col, row - 1);
			}

		}
		if (col > 0 && row > 0){
			if (state[row - 1][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col - 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col - 1, row - 1);
			}

		}
		if (col < w - 1){
			if (state[row][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row][col + 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col + 1, row);
			}

		}
		if (row < h - 1){
			if (state[row + 1][col] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col, row + 1);
			}

		}
		if (col < w - 1 && row < h - 1){
			if (state[row + 1][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col + 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col + 1, row + 1);
			}
		}
		if (col < w - 1 && row > 0){
			if (state[row - 1][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col + 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col + 1, row - 1);
			}

		}
		if (col > 0 && row < h - 1){
			if (state[row + 1][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col - 1] == MinesweeperBoard.HIDDEN){
				s[hidden++] = new Point(col - 1, row + 1);
			}

		}
		if (N == 0){
			for (i = 0; i < hidden; ++i){
				board.revealTile(s[i].y, s[i].x);
				if ((state[s[i].y][s[i].x] = board.getTile(s[i].y, s[i].x)) == 0){	
					secure.add(s[i]);
				}
				rc.remove(s[i]);
			}
			resweep[0] = 1;
		}
		if (hidden == N){
			for (i = 0; i < hidden; ++i){
				state[s[i].y][s[i].x] = MinesweeperBoard.MINE;
				mines.add(s[i]);
				rc.remove(s[i]);
			}
			sweeped[row][col] = 1;
			return N;
		}
		return 0;
	}

	private static void findPatterns(final int w, final int h, int[][] state, int[][] sweeped, HashSet<Point> rc, ArrayList<Point> mines, LinkedList<Point> secure, MinesweeperBoard board){
		boolean p;
		int i, j, s;
		do{
			s = 0;	/* 1-1 */
			for (i = 0; i < h; ++i){
				for (j = 0; j < w - 2; ++j){
					if (sweeped[i][j] != 1 && sweeped[i][j + 1] != 1 && state[i][j] == 1 && state[i][j + 1] == 1 && (j == 0 || (state[i][j - 1] != MinesweeperBoard.HIDDEN && state[i][j - 1] != MinesweeperBoard.MINE && (i == 0 || (state[i - 1][j - 1] != MinesweeperBoard.HIDDEN && state[i - 1][j - 1] != MinesweeperBoard.MINE)) && (i == h - 1 || (state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE))))){ 
						if (i > 0 && state[i - 1][j + 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 2, i - 1);
							board.revealTile(i - 1, j + 2);
							rc.remove(pt);
							if ((state[i - 1][j + 2] = board.getTile(i - 1, j + 2)) == 0){	
								secure.add(pt);
							}
							++s;
						}
						if (state[i][j + 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 2, i);
							board.revealTile(i, j + 2);
							rc.remove(pt);
							if ((state[i][j + 2] = board.getTile(i, j + 2)) == 0){	
								secure.add(pt);
							}
							++s;
						}
						if (i < h - 1 && state[i + 1][j + 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 2, i + 1);
							board.revealTile(i + 1, j + 2);
							rc.remove(pt);
							if ((state[i + 1][j + 2] = board.getTile(i + 1, j + 2)) == 0){	
								secure.add(pt);
							}
							++s;
						}
					}
				}
				for (j = 2; j < w; ++j){
					if (sweeped[i][j] != 1 && sweeped[i][j - 1] != 1 && state[i][j] == 1 && state[i][j - 1] == 1 && (j == w - 1 || (state[i][j + 1] != MinesweeperBoard.HIDDEN && state[i][j + 1] != MinesweeperBoard.MINE && (i == 0 || (state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE)) && (i == h - 1 || (state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE))))){ 
						if (i > 0 && state[i - 1][j - 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j - 2, i - 1);
							board.revealTile(i - 1, j - 2);
							rc.remove(pt);
							if ((state[i - 1][j - 2] = board.getTile(i - 1, j - 2)) == 0){	
								secure.add(pt);
							}
							++s;
						}
						if (state[i][j - 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j - 2, i);
							board.revealTile(i, j - 2);
							rc.remove(pt);
							if ((state[i][j - 2] = board.getTile(i, j - 2)) == 0){	
								secure.add(pt);
							}
							++s;
						}
						if (i < h - 1 && state[i + 1][j - 2] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j - 2, i + 1);
							board.revealTile(i + 1, j - 2);
							rc.remove(pt);
							if ((state[i + 1][j - 2] = board.getTile(i + 1, j - 2)) == 0){	
								secure.add(pt);
							}
							++s;
							
						}
					}
				}
				for (j = 0; j < w; ++j){
					for (i = 0; i < h - 2; ++i){
						if (sweeped[i][j] != 1 && sweeped[i + 1][j] != 1 && state[i][j] == 1 && state[i + 1][j] == 1 && (i == 0 || (state[i - 1][j] != MinesweeperBoard.HIDDEN && state[i - 1][j] != MinesweeperBoard.MINE && (j == 0 || (state[i - 1][j - 1] != MinesweeperBoard.HIDDEN && state[i - 1][j - 1] != MinesweeperBoard.MINE)) && (j == w - 1 || (state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE))))){ 
							if (j > 0 && state[i + 2][j - 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j - 1, i + 2);
								board.revealTile(i + 2, j - 1);
								rc.remove(pt);
								if ((state[i + 2][j - 1] = board.getTile(i + 2, j - 1)) == 0){	
									secure.add(pt);
								}
								++s;
							}
							if (state[i + 2][j] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j, i + 2);
								board.revealTile(i + 2, j);
								rc.remove(pt);
								if ((state[i + 2][j] = board.getTile(i + 2, j)) == 0){	
									secure.add(pt);
								}
								++s;
							}
							if (j < w - 1 && state[i + 2][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i + 2);
								board.revealTile(i + 2, j + 1);
								rc.remove(pt);
								if ((state[i + 2][j + 1] = board.getTile(i + 2, j + 1)) == 0){	
									secure.add(pt);
								}
								++s;
							}
						}
					}
					for (i = 2; i < h; ++i){
						if (sweeped[i][j] != 1 && sweeped[i - 1][j] != 1 && state[i][j] == 1 && state[i - 1][j] == 1 && (i == h - 1 || (state[i + 1][j] != MinesweeperBoard.HIDDEN && state[i + 1][j] != MinesweeperBoard.MINE && (j == 0 || (state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE)) && (j == w - 1 || (state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE))))){ 
							if (j > 0 && state[i - 2][j - 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j - 1, i - 2);
								board.revealTile(i - 2, j - 1);
								rc.remove(pt);
								if ((state[i - 2][j - 1] = board.getTile(i - 2, j - 1)) == 0){	
									secure.add(pt);
								}
								++s;
							}
							if (state[i - 2][j] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j, i - 2);
								board.revealTile(i - 2, j);
								rc.remove(pt);
								if ((state[i - 2][j] = board.getTile(i - 2, j)) == 0){	
									secure.add(pt);
								}
								++s;
							}
							if (j < w - 1 && state[i - 2][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i - 2);
								board.revealTile(i - 2, j + 1);
								rc.remove(pt);
								if ((state[i - 2][j + 1] = board.getTile(i - 2, j + 1)) == 0){	
									secure.add(pt);
								}
								++s;
							}
						}
					}
				}
			}/* 1-2 */
			for (i = 1; i < h; ++i){
				for (j = 0; j < w - 2; ++j){
					if (i == h - 1 || (state[i + 1][j] != MinesweeperBoard.HIDDEN && state[i + 1][j] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 2] != MinesweeperBoard.HIDDEN && state[i + 1][j + 2] != MinesweeperBoard.MINE)){
						if (sweeped[i][j] != 1 && sweeped[i][j + 1] != 1 && state[i][j] == 1 && state[i][j + 1] == 2 && state[i][j + 2] != MinesweeperBoard.MINE && state[i][j + 2] != MinesweeperBoard.HIDDEN){
							state[i - 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i - 1));
						}
						if (sweeped[i][j + 1] != 1 && sweeped[i][j + 2] != 1 && state[i][j + 1] == 2 && state[i][j + 2] == 1 && state[i][j] != MinesweeperBoard.MINE && state[i][j] != MinesweeperBoard.HIDDEN){
							state[i - 1][j] = MinesweeperBoard.MINE;
							mines.add(new Point(j, i - 1));
						}
					}
				}
			}
			for (i = 0; i < h - 1; ++i){
				for (j = 0; j < w - 2; ++j){
					if (i == 0 || (state[i - 1][j] != MinesweeperBoard.HIDDEN && state[i - 1][j] != MinesweeperBoard.MINE && state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE && state[i - 1][j + 2] != MinesweeperBoard.HIDDEN && state[i - 1][j + 2] != MinesweeperBoard.MINE)){
						if (sweeped[i][j] != 1 && sweeped[i][j + 1] != 1 && state[i][j] == 1 && state[i][j + 1] == 2 && state[i][j + 2] != MinesweeperBoard.MINE && state[i][j + 2] != MinesweeperBoard.HIDDEN){
							state[i + 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i + 1));
						}
						if (sweeped[i][j + 1] != 1 && sweeped[i][j + 2] != 1 && state[i][j + 1] == 2 && state[i][j + 2] == 1 && state[i][j] != MinesweeperBoard.MINE && state[i][j] != MinesweeperBoard.HIDDEN){
							state[i + 1][j] = MinesweeperBoard.MINE;
							mines.add(new Point(j, i + 1));
						}
					}
				}
			}
			for (j = 1; j < w; ++j){
				for (i = 0; i < h - 2; ++i){
					if (j == w - 1 || (state[i][j + 1] != MinesweeperBoard.HIDDEN && state[i][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 2][j + 1] != MinesweeperBoard.HIDDEN && state[i + 2][j + 1] != MinesweeperBoard.MINE)){
						if (sweeped[i][j] != 1 && sweeped[i + 1][j] != 1 && state[i][j] == 1 && state[i + 1][j] == 2 && state[i + 2][j] != MinesweeperBoard.MINE && state[i + 2][j] != MinesweeperBoard.HIDDEN){
							state[i + 2][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 2));
						}
						if (sweeped[i + 1][j] != 1 && sweeped[i + 2][j] != 1 && state[i + 1][j] == 2 && state[i + 2][j] == 1 && state[i][j] != MinesweeperBoard.MINE && state[i][j] != MinesweeperBoard.HIDDEN){
							state[i][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i));
						}
					}
				}
			}
			for (j = 0; j < w - 1; ++j){
				for (i = 0; i < h - 2; ++i){
					if (j == 0 || (state[i][j - 1] != MinesweeperBoard.HIDDEN && state[i][j - 1] != MinesweeperBoard.MINE && state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE && state[i + 2][j - 1] != MinesweeperBoard.HIDDEN && state[i + 2][j - 1] != MinesweeperBoard.MINE)){
						if (sweeped[i][j] != 1 && sweeped[i + 1][j] != 1 && state[i][j] == 1 && state[i + 1][j] == 2 && state[i + 2][j] != MinesweeperBoard.MINE && state[i + 2][j] != MinesweeperBoard.HIDDEN){
							state[i + 2][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 2));
						}
						if (sweeped[i + 1][j] != 1 && sweeped[i + 2][j] != 1 && state[i + 1][j] == 2 && state[i + 2][j] == 1 && state[i][j] != MinesweeperBoard.MINE && state[i][j] != MinesweeperBoard.HIDDEN){
							state[i][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i));
						}
					}
				}
			}
			/* 1-2-1*/
			for (i = 0; i < h; ++i){
				for (j = 0; j < w - 2; ++j){
					if (sweeped[i][j] == 1 || sweeped[i][j + 1] == 1 || sweeped[i][j + 2] == 1 || state[i][j] != 1 || state[i][j + 1] != 2 || state[i][j + 2] != 1){
						continue;
					}
					if (i > 0){
						if (i == h - 1 || (state[i + 1][j] != MinesweeperBoard.HIDDEN && state[i + 1][j] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 2] != MinesweeperBoard.HIDDEN && state[i + 1][j + 2] != MinesweeperBoard.MINE)){
							state[i - 1][j] = MinesweeperBoard.MINE; 
							mines.add(new Point(j, i - 1));
							if (state[i - 1][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i - 1);
								board.revealTile(i - 1, j + 1);
								if ((state[i - 1][j + 1] = board.getTile(i - 1, j + 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i - 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i - 1));
						}
					}
					if (i < h - 1){ 
						if (i == 0 || (state[i - 1][j] != MinesweeperBoard.HIDDEN && state[i - 1][j] != MinesweeperBoard.MINE && state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE && state[i - 1][j + 2] != MinesweeperBoard.HIDDEN && state[i - 1][j + 2] != MinesweeperBoard.MINE)){
							state[i + 1][j] = MinesweeperBoard.MINE;
							mines.add(new Point(j, i + 1));
							if (state[i + 1][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i + 1);
								board.revealTile(i + 1, j + 1);
								if ((state[i + 1][j + 1] = board.getTile(i + 1, j + 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i + 1));
						}	
					}
				}
			}
			for (i = 0; i < h - 2; ++i){
				for (j = 0; j < w; ++j){
					if (sweeped[i][j] == 1 || sweeped[i + 1][j] == 1 || sweeped[i + 2][j] == 1 || state[i][j] != 1 || state[i + 1][j] != 2 || state[i + 2][j] != 1){
						continue;
					}
					if (j > 0){
						if (j == w - 1 || (state[i][j + 1] != MinesweeperBoard.HIDDEN && state[i][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 2][j + 1] != MinesweeperBoard.HIDDEN && state[i + 2][j + 1] != MinesweeperBoard.MINE)){
							state[i][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i));
							if (state[i + 1][j - 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j - 1, i + 1);
								board.revealTile(i + 1, j - 1);
								if ((state[i + 1][j - 1] = board.getTile(i + 1, j - 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 2][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 2));
						}
					}
					if (j < w - 1){ 
						if (j == 0 || (state[i][j - 1] != MinesweeperBoard.HIDDEN && state[i][j - 1] != MinesweeperBoard.MINE && state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE && state[i + 2][j - 1] != MinesweeperBoard.HIDDEN && state[i + 2][j - 1] != MinesweeperBoard.MINE)){
							state[i][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i));
							if (state[i + 1][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i + 1);
								board.revealTile(i + 1, j + 1);
								if ((state[i + 1][j + 1] = board.getTile(i + 1, j + 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 2][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 2));
						}	
					}
				}
			} /* 1-2-2-1*/
			for (i = 0; i < h; ++i){
				for (j = 0; j < w - 3; ++j){
					if (sweeped[i][j] == 1 || sweeped[i][j + 1] == 1 || sweeped[i][j + 2] == 1 || sweeped[i][j + 3] == 1 || state[i][j] != 1 || state[i][j + 1] != 2 || state[i][j + 2] != 2 || state[i][j + 3] != 1){
						continue;
					}
					if (i > 0){
						if (i == h - 1 || (state[i + 1][j] != MinesweeperBoard.HIDDEN && state[i + 1][j] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 2] != MinesweeperBoard.HIDDEN && state[i + 1][j + 2] != MinesweeperBoard.MINE && state[i + 1][j + 3] != MinesweeperBoard.HIDDEN && state[i + 1][j + 3] != MinesweeperBoard.MINE)){
							if (state[i - 1][j] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j, i - 1);
								board.revealTile(i - 1, j);
								if ((state[i - 1][j] = board.getTile(i - 1, j)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i - 1][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i - 1));
							state[i - 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i - 1));
							if (state[i - 1][j + 3] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 3, i - 1);
								board.revealTile(i - 1, j + 3);
								if ((state[i - 1][j + 3] = board.getTile(i - 1, j + 3)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
						}
					}
					if (i < h - 1){ 
						if (i == 0 || (state[i - 1][j] != MinesweeperBoard.HIDDEN && state[i - 1][j] != MinesweeperBoard.MINE && state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE && state[i - 1][j + 2] != MinesweeperBoard.HIDDEN && state[i - 1][j + 2] != MinesweeperBoard.MINE && state[i - 1][j + 3] != MinesweeperBoard.HIDDEN && state[i - 1][j + 3] != MinesweeperBoard.MINE)){
							if (state[i + 1][j] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j, i + 1);
								board.revealTile(i + 1, j);
								if ((state[i + 1][j] = board.getTile(i + 1, j)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 1][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 1));
							state[i + 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i + 1));
							if (state[i + 1][j + 3] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 3, i + 1);
								board.revealTile(i + 1, j + 3);
								if ((state[i + 1][j + 3] = board.getTile(i + 1, j + 3)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
						}	
					}
				}
			}
			for (j = 0; j < w; ++j){
				for (i = 0; i < h - 3; ++i){
					if (sweeped[i][j] == 1 || sweeped[i + 1][j] == 1 || sweeped[i + 2][j] == 1 || sweeped[i + 3][j] == 1 || state[i][j] != 1 || state[i + 1][j] != 2 || state[i + 2][j] != 2 || state[i + 3][j] != 1){
						continue;
					}
					if (j > 0){
						if (j == w - 1 || (state[i][j + 1] != MinesweeperBoard.HIDDEN && state[i][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 2][j + 1] != MinesweeperBoard.HIDDEN && state[i + 2][j + 1] != MinesweeperBoard.MINE && state[i + 3][j + 1] != MinesweeperBoard.HIDDEN && state[i + 3][j + 1] != MinesweeperBoard.MINE)){
							if (state[i][j - 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j - 1, i);
								board.revealTile(i, j - 1);
								if ((state[i][j - 1] = board.getTile(i, j - 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 1][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 1));
							state[i + 2][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 2));
							if (state[i + 3][j - 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j - 1, i + 3);
								board.revealTile(i + 3, j - 1);
								if ((state[i + 3][j - 1] = board.getTile(i + 3, j - 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
						}
					}
					if (j < w - 1){ 
						if (j == 0 || (state[i][j - 1] != MinesweeperBoard.HIDDEN && state[i][j - 1] != MinesweeperBoard.MINE && state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE && state[i + 2][j - 1] != MinesweeperBoard.HIDDEN && state[i + 2][j - 1] != MinesweeperBoard.MINE && state[i + 3][j - 1] != MinesweeperBoard.HIDDEN && state[i + 3][j - 1] != MinesweeperBoard.MINE)){
							if (state[i][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i);
								board.revealTile(i, j + 1);
								if ((state[i][j + 1] = board.getTile(i, j + 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
							state[i + 1][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 1));
							state[i + 2][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 2));
							if (state[i + 3][j + 1] == MinesweeperBoard.HIDDEN){
								Point pt = new Point(j + 1, i + 3);
								board.revealTile(i + 3, j + 1);
								if ((state[i + 3][j + 1] = board.getTile(i + 3, j + 1)) == 0){
									secure.add(pt);
								}
								rc.remove(pt);
								++s;
							}
						}	
					}
				}
			}  /* 1-2-2-2-1 */
			for (i = 0; i < h; ++i){
				for (j = 0; j < w - 4; ++j){
					if (sweeped[i][j] == 1 || sweeped[i][j + 1] == 1 || sweeped[i][j + 2] == 1 || sweeped[i][j + 3] == 1 || sweeped[i][j + 4] == 1 || state[i][j] != 1 || state[i][j + 1] != 2 || state[i][j + 2] != 2 || state[i][j + 3] != 2 || state[i][j + 4] != 1){
						continue;
					}
					if (i > 0){
						if (i == h - 1 || (state[i + 1][j] != MinesweeperBoard.HIDDEN && state[i + 1][j] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 2] != MinesweeperBoard.HIDDEN && state[i + 1][j + 2] != MinesweeperBoard.MINE && state[i + 1][j + 3] != MinesweeperBoard.HIDDEN && state[i + 1][j + 3] != MinesweeperBoard.MINE && state[i + 1][j + 4] != MinesweeperBoard.HIDDEN && state[i + 1][j + 4] != MinesweeperBoard.MINE)){
							state[i - 1][j + 2] = MinesweeperBoard.MINE; 
							mines.add(new Point(j + 2, i - 1));
							if (j > 0){
								if (state[i - 1][j - 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j - 1, i - 1);
									board.revealTile(i - 1, j - 1);
									if ((state[i - 1][j - 1] = board.getTile(i - 1, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i][j - 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j - 1, i);
									board.revealTile(i, j - 1);
									if ((state[i][j - 1] = board.getTile(i, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
							if (j < w - 5){
								if (state[i - 1][j + 5] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j + 5, i - 1);
									board.revealTile(i - 1, j + 5);
									if ((state[i - 1][j + 5] = board.getTile(i - 1, j + 5)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i][j + 5] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j + 5, i);
									board.revealTile(i, j + 5);
									if ((state[i][j + 5] = board.getTile(i, j + 5)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
						}
					}
					if (i < h - 1){ 
						if (i == 0 || (state[i - 1][j] != MinesweeperBoard.HIDDEN && state[i - 1][j] != MinesweeperBoard.MINE && state[i - 1][j + 1] != MinesweeperBoard.HIDDEN && state[i - 1][j + 1] != MinesweeperBoard.MINE && state[i - 1][j + 2] != MinesweeperBoard.HIDDEN && state[i - 1][j + 2] != MinesweeperBoard.MINE && state[i - 1][j + 3] != MinesweeperBoard.HIDDEN && state[i - 1][j + 3] != MinesweeperBoard.MINE && state[i - 1][j + 4] != MinesweeperBoard.HIDDEN && state[i - 1][j + 4] != MinesweeperBoard.MINE)){
							state[i + 1][j + 2] = MinesweeperBoard.MINE; 
							mines.add(new Point(j + 2, i + 1));
							if (j > 0){
								if (state[i][j - 1] == MinesweeperBoard.HIDDEN){ 
									Point pt = new Point(j - 1, i);
									board.revealTile(i, j - 1);
									if ((state[i][j - 1] = board.getTile(i, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i + 1][j - 1] == MinesweeperBoard.HIDDEN){ 
									Point pt = new Point(j - 1, i + 1);
									board.revealTile(i + 1, j - 1);
									if ((state[i + 1][j - 1] = board.getTile(i + 1, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
							if (j < w - 5){
								if (state[i][j + 5] == MinesweeperBoard.HIDDEN){ 
									Point pt = new Point(j + 5, i);
									board.revealTile(i, j + 5);
									if ((state[i][j + 5] = board.getTile(i, j + 5)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i + 1][j + 5] == MinesweeperBoard.HIDDEN){ 
									Point pt = new Point(j + 5, i + 1);
									board.revealTile(i + 1, j + 5);
									if ((state[i + 1][j + 5] = board.getTile(i + 1, j + 5)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
						}	
					}
				}
			}
			for (j = 0; j < w; ++j){
				for (i = 0; i < h - 4; ++i){
					if (sweeped[i][j] == 1 || sweeped[i + 1][j] == 1 || sweeped[i + 2][j] == 1 || sweeped[i + 3][j] == 1 || sweeped[i + 4][j] == 1 || state[i][j] != 1 || state[i + 1][j] != 2 || state[i + 2][j] != 2 || state[i + 3][j] != 2 || state[i + 4][j] != 1){
						continue;
					}
					if (j > 0){
						if (j == w - 1 || (state[i][j + 1] != MinesweeperBoard.HIDDEN && state[i][j + 1] != MinesweeperBoard.MINE && state[i + 1][j + 1] != MinesweeperBoard.HIDDEN && state[i + 1][j + 1] != MinesweeperBoard.MINE && state[i + 2][j + 1] != MinesweeperBoard.HIDDEN && state[i + 2][j + 1] != MinesweeperBoard.MINE && state[i + 3][j + 1] != MinesweeperBoard.HIDDEN && state[i + 3][j + 1] != MinesweeperBoard.MINE && state[i + 4][j + 1] != MinesweeperBoard.HIDDEN && state[i + 4][j + 1] != MinesweeperBoard.MINE)){
							state[i + 2][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 2));
							if (i > 0){ 
								if (state[i - 1][j - 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j - 1, i - 1);
									board.revealTile(i - 1, j - 1);
									if ((state[i - 1][j - 1] = board.getTile(i - 1, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i - 1][j] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j, i - 1);
									board.revealTile(i - 1, j);
									if ((state[i - 1][j] = board.getTile(i - 1, j)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
							if (i < h - 5){ 
								if (state[i + 5][j - 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j - 1, i + 5);
									board.revealTile(i + 5, j - 1);
									if ((state[i + 5][j - 1] = board.getTile(i + 5, j - 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i + 5][j] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j, i + 5);
									board.revealTile(i + 5, j);
									if ((state[i + 5][j] = board.getTile(i + 5, j)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
						}
					}
					if (j < w - 1){ 
						if (j == 0 || (state[i][j - 1] != MinesweeperBoard.HIDDEN && state[i][j - 1] != MinesweeperBoard.MINE && state[i + 1][j - 1] != MinesweeperBoard.HIDDEN && state[i + 1][j - 1] != MinesweeperBoard.MINE && state[i + 2][j - 1] != MinesweeperBoard.HIDDEN && state[i + 2][j - 1] != MinesweeperBoard.MINE && state[i + 3][j - 1] != MinesweeperBoard.HIDDEN && state[i + 3][j - 1] != MinesweeperBoard.MINE && state[i + 4][j - 1] != MinesweeperBoard.HIDDEN && state[i + 4][j - 1] != MinesweeperBoard.MINE)){
							state[i + 2][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 2));
							if (i > 0){
								if (state[i - 1][j] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j, i - 1);
									board.revealTile(i - 1, j);
									if ((state[i - 1][j] = board.getTile(i - 1, j)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i - 1][j + 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j + 1, i - 1);
									board.revealTile(i - 1, j + 1);
									if ((state[i - 1][j + 1] = board.getTile(i - 1, j + 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
							if (i < h - 5){ 
								if (state[i + 5][j] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j, i + 5);
									board.revealTile(i + 5, j);
									if ((state[i + 5][j] = board.getTile(i + 5, j)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
								if (state[i + 5][j + 1] == MinesweeperBoard.HIDDEN){
									Point pt = new Point(j + 1, i + 5);
									board.revealTile(i + 5, j + 1);
									if ((state[i + 5][j + 1] = board.getTile(i + 5, j + 1)) == 0){
										secure.add(pt);
									}
									rc.remove(pt);
									++s;
								}
							}
						}	
					}
				}
			} /* 1-3-2 */
			for (i = 1; i < h - 1; ++i){
				for (j = 0; j < w - 2; ++j){
					if (state[i][j + 1] == 3){
						if (state[i][j] == 1 && state[i][j + 2] == 2){
							state[i - 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i - 1));
							state[i + 1][j + 2] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 2, i + 1));
						}else if (state[i][j] == 2 && state[i][j + 2] == 1){
							state[i - 1][j] = MinesweeperBoard.MINE;
							mines.add(new Point(j, i - 1));
							state[i + 1][j] = MinesweeperBoard.MINE;
							mines.add(new Point(j, i + 1));
						}
					}
				}
			}
			for (j = 1; j < w - 1; ++j){
				for (i = 0; i < h - 2; ++i){
					if (state[i + 1][j] == 3){
						if (state[i][j] == 1 && state[i + 2][j] == 2){
							state[i + 2][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i + 2));
							state[i + 2][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i + 2));
						}else if (state[i][j] == 2 && state[i + 2][j] == 1){
							state[i][j - 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j - 1, i));
							state[i][j + 1] = MinesweeperBoard.MINE;
							mines.add(new Point(j + 1, i));
						}
					}
				}
			} /* 2-4-2 */
			for (i = 1; i < h - 1; ++i){
				for (j = 0; j < w - 2; ++j){
					if (state[i][j] == 2 && state[i][j + 1] == 4 && state[i][j + 2] == 2){
						if (state[i - 1][j + 1] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 1, i - 1);
							board.revealTile(i - 1, j + 1);
							if ((state[i - 1][j + 1] = board.getTile(i - 1, j + 1)) == 0){
								secure.add(pt);
							}
							rc.remove(pt);
						}
						if (state[i + 1][j + 1] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 1, i + 1);
							board.revealTile(i + 1, j + 1);
							if ((state[i + 1][j + 1] = board.getTile(i + 1, j + 1)) == 0){
								secure.add(pt);
							}
							rc.remove(pt);
						}
						state[i - 1][j] = MinesweeperBoard.MINE;
						mines.add(new Point(j, i - 1));
						state[i - 1][j + 2] = MinesweeperBoard.MINE;
						mines.add(new Point(j + 2, i - 1));
						state[i + 1][j] = MinesweeperBoard.MINE;
						mines.add(new Point(j, i + 1));
						state[i + 1][j + 2] = MinesweeperBoard.MINE;
						mines.add(new Point(j + 2, i + 1));
					}
				}
			}
			for (j = 1; j < w - 1; ++j){
				for (i = 0; i < h - 2; ++i){
					if (state[i][j] == 2 && state[i + 1][j] == 4 && state[i + 2][j] == 2){
						if (state[i + 1][j - 1] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j - 1, i + 1);
							board.revealTile(i + 1, j - 1);
							if ((state[i + 1][j - 1] = board.getTile(i + 1, j - 1)) == 0){
								secure.add(pt);
							}
							rc.remove(pt);
						}
						if (state[i + 1][j + 1] == MinesweeperBoard.HIDDEN){
							Point pt = new Point(j + 1, i + 1);
							board.revealTile(i + 1, j + 1);
							if ((state[i + 1][j + 1] = board.getTile(i + 1, j + 1)) == 0){
								secure.add(pt);
							}
							rc.remove(pt);
						}
						state[i][j - 1] = MinesweeperBoard.MINE;
						mines.add(new Point(j - 1, i));
						state[i + 2][j - 1] = MinesweeperBoard.MINE;
						mines.add(new Point(j - 1, i + 2));
						state[i][j + 1] = MinesweeperBoard.MINE;
						mines.add(new Point(j + 1, i));
						state[i + 2][j + 1] = MinesweeperBoard.MINE;
						mines.add(new Point(j + 1, i + 2));
					}
				}
			}
		}while (s > 0);
	}

	private static double getProbability(final int w, final int h, final int col, final int row, int[][] state){
		int N = state[row][col], hidden = 0;
		if (N == 0){
			return 0.0;
		}
		if (col > 0){
			if (state[row][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row][col - 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}
		}
		if (row > 0){
			if (state[row - 1][col] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		if (col > 0 && row > 0){
			if (state[row - 1][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col - 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		if (col < w - 1){
			if (state[row][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row][col + 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		if (row < h - 1){
			if (state[row + 1][col] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		if (col < w - 1 && row < h - 1){
			if (state[row + 1][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col + 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}
		}
		if (col < w - 1 && row > 0){
			if (state[row - 1][col + 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row - 1][col + 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		if (col > 0 && row < h - 1){
			if (state[row + 1][col - 1] == MinesweeperBoard.MINE){
				--N;
			}else if (state[row + 1][col - 1] == MinesweeperBoard.HIDDEN){
				++hidden;
			}

		}
		return N * 1.0 / hidden;
	}

	private static double getScore(final int w, final int h, Point p, int[][] state){
		double prob, score = 0.0;
		if (p.x > 0){
			if ((prob = getProbability(w, h, p.x - 1, p.y, state)) > score){
				score = prob;
			}
		}
		if (p.y > 0){
			if ((prob = getProbability(w, h, p.x, p.y - 1, state)) > score){
				score = prob;
			}
		}
		if (p.x > 0 && p.y > 0){
			if ((prob = getProbability(w, h, p.x - 1, p.y - 1, state)) > score){
				score = prob;
			}
		}
		if (p.x < w - 1){
			if ((prob = getProbability(w, h, p.x + 1, p.y, state)) > score){
				score = prob;
			}
		}
		if (p.y < h - 1){
			if ((prob = getProbability(w, h, p.x, p.y + 1, state)) > score){
				score = prob;
			}
		}
		if (p.x < w - 1 && p.y < h - 1){
			if ((prob = getProbability(w, h, p.x + 1, p.y + 1, state)) > score){
				score = prob;
			}
		}
		if (p.x < w - 1 && p.y > 0){
			if ((prob = getProbability(w, h, p.x + 1, p.y - 1, state)) > score){
				score = prob;
			}
		}
		if (p.x > 0 && p.y < h - 1){
			if ((prob = getProbability(w, h, p.x - 1, p.y + 1, state)) > score){
				score = prob;
			}
		}
		return score;
	}

	private static void printState(final int w, final int h, int[][] state){
		int i, j;
		for (i = 0; i < h; ++i){
			for (j = 0; j < w; ++j){
				System.out.print(state[i][j] >= 0 ? " " + state[i][j] + " " : state[i][j] == MinesweeperBoard.MINE ? "|> " : " _ ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}
