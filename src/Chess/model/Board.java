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
        {
            // black pieces
            pieces.add(new Rook(new Point(0, 0), false));
            pieces.add(new Knight(new Point(1, 0), false));
            pieces.add(new Bishop(new Point(2, 0), false));
            pieces.add(new Queen(new Point(3, 0), false));
            pieces.add(new King(new Point(4, 0), false));
            pieces.add(new Bishop(new Point(5, 0), false));
            pieces.add(new Knight(new Point(6, 0), false));
            pieces.add(new Rook(new Point(7, 0), false));
            for (int col = 0; col < GameConstant.GAME_SIZE; ++col) {
                pieces.add(new Pawn(new Point(col, 1), false));
            }

            // white player
            pieces.add(new Rook(new Point(0, 7), true));
            pieces.add(new Knight(new Point(1, 7), true));
            pieces.add(new Bishop(new Point(2, 7), true));
            pieces.add(new Queen(new Point(3, 7), true));
            pieces.add(new King(new Point(4, 7), true));
            pieces.add(new Bishop(new Point(5, 7), true));
            pieces.add(new Knight(new Point(6, 7), true));
            pieces.add(new Rook(new Point(7, 7), true));
            for (int col = 0; col < GameConstant.GAME_SIZE; ++col) {
                pieces.add(new Pawn(new Point(col, 6), true));
            }
        }
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
    public void makeMove(Move move, boolean humanMove)
    {
        // Save this board's state before make a move
        this.previousState = this.clone();

        // Implement en passant (bắt tốt qua đường)
        for (Piece piece : this.pieces)
            if (piece.isWhite() == this.whiteTurn && piece instanceof Pawn)
                ((Pawn) piece).enPassantOK = false;

        // Castle move (The change-position-movement of the Rook and the King)
        if (move instanceof CastleMove)
        {
            CastleMove castleMove = (CastleMove) move;
            castleMove.getMovedPiece().moveTo(castleMove.getMoveTo());
            castleMove.getRook().moveTo(castleMove.getRookMoveTo());
        }
        else
        {
            if (move.getCapturedPiece() != null)
            {
                this.removePiece(move.getCapturedPiece());
            }

            // implementing en passant rule
            if (move.getMovedPiece() instanceof Pawn)
            {
                if (Math.abs(move.getMovedPiece().getPiecePosition().y - move.getMoveTo().y) == 2)
                {
                    ((Pawn) move.getMovedPiece()).enPassantOK = true;
                }
            }
            move.getMovedPiece().moveTo(move.getMoveTo());

            // promote pawn if reached final rank
            if (move.getMovedPiece() instanceof Pawn)
            {
                checkPawnPromotion(move.getMovedPiece(), humanMove);
            }
        }

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
        if (move instanceof CastleMove)
        {
            // creates a copy of the move for the copied board
            CastleMove c = (CastleMove) move;
            Piece king = copyBoard.getPieceAt(c.getMovedPiece().getPiecePosition());
            Piece rook = copyBoard.getPieceAt(c.getRook().getPiecePosition());

            // performs the move on the copied board
            copyBoard.makeMove(new CastleMove(king, c.getMoveTo(), rook, c.getRookMoveTo()), false);
        }

        // Other type of move
        else
        {
            // creates a copy of the move for the copied board
            Piece capture = null;
            if (move.getCapturedPiece() != null)
            {
                capture = copyBoard.getPieceAt(move.getCapturedPiece().getPiecePosition());
            }

            Piece moving = copyBoard.getPieceAt(move.getMovedPiece().getPiecePosition());

            // performs the move on the copied board
            copyBoard.makeMove(new Move(moving, move.getMoveTo(), capture), false);
        }


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
    private void checkPawnPromotion(Piece pawn, boolean showdialog)
    {
        if (pawn instanceof Pawn && (pawn.getPiecePosition().y == 0 || pawn.getPiecePosition().y == 7))
        {
            Piece promotedPiece = null;

            // if ai, automatically promote to queen
            {}

            // else, give the player a choice
            Object type = JOptionPane.showInputDialog(null, "", "Choose promotion:", JOptionPane.QUESTION_MESSAGE,
                    null, new Object[] { "Queen", "Rook", "Bishop", "Knight" }, "Queen");

            // will be null if JOptionPane is cancelled or close
            // default is to queen
            if (type == null)
            {
                type = "Queen";
            }

            if (type.toString().equals("Queen"))
            {
                promotedPiece = new Queen(pawn.getPiecePosition(), pawn.isWhite());
            }
            else if (type.toString().equals("Rook"))
            {
                promotedPiece = new Rook(pawn.getPiecePosition(), pawn.isWhite());
            }
            else if (type.toString().equals("Bishop"))
            {
                promotedPiece = new Bishop(pawn.getPiecePosition(), pawn.isWhite());
            }
            else
            {
                promotedPiece = new Knight(pawn.getPiecePosition(), pawn.isWhite());
            }


            // remove pawn and add promoted piece to board
            this.pieces.remove(pawn);
            this.pieces.add(promotedPiece);
        }
    }

    // Check if the Game has ended
    public boolean EndGame() {
        List<Move> whiteMoves = new ArrayList<>();
        List<Move> blackMoves = new ArrayList<>();

        for (Piece piece : this.getAllPieces())
        {
            if (piece.white)
            {
                whiteMoves.addAll(piece.calculatePossibleMoves(this, true));
            }
            else
            {
                blackMoves.addAll(piece.calculatePossibleMoves(this, true));
            }
        }

        // Captured both checkmate case and stalemate case
        return (whiteMoves.size() == 0 || blackMoves.size() == 0);
    }

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