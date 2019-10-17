package shashki1;

import java.io.Serializable;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;


/**
 *
 * @author Spike
 */
public class Piece extends StackPane  implements Serializable {

    private PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;

    public double getMouseX() {
        return mouseX;
    }

    public void setMouseX(double mouseX) {
        this.mouseX = mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public void setMouseY(double mouseY) {
        this.mouseY = mouseY;
    }

    public Piece() {
    }

    public PieceType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);


        Ellipse ellipse = new Ellipse(50 * 0.3, 50 * 0.3);
        ellipse.setFill(type == PieceType.RED
                ? Color.RED : Color.WHITE);

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(5);

        ellipse.setTranslateX((50 - 50 * 0.3 * 2) / 2);
        ellipse.setTranslateY((50 - 50 * 0.3 * 2) / 2);

        getChildren().addAll(ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y) {
        oldX = x * 50;
        oldY = y * 50;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}