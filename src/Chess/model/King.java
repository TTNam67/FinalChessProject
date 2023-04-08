package Chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece
{

    public King(Point piecePosition, boolean white)
    {
        this.piecePosition = piecePosition;
        this.white = white;
        this.firstMove = true;
    }

    // Constructor for clone method
    private King(Point piecePosition, boolean white, boolean firstMove)
    {
        this.firstMove = firstMove;
        this.piecePosition = piecePosition;
        this.white = white;
    }

    @Override
    public List<Move> calculatePossibleMoves(Board board, boolean checkKing)
    {
        int x = piecePosition.x;
        int y = piecePosition.y;

        List<Move> moves = new ArrayList<Move>();

        // if no board given, return empty list
        if (board == null)
            return moves;

        int[] dx = { -1, 0, 1, 1, 1, 0, -1, -1 };
        int[] dy = { -1, -1, -1, 0, 1, 1, 1, 0};
        // add moves around the king if they are valid
        for (int i = 0; i < 8; ++i) {
            addIfValid(board, moves, new Point(x + dx[i], y + dy[i]));
        }

        // "CastleMove"

        // Remove moves that making own king in check
        if (checkKing) {
            this.removeMovesPutsKingInCheck(board, moves);
        }
        return moves;
    }

    private void addIfValid(Board board, List<Move> moves, Point point)
    {
        // if the location is valid
        if (board.ValidSpot(point))
        {
            // and the location does not contain same color piece
            Piece pc = board.getPieceAt(point);
            if (pc == null || pc.white != this.white)
            {
                // all the move to the list
                moves.add(new Move(this, point, pc));
            }
        }
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public King clone() {
        return new King(this.piecePosition, this.white, this.firstMove);
    }

    @Override
    public String getImageName() {
        return "King";
    }

}
