public class Main {
	public static void main(String[] args) {
		MinesweeperBoard board;
		// Two ways to create boards.  Create a random board...

		//board = new MinesweeperBoard(8, 4, 10);

		// ...or create a specific board.

/*
		board = new MinesweeperBoard(new int[][]{{1, 0, 0, 1},
		                                         {0, 0, 0, 0},
		                                         {0, 0, 0, 0},
							 {1, 0, 0, 1},
		                                         {0, 0, 0, 0},
		                                         {0, 0, 0, 0},
							 {1, 0, 0, 1},
							 });
*/
		for (int i = 0; i < 10000; ++i){
		board = new MinesweeperBoard(9, 9, 10);
		MinesweeperPlayer.solve(board);
		}
	}
}
