package shashki1;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 *
 * @author Spike
 */
public class FXMLDocumentController implements Initializable {

    Board playBoard;
    @FXML
    private Pane pole;
    private Button bChange;
    Group gBoard;
    Group gPieces;
    Connection conn;
    PieceType playerPiece;
    @FXML
    private ListView<String> lvChat;
    @FXML
    private TextField tfText;
    @FXML
    private Label textPlayer;
    @FXML
    private Label textNowGo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setContentText("Choose typ");
        ButtonType server = new ButtonType("Server");
        ButtonType client = new ButtonType("Client");
        alert.getButtonTypes().addAll(server, client);
        Optional<ButtonType> chooseTyp = alert.showAndWait();
        if (chooseTyp.get() == server) {
            Alert al = new Alert(Alert.AlertType.NONE);
            try {
                al.setContentText("Wait for connection");
                al.show();
                conn = new Connection(6666);
                
                
            } catch (SocketException ex) {
                alert(ex.toString());
            } catch (IOException ex) {
                alert(ex.toString());
            }
            al.close();
            start(true);
        }
        if (chooseTyp.get() == client) {
            TextInputDialog dialog = new TextInputDialog("127.0.0.1");
            dialog.setContentText("Input IP(xxx.xxx.xxx.xxx):");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    conn = new Connection(result.get(), 6666);

                } catch (IOException ex) {
                    Alert al = new Alert(Alert.AlertType.WARNING);
                    al.setContentText("No server in your network");
                    Optional<ButtonType> opt = al.showAndWait();
                    if (opt.get() == ButtonType.OK) {
                        System.exit(0);
                    }
                }
                start(false);
            }
        }

    }

    private void start(boolean server) {
        playBoard = new Board();
        if (server) {
            playerPiece = PieceType.RED;
            playBoard.setNextStep(PieceType.RED);
            textPlayer.setText("You are RED!");
        } else {
            playerPiece = PieceType.WHITE;
            playBoard.setNextStep(PieceType.RED);
            textPlayer.setText("You are WHITE!");
        }
        gBoard = new Group();
        gPieces = new Group();
        gBoard.getChildren().addAll(playBoard.getListTile());
        pole.getChildren().add(gBoard);
        gPieces.getChildren().addAll(playBoard.getListPiece());
        pole.getChildren().add(gPieces);
        addListenerPiece();
        KeyFrame update = new KeyFrame(Duration.seconds(1), event -> {
            Object obj = conn.getObject();
            if (obj != null && obj instanceof String) {
                lvChat.getItems().add((String) obj);
            }
            if (obj != null && obj instanceof Board) {
                playBoard = (Board) obj;
                redraw();
            }

            int red = 0;
            int white = 0;
            for (Piece piece : playBoard.getListPiece()) {
                if (piece.getType() == PieceType.RED) {
                    red++;
                }
                if (piece.getType() == PieceType.WHITE) {
                    white++;
                }
            }
            if (red == 0) {
                Alert al = new Alert(Alert.AlertType.WARNING);
                al.setContentText("WHITE WIN!!!");
                al.showAndWait();
            }
            if (white == 0) {
                Alert al = new Alert(Alert.AlertType.WARNING);
                al.setContentText("RED WIN!!!");
                al.showAndWait();
            }
        });
        Timeline tl = new Timeline(update);
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (playBoard.getBoard()[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == piece.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (playBoard.getBoard()[x1][y1].hasPiece() && playBoard.getBoard()[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, playBoard.getBoard()[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int) (pixel + 50 / 2) / 50;
    }

    private void addListenerPiece() {
        for (Node p : gPieces.getChildren()) {

            Piece piece = (Piece) p;
            piece.setOnMouseReleased((e) -> {
                if (playerPiece == piece.getType() && playBoard.getNextStep() == playerPiece) {
                    try {
                        int newX = toBoard(piece.getLayoutX());
                        int newY = toBoard(piece.getLayoutY());

                        MoveResult result;

                        if (newX < 0 || newY < 0 || newX >= 8 || newY >= 8) {
                            result = new MoveResult(MoveType.NONE);
                        } else {
                            result = tryMove(piece, newX, newY);
                        }

                        int x0 = toBoard(piece.getOldX());
                        int y0 = toBoard(piece.getOldY());

                        switch (result.getType()) {
                            case NONE:
                                piece.abortMove();
                                break;
                            case NORMAL:
                                piece.move(newX, newY);
                                playBoard.getBoard()[x0][y0].setPiece(null);
                                playBoard.getBoard()[newX][newY].setPiece(piece);
                                break;
                            case KILL:
                                piece.move(newX, newY);
                                playBoard.getBoard()[x0][y0].setPiece(null);
                                playBoard.getBoard()[newX][newY].setPiece(piece);

                                Piece otherPiece = findPiece(result.getPiece());
                                playBoard.getBoard()[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                                playBoard.getListPiece().remove(otherPiece);
                                gPieces.getChildren().remove(otherPiece);
                                break;
                        }
                        changePlayer();
                        conn.sendObject(playBoard);
                    } catch (IOException ex) {
                        alert(ex.toString());
                    }
                } else {
                    piece.abortMove();
                }

            });
        }
    }


    private void changePlayer() {
        if (playBoard.getNextStep() == PieceType.RED) {
            playBoard.setNextStep(PieceType.WHITE);
        } else {
            playBoard.setNextStep(PieceType.RED);
        }
    }

    private void redraw() {
        pole.getChildren().clear();
        pole.getChildren().add(gBoard);
        gPieces.getChildren().clear();

        for (Piece piece : playBoard.getListPiece()) {
            gPieces.getChildren().add(new Piece(piece.getType(), (int) piece.getOldX() / 50, (int) piece.getOldY() / 50));
        }
        addListenerPiece();
        pole.getChildren().add(gPieces);
        playBoard.getListPiece().clear();
        for (Node node : gPieces.getChildren()) {
            playBoard.getListPiece().add((Piece) node);
        }
    }

    private Piece findPiece(Piece piece) {
        if (piece == null) {
            return null;
        }
        for (Piece p : playBoard.getListPiece()) {
            if (p.getOldX() == piece.getOldX() && p.getOldY() == piece.getOldY()) {
                return p;
            }
        }
        return null;
    }

    @FXML
    private void bSend(ActionEvent event) {
        if (!tfText.getText().isEmpty()) {
            try {
                String s = playerPiece + ": " + tfText.getText();
                conn.sendObject(s);
                lvChat.getItems().add(s);
            } catch (IOException ex) {
                alert(ex.toString());
            }
        }
    }

    void alert(String s) {
        Alert al = new Alert(Alert.AlertType.ERROR);
        al.setContentText(s);
        al.showAndWait();
    }

}
