package Chess.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import Chess.constant.GameConstant;

public class Board implements Cloneable, Serializable
{
    private ArrayList<Piece> pieces = new ArrayList<>(); // List of all Pieces on the board
    private boolean whiteTurn; // Is this white piece's turn?
    private Piece inCheck = null; // Whick king is in check?
    private Board previousState = null; // Save the previous state of the board (for "Undo" feature)
    private Piece lastMoved = null; // The Piece just moved


    //Constructors:
    // Create and save initial information of all pieces into "pieces" array
    public Board(boolean initPiece)
    {
        this.whiteTurn = true; // White piece makes the 1st move

        // If the board need to be initial pieces
        if (initPiece)
        {}
    }

    // Constructor for clone method
    private Board(boolean whiteTurn, Board previousState, List<Piece> pieces, Piece lastMoved, Piece inCheck)
    {
        this.whiteTurn = whiteTurn;
        if (inCheck != null)
            this.inCheck = inCheck.clone();

        if (lastMoved != null)
            this.lastMoved = lastMoved.clone();

        this.previousState = previousState;

        for (Piece p : pieces)
            this.pieces.add(p.clone());
    }

    @Override
    public Board clone() {return new Board(whiteTurn, this.previousState, pieces, this.lastMoved, this.inCheck);}


    //Getters
    public Board getPreviousState() {return previousState;}
    public List<Piece> getAllPieces() {return this.pieces;}
    public Piece getLastMoved() {return lastMoved;}
    public Piece getInCheck() {return inCheck;}
    public boolean isWhiteTurn() {return this.whiteTurn;}


//    Fine the piece is in the "pPos" position, if there no any piece return "null"
    public Piece getPieceAt(Point pPos)
    {
        for (Piece p : pieces)
        {
            if (p.getPiecePosition().x == pPos.x && p.getPiecePosition().y == pPos.y)
                return p;
        }
        return null;
    }

    // Delete the "p" piece
    public void removePiece(Piece p)
    {
        if (pieces.contains(p))
            pieces.remove(p);
    }

    // Delete the piece in "pPos" position
    public void removePieceAt(Point pPos)
    {
        Piece p = null;
        for (Piece pc : pieces)
        {
            if (pc.getPiecePosition().equals(pPos))
            {
                p = pc;
                break;
            }
        }

        if (p != null)
            pieces.remove(p);
    }

//    Add "p" piece into "pieces" list
    public void addPiece(Piece p) {pieces.add(p);}

    // Perform move
    public void makeMove(Move move)
    {
        // Save this board's state before make a move
        this.previousState = this.clone();

        // Implement en passant (bắt tốt qua đường)
        {}

        // Castle move (The change-position-movement of the Rook and the King)
        {}

        // Update properties of board
        this.lastMoved = move.getMovedPiece();

        // Save the checked king into "inCheck"
        this.inCheck = this.kingInCheck();

        // Change turn to the other player
        this.whiteTurn = !whiteTurn;
    }

    // Check if this position in board range
    public boolean ValidSpot(Point spot)
    {
        return (spot.x >= 0 && spot.x < GameConstant.GAME_SIZE) && (spot.y >= 0 && spot.y < GameConstant.GAME_SIZE);
    }

    // Check if a move puts own king in check
    public boolean movePutsKingInCheck(Move move, boolean isWhite)
    {
        // Create the board after perform given move
        Board board = new Board(false);
        board = tryMove(move);

        // Go through all the opponent's piece
        for (Piece piece : board.getAllPieces())
        {
            if (piece.white != isWhite)
            {
                // check whether piece capture king or not.
                for (Move muv : piece.calculatePossibleMoves(board, false))
                    // if a move would result in the capture of a king
                    if (muv.getCapturedPiece() instanceof King)
                        return true;

            }
        }
        return false;
    }

    // Perform the move on a new copied board and return that board
    public Board tryMove(Move move)
    {
        // creates a copy of the board
        Board copyBoard = this.clone();

        // if this is a "CastleMove"
        {}

        // Other type of move
        // creates a copy of the move for the copied board
        Piece capture = null;
        if (move.getCapturedPiece() != null)
        {
            capture = copyBoard.getPieceAt(move.getCapturedPiece().getPiecePosition());
        }

        Piece moving = copyBoard.getPieceAt(move.getMovedPiece().getPiecePosition());

        // performs the move on the copied board
        copyBoard.makeMove(new Move(moving, move.getMoveTo(), capture));


        // returns the copied board with the move executed
        return copyBoard;
    }

    // Check if there is any king in checked
    private Piece kingInCheck()
    {
        for (Piece pc : pieces)
            for (Move mv : pc.calculatePossibleMoves(this, false))
                if (mv.getCapturedPiece() instanceof King)
                {
                    this.inCheck = mv.getCapturedPiece();
                    return mv.getCapturedPiece();
                }
        return null;
    }

    // Check if a Pawn is ready to promotion
    {}

    // Check if the Game has ended
    {}

    // Convert the board into console graphic
    @Override
    public String toString()
    {
        StringBuilder string = new StringBuilder();
        String pieceString = new String();
        for (int row = 0; row < GameConstant.GAME_SIZE; row++)
        {
            for (int col = 0; col < GameConstant.GAME_SIZE; col++)
            {
                pieceString = (this.getPieceAt(new Point(col, row)) == null) ? "-"
                        : this.getPieceAt(new Point(col, row)).isWhite()
                        ? this.getPieceAt(new Point(col, row)).toString().toLowerCase()
                        : this.getPieceAt(new Point(col, row)).toString();
                string.append(pieceString + " ");
            }
            string.append("\n");
        }
        return string.toString();
    }

    public void showTextGame()
    {
        System.out.println(this.toString());
    }
}