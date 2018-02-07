/**
 * Aakash Basnet
 * This is WorkerThread class which extends thread class.
 * It has a constructor, run method, update method, checkNeighbour
 * method and equate methods.
 * This class checks and update the board array by assigning the
 * stating x and ending x values.
 */

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class WorkerThread extends Thread
{
  private int StartX = 0; //start position of worker thread
  private int EndX = 0;  // end position of worker therad
  private CyclicBarrier barrier;
  private int seperator; // seperator value that seperates satrt/pause button action
                 // with next button action. its value is 1 if the start/pause
                 // is clicked and 0 if next button is clicked


  /**
   * This is a constructor for the WorkerThread class
   * It takes following values as parameter and set it to the value declared
   * above in the class
   * @param x1   it is the start position on board for worker thread
   * @param x2  it is the end position on board for the worker thread
   * @param b   it is the cyclic barrier for the worker thread
   * @param x   it is the value of the seperator
   */
  public WorkerThread(int x1, int x2, CyclicBarrier b,int x){
    this.StartX = x1;
    this.EndX = x2;
    this.barrier = b;
    this.seperator = x;
  }

  /**
   * This run method contains the procress what a worker thread
   * does after it is started. If the value of a seperator is 1
   * then it creates a while loop unless user ask it to stop by
   * clicking next button. On while loop it calls update method,
   * which updates the game board and then the worker waits in a
   * cyclic barrier until other worker threads are done with their
   * work. And if the seperator is zero, it just calls the update
   * method, gets next generation and wait for the other worker thread
   * in a barrier.
   */
  public void run(){
    if(seperator == 1)
    {
      while (Main.done == false)
      {
        updateArray();
        try
        {
          barrier.await();
        } catch (InterruptedException e)
        {
          e.printStackTrace();
        } catch (BrokenBarrierException e)
        {
          e.printStackTrace();
        }
        //if(seperator == 0) Main.done = true;

      }
    }

    if(seperator == 0){
      updateArray();
      try
      {
        barrier.await();
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      } catch (BrokenBarrierException e)
      {
        e.printStackTrace();
      }
    }

  }


  /**
   * This method update the present gameBoard array calling
   * check neighbours method. It will check the form certain
   * startX value to EndX value. It checks Y form 1 to 9999(all y)
   * It contains the logic to determine if the current cell id dead
   * or alive for next generation and also determies the age of the
   * alive cell and writes to the tempBoard array. Finally, calls equate
   * method to equate boardArray to tempBoardArray.
   */
  public void updateArray(){
    for(int i = StartX; i <= EndX; i++){
      for(int j = 1; j <Main.N-1; j++){
        int tot = checkNeigh(i,j);
        if(Main.boardState[i][j]  > 0)
        {
          if (tot < 2) Main.tempboardState[i][j] = 0;  //underpopulation
          else if (tot > 3) Main.tempboardState[i][j] = 0;  //overpopulation
          else if (tot == 2 || tot == 3)
          {
            if(Main.boardState[i][j] < 12)
              Main.tempboardState[i][j] = (byte)(Main.boardState[i][j] + 1);
          }
        }
        else if(Main.boardState[i][j] == 0){
          if(tot == 3) Main.tempboardState[i][j] = 1;
          else Main.tempboardState[i][j] = 0;
        }
      }
    }
    equateBoard();
  }

  /**
   * This method takes following parameters and check for the all
   * 8 neighbours.
   * @param x     x poition of present cell
   * @param y     y position of present cell
   * @return      total live neighbour around present cell
   */
  public int checkNeigh(int x, int y){
    int totNeigh = 0;

    if(Main.boardState[x+1][y] >= 1){
      totNeigh ++;
    }
    if(Main.boardState[x-1][y] >= 1){
      totNeigh ++;

    }
    if(Main.boardState[x][y+1] >= 1){
      totNeigh ++;

    }
    if(Main.boardState[x][y-1] >= 1){
      totNeigh ++;

    }
    if(Main.boardState[x-1][y-1] >= 1){
      totNeigh ++;

    }
    if(Main.boardState[x-1][y+1] >= 1){
      totNeigh ++;

    }
    if(Main.boardState[x+1][y+1] >= 1){

      totNeigh ++;
    }
    if(Main.boardState[x+1][y-1] >= 1){
      totNeigh ++;
    }
    return totNeigh;
  }

  /**
   * This method equates all the element of board array to tempboard array
   * so that we have updated version of boardarray.
   */
  public void equateBoard(){
    for(int i = StartX; i <=EndX; i++){
      for(int j = 1; j < Main.N; j++){
        Main.boardState[i][j] = Main.tempboardState[i][j];
      }
    }

  }
}
