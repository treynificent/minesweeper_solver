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
		int success = 0, score;
		final int board_size = 10, num_trials = 10000, num_mines = 12;
		double avg_score = 0.0; 
		for (int i = 0; i < num_trials; ++i){
			board = new MinesweeperBoard(board_size, board_size, num_mines);
			if ((score = MinesweeperPlayer.solve(board).size()) == num_mines){
				++success;
			}
			avg_score += score * 1.0 / num_trials;
		}
		System.out.println("average score == " + avg_score + "\nsuccess rate == " + (success * 1.0 / num_trials));
	}
}
