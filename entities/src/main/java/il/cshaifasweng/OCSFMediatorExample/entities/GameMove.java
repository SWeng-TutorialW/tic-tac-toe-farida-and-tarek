package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class GameMove implements Serializable {
    private int row;
    private int col;
    private String player; // "X" or "O"

    public GameMove(int row, int col, String player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getPlayer() {
        return player;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
