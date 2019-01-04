package com.om.Checkers.MiniMax;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class Board {


    public Checkers[][] board;


    private int whitecount;
    private int blackcount;
    private int statedepth;

    public int getStatedepth() {
        return statedepth;
    }

    public void setStatedepth(int statedepth) {
        this.statedepth = statedepth;
    }



// Define three types of state on Game
    public enum Checkers {
        EMPTY, WHITE, BLACK
    }
// To get the decision after the move
    public enum Decision {
        COMPLETED,
        FAILED_MOVING_INVALID_PIECE,
        FAILED_INVALID_DESTINATION,
        GAME_ENDED
    }

    public Board() {
        //populateTestDummyBoard();
        //countWhiteBlackPieces(board);
        populateInitialBoard();
    }

    public Board(Checkers[][] board)
    {

        whitecount=0;
        blackcount=0;
        this.board = board;
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j< 6; j++)
            {
                Checkers piece = getCheckers(i, j);
                if(piece == Checkers.BLACK)
                    blackcount++;
                else if(piece == Checkers.WHITE)
                    whitecount++;

            }
        }
    }

    // populate initial state of the Game
    private void populateInitialBoard() {


        board = new Checkers[6][6];
        for (int i = 0; i < board.length; i++) {
            int start = 0;
            if (i % 2 == 0)
                start = 1;

            Checkers pieceCheckers = Checkers.EMPTY;
            if (i <= 1)
                pieceCheckers = Checkers.WHITE;
            else if (i >= 4)
                pieceCheckers = Checkers.BLACK;

            for (int j = start; j < board[i].length; j += 2) {
                board[i][j] = pieceCheckers;
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null)
                    board[i][j] = Checkers.EMPTY;
            }
        }


    }



// For counting white and Black Pieces

    public void countWhiteBlackPieces(Checkers[][] board) {

        whitecount = 0;
        blackcount = 0;

        this.board = board;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Checkers piece = getCheckers(i, j);
                if (piece == Checkers.BLACK)
                    this.blackcount++;
                else if (piece == Checkers.WHITE)
                    this.whitecount++;


            }
        }
    }


    public Checkers getCheckers(int row, int col) {
        return board[row][col];
    }

    public Checkers getCheckers(Point point) {
        return board[point.x][point.y];
    }

    public Checkers[][] getBoard() {
        return board;
    }

    public int getWhitecount() {
        return whitecount;
    }

    public int getBlackcount()
    {
        return blackcount;
    }

    // get ALl possibles moves for a given Player

    public List<Move> getAllpossiblevalidMoves(Player.Side s) {

        List<Move> regularmoves ;
        List<Move> moves = new ArrayList<>();
        List<Move> capturemoves ;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                Checkers type = this.board[i][j];
                if((s == Player.Side.WHITE && type== Checkers.WHITE) || (s == Player.Side.BLACK && type== Checkers.BLACK)){
                    regularmoves = getregularmove(i, j, type);
                    capturemoves = capturemoves(i, j, s);
                    moves.addAll(regularmoves);
                    moves.addAll(capturemoves);
                }
            }
        }
        return moves;
    }

// Only return a capture move for a player
    public  List<Move> capturemoves(int row, int col, Player.Side p) {

        Point startPoint = new Point(row, col);
        List<Move> moves = new ArrayList<>();
        List<Point> choices = new ArrayList<>();
        if (p == Player.Side.WHITE && getCheckers(row, col) == Checkers.WHITE) {
            //System.out.println(" Player Side White");
            if(! ((row+2)>=6) && !((col+2) >=6))
                choices.add(new Point(row + 2, col + 2));
            if(! ((row+2)>=6) && !((col-2) <0))
                choices.add(new Point(row + 2, col - 2));
        } else if (p == Player.Side.BLACK && getCheckers(row, col) == Checkers.BLACK) {
            //System.out.println(" Player Side Black");
            if(!((row-2)<0) && !((col+2) >=6))
                choices.add(new Point(row - 2, col + 2));
            if(! ((row-2)<0) && !((col-2) <0))
                choices.add(new Point(row - 2, col - 2));
        }


        for (int i = 0; i < choices.size(); i++) {
            Point movepoint = choices.get(i);
            Move m = new Move(startPoint, movepoint);

            // requires there to actually be a mid square



            Point midpoint = new Point((m.getStart().x + m.getEnd().x) / 2, (m.getStart().y + m.getEnd().y) / 2);
            //System.out.println(midpoint);
            Checkers midpointtype = getCheckers(midpoint);
            boolean isoppnent = (p == Player.Side.BLACK && midpointtype == Checkers.WHITE) || (p == Player.Side.WHITE && midpointtype == Checkers.BLACK);


            if (movepoint.x < 6 && movepoint.x >= 0 && movepoint.y < 6 && movepoint.y >= 0 && getCheckers(movepoint.x, movepoint.y) == Checkers.EMPTY
                    && isoppnent) {
                moves.add(m);
            }
        }

        return moves;
    }

/// Only returns a regular move for a player
    public  List<Move> getregularmove(int row, int col, Checkers type){
        Point startPoint = new Point(row, col);
        List<Move> moves = new ArrayList<Move>();

        //System.out.print(" Checkers :" + type);
        if (type == Checkers.WHITE)    {
            int newRow = row + 1;
            if (newRow >= 0 && newRow < 6) {
                int newCol = col + 1;
                if (newCol < 6 && getCheckers(newRow, newCol) == Checkers.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
                newCol = col - 1;
                if (newCol >= 0 && getCheckers(newRow, newCol) == Checkers.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
            }
        }if (type == Checkers.BLACK){
            //2 possible moves
            int newRow = row -1 ;
            if (newRow >= 0 && newRow < 6) {
                int newCol = col + 1;
                // System.out.print("row:" + newRow +"new col:"+newCol );
                if (newCol < 6 && getCheckers(newRow, newCol) == Checkers.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
                newCol = col - 1;
                if (newCol >= 0 && getCheckers(newRow, newCol) == Checkers.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
            }

        }

        return moves;
    }
// to check wjether the player is moving its own piece or not
    private boolean isOwnPieceMove(int row, int col, Player.Side side) {
        Checkers pieceCheckers = getCheckers(row, col);
        if (side == Player.Side.BLACK && pieceCheckers != Checkers.BLACK)
            return false;
        else if (side == Player.Side.WHITE && pieceCheckers != Checkers.WHITE )
            return false;
        return true;
    }
 ///To check whether moves are capture or not..This helps to narrow down search tree
    public boolean checkCaptureMoves(Move currentMove){

        Point start = currentMove.getStart();
        Point end = currentMove.getEnd();
        if( Math.abs(start.getX() - end.getX() )== 2 && Math.abs (start.getY()- end.getY())== 2)
            return true;
        else
            return false;


    }
    // to make a move from start point to end point for a player
    public Decision makeMove(Move move, Player.Side p) {
        if(move == null) {
            return Decision.GAME_ENDED;
        }
        Point start = move.getStart();
        int startRow = start.x;
        int startCol = start.y;
        Point end = move.getEnd();
        int endRow = end.x;
        int endCol = end.y;

        //can only move own piece and not empty space
        if (!isOwnPieceMove(startRow, startCol,p) || getCheckers(startRow, startCol) == Checkers.EMPTY)
            return Decision.FAILED_MOVING_INVALID_PIECE;

        List<Move> possibleMoves = getAllpossiblevalidMoves(p);
        //System.out.println(possibleMoves);
        //System.out.println(possibleMoves);

        Checkers currCheckers = getCheckers(startRow, startCol);

        if (possibleMoves.contains(move)) {
            boolean jumpMove = false;
            //if it contains move then it is either 1 move or 1 jump
            if (startRow + 1 == endRow || startRow - 1 == endRow) {
                board[startRow][startCol] = Checkers.EMPTY;
                board[endRow][endCol] = currCheckers;
            } else {
                jumpMove = true;
                board[startRow][startCol] = Checkers.EMPTY;
                board[endRow][endCol] = currCheckers;
                Point mid = new Point((move.getStart().x + move.getEnd().x) / 2, (move.getStart().y + move.getEnd().y) / 2);
                Checkers middle = getCheckers(mid);
                if (middle == Checkers.BLACK)
                    blackcount--;
                else if(middle == Checkers.WHITE)
                    whitecount--;
                board[mid.x][mid.y] = Checkers.EMPTY;
            }

            return Decision.COMPLETED;
        } else
            return Decision.FAILED_INVALID_DESTINATION;
    }

// Method to show the position or state of Game
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("  ");
        for (int i = 0; i < board.length; i++) {
            b.append(i + " ");
        }
        b.append("\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = -1; j < board[i].length; j++) {
                String a = "";
                if (j == -1)
                    a = i + "";
                else if (board[i][j] == Checkers.WHITE)
                    a = "w";
                else if (board[i][j] == Checkers.BLACK)
                    a = "b";
                else
                    a = "_";
                b.append(a);
                b.append(" ");
            }
            b.append("\n");
        }
        return b.toString();
    }
    // To clone the current state of the Game
    public Board copyBoardState()
    {
        Checkers[][] newBoard = new Checkers[6][6];
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j< 6; j++)
            {
                newBoard[i][j] = board[i][j];
            }
        }
        Board b = new Board(newBoard);
        return b;
    }









}


