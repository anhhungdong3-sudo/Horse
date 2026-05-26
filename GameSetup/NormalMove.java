package GameSetup;

import java.util.List;

public class NormalMove implements MoveStrategy {

	// hỗ trợ tính toán soay vòng
	private int wrapIndex(int index, int size) {
		int mod = index % size;
		if (mod < 0) {
			return mod + size;
		} else {
			return mod;
		}
	}

	@Override
	public boolean move(int dice, Piece piece, Board board) {
		int currentPos = piece.getBoardPosition();
		int stepMoved = piece.getStepsMoved();
		List<BoardCell> gridGoal = piece.getOwner().getGridGoal();
		int goFirst = 0;
		int steps = dice;

		// --- Trạng thái đặc biệt: quân đã hoàn thành ---
		if (currentPos == -3) {
			System.out.println("Piece already completed.");
			return false;
		}

		// --- Nếu quân đang ở cửa nhà lên đích ---
		int tmp = piece.getGoalPosition() + 1;
		while (tmp <= 5) {
			if (gridGoal.get(tmp).getPiece() != null) {
				goFirst++;
			}
			tmp++;
		}
		if (stepMoved == 55) {
			for (int i = 0; i < Math.abs(steps); i++) {
				Piece midPiece = gridGoal.get(i).getPiece();
				if (midPiece != null) {
					boolean isLastStep = (i == Math.abs(steps) - 1);
					if (isLastStep) {
						System.out.println("Blocked by piece in goal path.");
						return false;
					}
				}
			}
			
			//3.2.1 xóa quân khỏi bàn
			board.removePiece(piece);
			//3.2.2 thay đổi thông tin vị trí
			piece.setGoalPosition(Math.abs(steps) - 1);
			piece.setBoardPosition(-2);
			piece.setStepsMoved(-1);
			//3.2.3. thêm lại vào vị trí mới được thay đổi
			gridGoal.get(Math.abs(steps) - 1).setPiece(piece);

			if (piece.getGoalPosition() == 5 - goFirst) {
				piece.setBoardPosition(-3);
				System.out.println("Completed!");
			}
			return true;
		}

		// --- Nếu bước đi vượt quá đích ---
		if (stepMoved + steps > 55 && stepMoved != 55) {
			System.out.println("Cannot move beyond goal entry.");
			return false;
		}

		// --- Nếu đang trong khu goal riêng ---
		if (piece.getBoardPosition() == -2) {
			if (dice == piece.getGoalPosition() + 2) {
				if (gridGoal.get(piece.getGoalPosition() + 1).getPiece() == null) {
					//3.2.1 xóa quân khỏi bàn
					gridGoal.get(dice - 1).setPiece(piece);
					//3.2.2 thay đổi thông tin vị trí
					piece.setGoalPosition(dice - 1);
					//3.2.3. thêm lại vào vị trí mới được thay đổi
					gridGoal.get(dice - 2).setPiece(null);
					System.out.println("Piece " + piece.getId() + " moved up to cell " + dice);
					if (piece.getGoalPosition() == 5 - goFirst) {
						//trường hợp đặc biệt đã hoàn thành
						piece.setBoardPosition(-3);
						System.out.println("Completed!");
						return true;
					}
					return false;
				}
				System.out.println("Piece is blocked.");
				return false;
			}
			System.out.println("Incorrect dice roll.");
			return false;
		}

		// --- Nếu đang trên vòng ngoài (56 ô) ---
		if (currentPos >= 0) {
			List<BoardCell> gridNormal = board.getGridNormal();
			int destPos = wrapIndex(currentPos + steps, 56);
			int absSteps = Math.abs(steps);

			// Duyệt từng ô trung gian theo hướng đi
			for (int i = 1; i <= absSteps; i++) {
				int offset;
				if (steps > 0) {
					offset = i; // đi xuôi
				} else {
					offset = -i; // đi lùi
				}

				int midPos = wrapIndex(currentPos + offset, 56);
				Piece midPiece = gridNormal.get(midPos).getPiece();

				if (midPiece != null) {
					boolean isLastStep = (i == absSteps);

					if (isLastStep) {
						// Ô đích: nếu cùng màu → không thể tới
						if (midPiece.getColor().equals(piece.getColor())) {
							System.out.println("Cannot land on your own piece.");
							return false;
						} else {
							// Ăn quân khác
							System.out.println(piece.getColor() + " piece " + piece.getId() + " kicked "
									+ midPiece.getColor() + " " + midPiece.getId());
							//3.2.1 xóa quân khỏi bàn
							board.removePiece(midPiece);
							//3.2.2 thay đổi thông tin vị trí
							midPiece.setBoardPosition(-1);
							midPiece.setStepsMoved(0);
							break;
						}
					} else {

						System.out.println("Piece " + piece.getId() + " at " + piece.getBoardPosition()
								+ " is blocked at " + midPos + " by " + midPiece.getColor() + " " + midPiece.getId());
						return false;

					}
				}
			}

			// --- Di chuyển thực tế ---
			//3.2.1 xóa quân khỏi bàn
			board.removePiece(piece);
			//3.2.2 thay đổi thông tin vị trí
			piece.setBoardPosition(destPos);
			piece.setStepsMoved(stepMoved + steps);
			//3.2.3. thêm lại vào vị trí mới được thay đổi
			board.addPiece(piece);

			System.out.println(piece.getColor() + " piece " + piece.getId() + " moved from " + currentPos + " to "
					+ destPos + " | total moved: " + piece.getStepsMoved());
			return true;
		}

		return false;
	}
}
