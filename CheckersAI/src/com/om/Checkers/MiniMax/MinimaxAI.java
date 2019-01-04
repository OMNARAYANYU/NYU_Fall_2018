package com.om.Checkers.MiniMax;
import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/*
Class for MiniMaxAI Player and AI implementation
 */

public class MinimaxAI extends Player{

    private Point skippingPoint;
    private int cut_off_limit =35;
    private int totalNode;
    private int minPruned;
    private int maxPruned;
    private int maxdepth;

    private long totalTimeElapsed;
    private int level;

    public MinimaxAI(String name, Side s)
    {
        super(name, s);
    }
    public MinimaxAI(Side s, int level)
    {
        super("MinimaxAI", s);
        this.level=level;
        //this.depth = depth;
        this.totalTimeElapsed = 0;
    }

    public Board.Decision makeMove(Board board,int level) throws InterruptedException {

        //long startTime = System.nanoTime();
        Board.Decision decision=null;
        //Move m = minimaxStart(board, startTime, getSide(), true);

        //System.out.println("m is: " + m);
        Random rand = new Random();
        // For easy level
        if (level == 1) {
            // possibleMoves.get(rand.nextInt(possibleMoves.size()))
            List<Move> possibleMove = board.getAllpossiblevalidMoves(getSide());
            Move move = possibleMove.get(rand.nextInt(possibleMove.size()));
            decision = board.makeMove(move, getSide());


            //System.out.println("Pruned tree: " + pruned + " times");

        }
        // For Medium Level
        else if(level == 2) {
            // possibleMoves.get(rand.nextInt(possibleMoves.size()))
            long startTime = System.nanoTime();
            Move m = minimaxStart(board, startTime, getSide(), true,level);

            //System.out.println("m is: " + m);
            //Move move = board.getAllValidMoves(getSide()).get(m);
            decision = board.makeMove(m,getSide());



            //System.out.println("Pruned tree: " + pruned + " times");

        }
        // For Hard level
        else if(level==3){
            long startTime = System.nanoTime();
           // Move m = minimaxStart(board, startTime, getSide(), true,level);
            Move m1 = alpha_beta_search(board, startTime, getSide(), true);

            //System.out.println("m is: " + m);
            //Move move = board.getAllValidMoves(getSide()).get(m);
            decision = board.makeMove(m1,getSide());


            //System.out.println("Pruned tree: " + pruned + " times");
            return decision;
        }

        return decision;
    }
    // start aplha_beta for level3
    private Move alpha_beta_search (Board board,long startTime, Side side, boolean maximizingPlayer ) throws InterruptedException  {

        List<Move> possibleMoves = board.getAllpossiblevalidMoves(side);
        List<Move> movetoexplore=possibleMoves;
        List<Move> captureMoves = new ArrayList<>();
        Board tempBoard;
        List<Integer> heuristicsvalue = new ArrayList<>();




        for (int i =0 ;i <possibleMoves.size();i++ ) {
            //Move c = possibleMoves.get(i);
            if(board.checkCaptureMoves(possibleMoves.get(i)))
                captureMoves.add(possibleMoves.get(i));
        }
        // check if capture move present or not. If it is then explore only that move down the tree
        if (captureMoves.size() > 0)
            movetoexplore = captureMoves;



        for(int i = 0; i < movetoexplore.size(); i++) {
            totalNode++;
            tempBoard = board.copyBoardState();
            tempBoard.setStatedepth(board.getStatedepth() + 1);
            tempBoard.makeMove(movetoexplore.get(i),side);

            heuristicsvalue.add(max_value(tempBoard, startTime, minimaxPlayer(side), -1000, 1000));
        }
        Integer maxvalue = Collections.max(heuristicsvalue);
        int index= heuristicsvalue.indexOf(maxvalue);
        if (cut_off_limit == maxdepth+1)
            System.out.println("cutoff reached at: " + cut_off_limit);
        //to return best heuristics
        System.out.println("Filtered/max heuristics: " + heuristicsvalue.toString());
        System.out.println( " Max Depth ::  " + (maxdepth+1)  + " Total Node Generated :: " + totalNode + " maxPruned :: " + maxPruned + " minPruned ::  " + minPruned );

        return movetoexplore.get(index);
    }

// Min Player Function
    private int min_value(Board board,long startTime,  Side side,int alpha, int beta)throws InterruptedException
    {
        totalTimeElapsed = (System.nanoTime() - startTime)/ 1000000000;

        List<Move> possibleMoves = board.getAllpossiblevalidMoves(side);
        List<Move> movetoexplore=possibleMoves;
        List<Move> captureMoves = new ArrayList<>();


        for (int i =0 ;i <possibleMoves.size();i++ ) {
            //Move c = possibleMoves.get(i);
            if(board.checkCaptureMoves(possibleMoves.get(i)))
                captureMoves.add(possibleMoves.get(i));
        }
        // check if capture move present or not. If it is then explore only that move down the tree
        // Capture moves are obligatory
        if (captureMoves.size() > 0)
            movetoexplore = captureMoves;


        // Terminal test for 15 seconds
        if  (totalTimeElapsed >= 15) {
          //  System.out.println("total size : " + possibleMoves.size() +" totalTimeElapsed : " +totalTimeElapsed + "Side :"+side);
           //4130
            // TimeUnit.SECONDS.sleep(5);

                return getHeuristic(board,side);

        }
        //cutoff test at a set depth level
        if (cutoffsearch(board))
        {
          //  System.out.println(" Cut off reached: -----" + side);
            return getHeuristic(board,side);

        }

        int initial = +1000;
        Board tempBoard ;
        if (movetoexplore.size() == 0)
             return getHeuristic(board,side);
        for(int i = 0; i < movetoexplore.size(); i++)
        {   totalNode++;
            tempBoard = board.copyBoardState();
            tempBoard.setStatedepth(board.getStatedepth()+1);
            //System.out.println(tempBoard.toString());
            tempBoard.makeMove(movetoexplore.get(i),side);


            initial= Math.min (initial, max_value(tempBoard, startTime, minimaxPlayer(side), alpha, beta));
          //  System.out.println(" Depth reached at MiniPlayer :"+ board.getStatedepth());
            if (board.getStatedepth() > maxdepth)
                maxdepth= board.getStatedepth();

            if (initial<=alpha) {
                minPruned++;
                return initial;
            }
            beta = Math.min(beta,initial);

        }
        return initial;
    }

// Max-player function
    private int max_value(Board board,long startTime,  Side side,int alpha, int beta)throws InterruptedException
    {
        totalTimeElapsed = (System.nanoTime() - startTime)/ 1000000000;
        /*if (board.getAllpossiblevalidMoves(side).size()==0) {
            System.out.print(" No Possible move at depth " + depth);
            TimeUnit.SECONDS.sleep(5);

        }*/
        List<Move> possibleMoves = board.getAllpossiblevalidMoves(side);
        List<Move> movetoexplore=possibleMoves;
        List<Move> captureMoves = new ArrayList<>();


        for (int i =0 ;i <possibleMoves.size();i++ ) {
            //Move c = possibleMoves.get(i);
            if(board.checkCaptureMoves(possibleMoves.get(i)))
                captureMoves.add(possibleMoves.get(i));
        }
        // check if capture move present or not. If it is then explore only that move down the tree
        if (captureMoves.size() > 0)
            movetoexplore = captureMoves;


       // System.out.print(" size of capture move :" + captureMoves.size());
      // Terminal test for 15 seconds
         if(totalTimeElapsed >= 15) {
           // System.out.println("total size : " + possibleMoves.size() +" totalTimeElapsed : " +totalTimeElapsed + "Side :"+side);
           // TimeUnit.SECONDS.sleep(5);

            return getHeuristic(board,side);

        }
        //cutoff test at a set depth level
        if (cutoffsearch(board))
        {
            //System.out.println(" Cut off reached**************: "+ side);
            return getHeuristic(board,side);

        }

        int initial = -1000;
        Board tempBoard;
        if (movetoexplore.size() == 0)
            return getHeuristic(board,side);
        for(int i = 0; i < movetoexplore.size(); i++)
        {   totalNode++;
            tempBoard = board.copyBoardState();
            tempBoard.setStatedepth(board.getStatedepth()+1);
            //System.out.println(tempBoard.toString());
            tempBoard.makeMove(movetoexplore.get(i),side);


            initial= Math.max(initial, min_value(tempBoard, startTime, minimaxPlayer(side), alpha, beta));
            //System.out.println(" Depth reached at MiniPlayer :"+ board.getStatedepth());
            if (board.getStatedepth() > maxdepth)
                maxdepth= board.getStatedepth();

            if (initial>=beta) {
                maxPruned++;
                return initial;
            }
            alpha = Math.max(alpha,initial);

        }
        return initial;
    }





//Minimax start for depth level2
    private Move minimaxStart(Board board, long startTime, Side side, boolean maximizingPlayer,int level) throws InterruptedException {
        double alpha = -1000;
        double beta = +1000;

        List<Move> possibleMoves;

            possibleMoves = board.getAllpossiblevalidMoves(side);


        //System.out.println("side: " + side + " " + possibleMoves.size());

        List<Double> heuristicsvalue = new ArrayList<>();
        if(possibleMoves.isEmpty())
            return null;
        Board tempBoard = null;
        int depth=0;

        totalNode=1;

        maxPruned=0;
        minPruned=0;
        for(int i = 0; i < possibleMoves.size(); i++)
        {
            totalNode++;
            tempBoard = board.copyBoardState();
            tempBoard.setStatedepth(board.getStatedepth()+1);
            tempBoard.makeMove(possibleMoves.get(i),side);

           // System.out.println(tempBoard.toString());
            heuristicsvalue.add(minimax(tempBoard,startTime, minimaxPlayer(side), !maximizingPlayer, alpha, beta,level));

        }
        System.out.println("\nMinimax at depth: " + maxdepth + "\n" + heuristicsvalue.toString() + "TOTOAL NODE:" + totalNode +  " ::maxPruned ::" + maxPruned +":: minPruned ::" +  minPruned);

        double maxHeuristics = -1000;

        Random rand = new Random();
        for(int i = heuristicsvalue.size() - 1; i >= 0; i--) {
            if (heuristicsvalue.get(i) >= maxHeuristics) {
                maxHeuristics = heuristicsvalue.get(i);
                System.out.println("Heuristic: "+ heuristicsvalue.get(i));
            }
        }
        //Main.println("Unfiltered heuristics: " + heuristics);
        for(int i = 0; i < heuristicsvalue.size(); i++)
        {
            if(heuristicsvalue.get(i) < maxHeuristics)
            {
                heuristicsvalue.remove(i);
                possibleMoves.remove(i);
                i--;
            }
        }

        //to return best heuristics
        System.out.println("Filtered/max heuristics: " + heuristicsvalue.toString());
        if (possibleMoves.size()==1 )
            return possibleMoves.get(0);
        else
        return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }

    private double minimax(Board board, long startTime, Side side, boolean maximizingPlayer, double alpha, double beta,int level) throws InterruptedException {

        totalTimeElapsed = (System.nanoTime() - startTime)/ 1000000000;
        /*if (board.getAllpossiblevalidMoves(side).size()==0) {
            System.out.print(" No Possible move at depth " + depth);
            TimeUnit.SECONDS.sleep(5);

        }*/
        List<Move> possibleMoves = board.getAllpossiblevalidMoves(side);
        List<Move> movetoexplore=possibleMoves;
        List<Move> captureMoves = new ArrayList<>();
          for (int i =0 ;i <possibleMoves.size();i++ ) {
              //Move c = possibleMoves.get(i);
              if(board.checkCaptureMoves(possibleMoves.get(i)))
                  captureMoves.add(possibleMoves.get(i));
          }

         //System.out.print(" size of capture move :" + captureMoves.size());
        if (level ==2 && totalTimeElapsed >= 15)
            return getHeuristiclevel2(board,side);

        if (cutoffsearch(board))
        {
           // System.out.println(" Cut off reached");
            return getHeuristiclevel2(board,side);

        }


        if (captureMoves.size() > 0)
            movetoexplore = captureMoves;


        double initial = 0;
        Board tempBoard = null;
        if(maximizingPlayer)
        {
            //System.out.println("-------------- Max Player: AI-------------------");
            //System.out.println("-------------- Max NODES: -------------------");


            initial = -1000;
            for(int i = 0; i < movetoexplore.size(); i++)
            {
                totalNode++;
                tempBoard = board.copyBoardState();
                tempBoard.makeMove(possibleMoves.get(i),side);
                tempBoard.setStatedepth(board.getStatedepth()+1);
                //System.out.println(tempBoard.toString());

                double result = minimax(tempBoard,startTime, minimaxPlayer(side), !maximizingPlayer, alpha, beta,level);
              //  System.out.println(" Depth reached at MaxPlayer "+ board.getStatedepth());

                initial = Math.max(result, initial);
                alpha = Math.max(alpha, initial);
                if (board.getStatedepth() > maxdepth)
                    maxdepth= board.getStatedepth();

                if(alpha >= beta){
                    maxPruned++;
                  //  System.out.print(" Pruned Tree for Max Player" +maxPruned);
                    //TimeUnit.SECONDS.sleep(5);
                    break;
                }

            }
        }
        //minimizing
        else
        {
           // System.out.println("-------------- Min Player: HUMAN-------------------");
            //System.out.println("-------------- Min Expanded NODES: -------------------");
            initial = +1000;
            for(int i = 0; i < movetoexplore.size(); i++)
            {   totalNode++;
                tempBoard = board.copyBoardState();
                tempBoard.setStatedepth(board.getStatedepth()+1);
                //System.out.println(tempBoard.toString());
                tempBoard.makeMove(possibleMoves.get(i),side);


                double result = minimax(tempBoard, startTime, minimaxPlayer(side), !maximizingPlayer, alpha, beta,level);
                //System.out.println(" Depth reached at MiniPlayer "+ board.getStatedepth());
                if (board.getStatedepth() > maxdepth)
                    maxdepth= board.getStatedepth();


                initial = Math.min(result, initial);
                alpha = Math.min(alpha, initial);

                if(alpha >= beta) {
                    minPruned++;
                   // System.out.print(" Pruned Tree for Mini Player" +minPruned);
                    //TimeUnit.SECONDS.sleep(5);4
                    break;
                }
            }
        }//

        //System.out.println(" depth of tree generated: " +depth+ "::MAX DEPTH:: "+ maxDepth +" :: maxPruned :" + maxPruned +" :: minPruned ::" + minPruned +":: totalNodeGenerated:: " + totalNode);
        return  initial;
    }
/*Heuristic to get Utility Value for level3 Hard */
    private int getHeuristic(Board b, Player.Side side) throws InterruptedException {
       // System.out.print(" Side in Heuristic: 43" + side);
       // TimeUnit.SECONDS.sleep(1);
        //naive implementation
        int heuristicValue=0;


        if(side == Side.WHITE)
            heuristicValue+= b.getWhitecount() - b.getBlackcount();
        else
            heuristicValue+= b.getBlackcount() - b.getWhitecount();

        for (int i =0 ;i< 6 ;i++)
             for (int j =0 ;j<6 ;j++)
                  if (b.board[i][j] == Board.Checkers.WHITE && side == Side.WHITE)
                  {
                      heuristicValue += i-6;
                      //System.out.println(" (6-i); " +(6-i));
                  }
                  else if (b.board[i][j] == Board.Checkers.BLACK && side == Side.BLACK)
                    {
                        heuristicValue+= 6-i;
                    }



        //System.out.print(" heurise=tic value:------------------------ " + heuristicValue);





        return heuristicValue;

    }
// Heuristic function for Level2
    private int getHeuristiclevel2(Board b, Player.Side side) throws InterruptedException {
        // System.out.print(" Side in Heuristic: 43" + side);
        // TimeUnit.SECONDS.sleep(1);
        //naive implementation
        int heuristicValue=0;

        if(side == Side.WHITE)
            heuristicValue+= b.getWhitecount() - b.getBlackcount();
        else
            heuristicValue+= b.getBlackcount() - b.getWhitecount();

        return heuristicValue;

    }
// Flip the side of the player
    private Side minimaxPlayer(Side side)
    {

        if(side == Side.BLACK)
            return Side.WHITE;
        return Side.BLACK;
    }
// Cut-off level check
    private boolean cutoffsearch(Board b) {
        if (b.getStatedepth() == cut_off_limit) {
            //System.out.println(" CUT OFF LEVEL " + b.getStatedepth());
            //System.out.print(side);

            return true;
        }
        else return false;
    }

}
