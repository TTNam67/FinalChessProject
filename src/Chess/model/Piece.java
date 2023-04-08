package Chess.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.List;
/*
- "abstract": it cannot be directly instantiated, and must be subclassed to be used
- "Cloneable": object can be cloned
- "Serializable": object can be serialized or converted into a stream of bytes
to be stored on disk or transmitted over a network.

By implementing these interfaces, the Piece class provides support for creating
new objects of the class through cloning, and also provides support for storing
or transmitting objects of the class in binary format.
 */

// This class represents for Pieces
public abstract class Piece implements Cloneable, Serializable
{
    protected boolean white; //is this white piece ot not?
    protected Point piecePosition; // Position of the piece
    protected boolean firstMove = true; // Is this my move?

    //Getters
    public Point getPiecePosition() {return piecePosition;}
    public boolean isFirstMove() {return firstMove;}
    public boolean isWhite() {return this.white;}

    //Setters

    public void setFirstMove(boolean firstMove) {this.firstMove = firstMove;}

    public void setPiecePosition(Point piecePosition) {this.piecePosition = piecePosition;}

    public void setWhite(boolean white) {this.white = white;}


    // Move the piece to "destination"
    public void moveTo(Point destination)
    {
        this.firstMove = false;
        this.piecePosition = destination;
    }

    public abstract String toString();
    @Override
    protected abstract Piece clone(); 

    public abstract String getImageName();
}

/*
Đọc script Queen.java để hiểu cách triển khai 1 loại quân cờ
 */
