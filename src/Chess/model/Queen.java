package Chess.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece
{

    public Queen(Point piecePosition, boolean white)
    {
        this.piecePosition = piecePosition;
        this.white = white;
        this.firstMove = true;
    }

    // Constructor for clone method
    private Queen(Point piecePosition, boolean white, boolean firstMove)
    {
        this.firstMove = firstMove;
        this.piecePosition = piecePosition;
        this.white = white;
    }

    @Override
    public String toString() {
        return "Q";
    }

    @Override
    public List<Move> calculatePossibleMoves(Board board, boolean checkKing)
    {
        List<Move> moves = new ArrayList<Move>();

        // if no board given, return empty list
        if (board == null)
            return moves;

        int[] dx = { 1, 0, -1, 0, 1, -1, 1, -1 };
        int[] dy = { 0, 1, 0, -1, 1, 1, -1, -1 };
        for (int i = 0; i < 8; ++i)
            addMovesInLine(board, moves, dx[i], dy[i]);


        // check that move doesn't put own king in check
        if(checkKing)
            this.removeMovesPutsKingInCheck(board, moves);

        return moves;
    }


    private void addMovesInLine(Board board, List<Move> moves, int xi, int yi)
    {
        int x = piecePosition.x;
        int y = piecePosition.y;

        Point pt = new Point(x + xi, y + yi);
        Piece pc;

        while(board.ValidSpot(pt))
        {
            pc = board.getPieceAt(pt);


            if(pc == null)
                moves.add(new Move(this, pt, pc));

            else if(pc.white != this.white)
            {
                moves.add(new Move(this, pt, pc));
                break;
            }

            else
                break;

            pt = new Point(pt.x + xi, pt.y + yi);
        }
    }

    @Override
    public Queen clone() {return new Queen(this.piecePosition, this.white, this.firstMove);}
    @Override
    public String getImageName() {return "Queen";}
}
