package shashki1;

import java.io.Serializable;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 *
 * @author Spike
 */
public class Tile extends Rectangle  implements Serializable {

    private Piece piece;

    public Tile() {
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Tile(boolean light, int x, int y) {
        setWidth(50);
        setHeight(50);

        super.relocate(x * 50, y * 50);

        setFill(light ? Color.valueOf("#f3c13a") : Color.valueOf("2a7e19"));
    }
}