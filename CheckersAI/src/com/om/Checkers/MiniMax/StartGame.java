package com.om.Checkers.MiniMax;

import java.util.Scanner;

/* This Class is to kick-off the Game

 */



class StartGame{

    public static void main(String[] args) throws InterruptedException {



        //RandomAI one = new RandomAI(Player.Side.BLACK);
        //RandomAI two = new RandomAI(Player.Side.WHITE);

        //one goes first if true;
        boolean turn = true;

        //System.out.println(board.toString());
// infinite loop for replaying the Game
        while (true) {
            int round;
            boolean chance=false;
            Board board = new Board();
            board.countWhiteBlackPieces(board.getBoard());
          //  System.out.println(board.getBlackcount());
            //System.out.println(board.getWhitecount());

            System.out.print(" Do You want to Play first? : Y : N");
            Scanner sc = new Scanner(System.in);
            String choice = sc.next();

            System.out.print(" Select the level of the Game 1:Easy 2: Medium 3: Hard");
            // To determine the level of the Game.
            Scanner players_level_selection = new Scanner(System.in);
            int players_level = players_level_selection.nextInt();

            /// Initialize Game Players Human as Black
            Player one = new Player("Human", Player.Side.BLACK);
            //Player two = new Player("Player 2", Player.Side.WHITE);

            //MinimaxAI one = new MinimaxAI(Player.Side.BLACK, 3);
            MinimaxAI two = new MinimaxAI(Player.Side.WHITE, players_level);

            round=0;

            if (choice.toString().equals("Y")) {
                chance = true;
            }
            while (true) {
                Board.Decision decision = null;
                round++;
                if (!chance) {
                    // to Enter in lool for AI Player
                    if (players_level ==1){
                        decision = two.makeMove(board,players_level);
                    }
                    else if (players_level == 2)
                    {
                        decision = two.makeMove(board,players_level);
                    }
                    else

                        decision = two.makeMove(board,players_level);
                }
                else{
                    // to Enter in lool for Human Player
                   // System.out.println(board.toString());
                    System.out.println(" Make a move by providing your start and end corrdinates");
                    Scanner inputMove = new Scanner(System.in);
                    String moveCordinates = inputMove.nextLine();
                    // For Taking user input for Moves

                    String[] cordinates = moveCordinates.split(" ");
                    Move m;
                    if (cordinates.length == 1) {
                        m = new Move(Integer.parseInt(moveCordinates.charAt(0) + ""), Integer.parseInt(moveCordinates.charAt(1) + ""),
                                Integer.parseInt(moveCordinates.charAt(2) + ""), Integer.parseInt(moveCordinates.charAt(3) + ""));
                    } else {
                        int[] srcDestCordinates = new int[cordinates.length];
                        for (int i = 0; i < cordinates.length; i++) {
                            srcDestCordinates[i] = Integer.parseInt(cordinates[i]);
                        }
                        m = new Move(srcDestCordinates[0], srcDestCordinates[1], srcDestCordinates[2], srcDestCordinates[3]);


                    }
                    decision = one.makeMove(m, board,one.getSide());
                    System.out.println(decision);
                }
                 /// Cases for making Invalid Moves
                if(decision == Board.Decision.FAILED_INVALID_DESTINATION || decision == Board.Decision.FAILED_MOVING_INVALID_PIECE)
                {
                    System.out.println("Move Failed");
                    // don't update anything
                }
                //cases when the moves are legtimiate and complete
                else if(decision == Board.Decision.COMPLETED)
                {
                    System.out.println(board.toString());
                    if(board.getBlackcount() == 0)
                    {
                        System.out.println("AI wins with " + board.getWhitecount() + " pieces left");

                        board.countWhiteBlackPieces(board.getBoard());

                        break;
                    }
                    if(board.getWhitecount() == 0)
                    {
                        System.out.println("You win with " + board.getBlackcount()+ " pieces left");

                        board.countWhiteBlackPieces(board.getBoard());
                        break;
                    }
                    // case to decide winner of the game.
                    if ((chance && (board.getAllpossiblevalidMoves(two.getSide()).size()==0)) || (!chance && (board.getAllpossiblevalidMoves(one.getSide()).size()==0))) {
                        if(board.getBlackcount() == board.getWhitecount())
                            System.out.println("Match Drawn");
                        else if (board.getBlackcount() > board.getWhitecount())
                            System.out.println("Human Wins");
                        else
                            System.out.println("AI Wins");

                        break;
                    }


                    if(chance)
                        chance = false;
                    else
                        chance = true;

                }

                 //case to decide winner of the game when decision turn out to be Game Ended.
                else if(decision == Board.Decision.GAME_ENDED)
                {
                    //current player cannot move
                    if(chance)
                    {
                        System.out.println("White wins");


                    }
                    else {
                        System.out.println("Black wins");

                    }
                    break;
                }

            }
            System.out.println("Game finished after: " + round + " rounds");

        }





    }

}




