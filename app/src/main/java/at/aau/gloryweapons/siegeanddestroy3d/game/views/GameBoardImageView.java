package at.aau.gloryweapons.siegeanddestroy3d.game.views;

import android.content.Context;


/**
 * This custom ImageView is used for the game board.
 */
public class GameBoardImageView extends android.support.v7.widget.AppCompatImageView {
    private int boardRow = -1;
    private int boardCol = -1;

    public GameBoardImageView(Context context, int boardRow, int boardCol) {
        super(context);
        setBoardLocation(boardRow, boardCol);
    }

    private void setBoardLocation(int boardRow, int boardCol) {
        this.boardRow = boardRow;
        this.boardCol = boardCol;
    }

    public int getBoardRow() {
        return boardRow;
    }

    public int getBoardCol() {
        return boardCol;
    }
}
