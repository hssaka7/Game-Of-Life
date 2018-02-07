/**
 * Aakash Basnet
 *
 * This is a Main class which have static main method
 * This class extends Application and also implements Eventhandler
 * It setups and shows the window for game of life and also sets
 * action when the button is clicked.It also sets the intial value
 * for all the variables and the intial boardArray.
 * It also creates the worker threads.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;


public class Main extends Application implements EventHandler
{
    private Button buttonStart;
    private Button buttonNext;
    private Button buttonReset;
    private Button buttonPreset1;  //All Dead
    private Button buttonPreset2;  // Random
    private Button buttonPreset3; // Glider
    private Button buttonPreset4; // All Alive except edges
    private Button buttonPreset5; // UperRight checkBoard
    private Button buttonPreset6; // Own Present / cool preset
    private ComboBox threadCombo;  // Number of therad option
    private List<WorkerThread> threads;
    private CyclicBarrier barrier;
    private int threadNum = 8;     // default thread number

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 750;
    public static final int N = 10000; // num or rows/column

    public static ResizableCanvas canvas;
    public static ScrollBar vScroll, hScroll;

    public static byte[][] boardState = new byte[N][N];
    public static byte[][] tempboardState = new byte[N][N];
    public static boolean done = false;
    public static int GridPixal = 15; // defeault gridpixel

    public static double strokeSize = 1;
    public static int scrollX = 1 ; // initial value of horizontal scrollbar
    public  static int scrollY = 1; // initial value of vertical scrollbar
    @Override


    /**
     * Creates the window for the game og life with buttons, combobox, canvas
     * and two scroll bars. This method designs the window fro the game, creates
     * a scene and shows the window.
     */
    public void start(Stage primaryStage) throws Exception
    {
        createArray();  // creates an empty N*N array
        primaryStage.setTitle("Game of Life");

        // defining buttons and setting action.
        buttonStart = new Button("Start");
        buttonStart.setOnAction(this);

        buttonReset = new Button("Reset");
        buttonReset.setOnAction(this);

        buttonNext = new Button("Next");
        buttonNext.setOnAction(this);

        buttonPreset1 = new Button("All Dead");
        buttonPreset1.setOnAction(this);

        buttonPreset2 = new Button("Random");
        buttonPreset2.setOnAction(this);

        buttonPreset3 = new Button("Glider Gun");
        buttonPreset3.setOnAction(this);

        buttonPreset4 = new Button("All Alive");
        buttonPreset4.setOnAction(this);

        buttonPreset5 = new Button("UpperRight");
        buttonPreset5.setOnAction(this);

        buttonPreset6 = new Button("Cool");
        buttonPreset6.setOnAction(this);

        ObservableList<Integer> option = FXCollections.observableArrayList(
          1,2,3,4,5,6,7,8);

        // creating thread combo ans setting action
        threadCombo = new ComboBox(option);
        threadCombo.setPromptText("Total Thread");
        threadCombo.setOnAction(e ->
        {
            threadNum = (int) threadCombo.getSelectionModel().getSelectedItem();
            //System.out.println("total Thread = " + threadNum);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.getChildren().addAll(buttonStart, buttonNext, buttonReset, buttonPreset1,
          buttonPreset2, buttonPreset3, buttonPreset4,buttonPreset5,buttonPreset6,threadCombo);

        VBox pageContainer = new VBox(0);

        //creating canvas and scrollBars
        canvas = new ResizableCanvas(WINDOW_WIDTH, WINDOW_HEIGHT - buttonBox.getHeight());
        vScroll = createScrollbar(Orientation.VERTICAL, canvas.getHeight());
        hScroll = createScrollbar(Orientation.HORIZONTAL, canvas.getWidth());


        GridPane scrollPane = new GridPane();
        scrollPane.prefHeightProperty().bind(pageContainer.heightProperty());
        scrollPane.prefWidthProperty().bind(pageContainer.widthProperty());

        scrollPane.addColumn(0,canvas, hScroll);
        scrollPane.add(vScroll, 1, 0);
        scrollPane.setGridLinesVisible(true);
        pageContainer.getChildren().addAll(buttonBox, scrollPane);


        //making canvas and scrollBar resizable

        canvas.widthProperty().bind(scrollPane.widthProperty().subtract(20));
        canvas.heightProperty().bind(scrollPane.heightProperty().subtract(20));
        hScroll.prefWidthProperty().bind(scrollPane.widthProperty());
        vScroll.prefHeightProperty().bind(scrollPane.heightProperty());

        //setting action to scrollBars
        vScroll.valueProperty().addListener(e->
        {
            //System.out.println("Scroll Value = " + vScroll.getValue());
            int y = (int)vScroll.getValue();
            scrollY = y;
            //System.out.println("Starty --------------------->" + vScroll.getValue());
            canvas.drawGrid(scrollX,scrollY);

        });

        hScroll.valueProperty().addListener(e->
        {
            //System.out.println("Scroll Value = " + hScroll.getValue());
            int x = (int)hScroll.getValue();
            //System.out.println("StartX --------------------->" + x);
            scrollX = x;
            canvas.drawGrid(scrollX,scrollY);

        });


        StackPane root = new StackPane();
        root.getChildren().addAll(pageContainer);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e-> Platform.exit());

        primaryStage.show();
    }

  /**
   * creates initial boardArray with all the random alive/dead cells
   */
  public void createArray(){
      for(int i = 1; i < N-1 ; i++)
      {
        for (int j = 1; j < N-1; j++)
        {

          boardState[i][j] = (byte) (Math.round(Math.random()));
        }
      }
    }

  /**
   * This methods fills the boardArray with all the dead cells
   */
    public void deadArray()
    {
      for(int i = 0; i < N ; i++)
      {
        for (int j = 0; j < N; j++)
        {

          boardState[i][j] = 0;
        }
      }

    }

  /**
   * It creates a scrollBar from the following parameters
   * and also sets the min and maximum value of the scrollBar
   * @param orien             orientation of scrollBar
   * @param canvasSize        canvassize
   * @return                  created scrollBar
   */
    private ScrollBar createScrollbar(Orientation orien, double canvasSize)
    {
        ScrollBar scroll = new ScrollBar();
        scroll.setOrientation(orien);
        scroll.setMin(1);
        scroll.setValue(1);
        if(orien.equals(Orientation.HORIZONTAL)){
            scroll.setMax((N-(canvas.getWidth()/GridPixal))+1);

        }
        else {
            scroll.setMax((N-(canvas.getHeight()/GridPixal))+1);
        }


        scroll.setBlockIncrement(5);
        scroll.setUnitIncrement(1);

        return scroll;
    }

    @Override

    /**
     * this methods handle the Event from buttons and combobox.
     * It sets action whenever an event is dected.
     */
    public void handle(Event event)
    {
        Object source = event.getSource();

        /**
         * if start button is pressed. It starts a game and change the text
         * of the button from Start to Pause. if pause button is clicked, it
         * paused the game. Whenever a game is started, other buttons and
         * comboBox are disabled. To enable these objects, user must pause the
         * game. Whenever Start button is cliced the createThread method is called.
         */
        if (source == buttonStart)
        {
           if (buttonStart.getText().equals("Start"))
           {
             done = false;
             buttonStart.setText("Pause");
             disable(true);


             createThread(1);
           }

           else
           {
             buttonStart.setText("Start");
             done = true;
             disable(false);
           }

        }

        /**
         * when next button is clicked, it creates the worker thread and
         * updates the board with next generation.
         */
        else if (source == buttonNext)
        {
            barrier = new CyclicBarrier(8);
            createThread(0);
             //canvas.drawGrid(scrollX,scrollY);
        }

        /**
         * Whenever Reset is clicked, it resets the whole board.
         */
        else if (source == buttonReset)
        {
            threadNum = 8;
            GridPixal = 15;
            vScroll.setValue(1);
            hScroll.setValue(1);
            deadArray();
            canvas.drawGrid(scrollX,scrollY);
        }

        // All dead
        else if (source == buttonPreset1)
        {
            deadArray();
            canvas.drawGrid(scrollX,scrollY);

        }
        //Random
        else if (source == buttonPreset2)
        {
          createArray();
          canvas.drawGrid(scrollX,scrollY);

        }

        // glider gun
        else if (source == buttonPreset3)
        {
            deadArray();
            boardState[1][1] = 1;
            boardState[2][2] = 1;
            boardState[2][3] = 1;
            boardState[3][1] = 1;
            boardState[3][2] = 1;

            canvas.drawGrid(scrollX,scrollY);

        }

        //All ALive Except Edges.
        else if (source == buttonPreset4)
        {
            deadArray();
            for(int i = 2; i < N-2 ; i++)
            {
                for (int j = 2; j < N-2; j++)
                {
                   boardState[i][j] = 1;
                }
            }
            canvas.drawGrid(scrollX,scrollY);

        }
        //upperleft checker board
        else if (source == buttonPreset5)
        {
          /*int num = 0;
          int temp1 = 8;


          deadArray();
          for(int j = 1; j<N-1;j ++)
          {
            for (int i = j; i < N - 1; i+=2)
            {
              num ++;
              if (i % 16 != 0 ) boardState[i][j] = 1;

            }
          }*/
          deadArray();
          int count =0,oddOrEven = 0;
          int icount = 0;
          int jStartValue =2;
          boolean flag = true;
          for (int i = 2; i < boardState.length - 2; i++)
          {
            for (int j = jStartValue; j < boardState[0].length - 2; j++)
            {
              if (true)
              {
                boardState[i][j] = 1;
              }
              j++;
              count++;
              if (count ==7){
                j+= 2;
                count =0;

              }


            }
            if (icount ==7){
              icount=0;
            }
            else icount++;
            count = icount;
            if (oddOrEven==0) oddOrEven =1;
            else oddOrEven =0;
            jStartValue++;
            //count;;

          }
          canvas.drawGrid(scrollX,scrollY);


        }


        // own Preset
        else if (source == buttonPreset6)
        {
          deadArray();
          for(int i = 1; i < N-1 ; i++)
          {
            for (int j = 1; j < N-1; j++)
            {
             if(i % 20 == 0 || j % 20 == 0) boardState[i][j] = 1;

            }
          }
          canvas.drawGrid(scrollX,scrollY);

        }
    }

  /**
   * This methods creates worker threads and the number of the worker
   * threads depends on the number of threads the user selects. Also
   * the default number of thread is 8. Once worker thread are defined,
   * they are started.
   * @param sep
   */
  public void createThread(int sep){

        int diff = N/threadNum;
        int x = 1;
        int  y = diff;

        Runnable barrierAction = new Runnable()
        {
          @Override
          public void run()
          {
            canvas.drawGrid(Main.scrollX,Main.scrollY);
          }
        };

        barrier = new CyclicBarrier(threadNum,barrierAction);
        threads = new ArrayList<>(threadNum);

        for(int i = 0; i < threadNum; i++)
        {
          if(y > N -2) y = N - 2;
          WorkerThread worker = new WorkerThread(x,y,barrier,sep);
          x = y +1;
          y = y + diff;
          threads.add(worker);
          worker.start();
        }

  }

  /**
   * disable methods either enables or disables the buttons
   * and combobox depending on the parameter passed.
   * @param value                boolean value
   */
  public void disable(boolean value){
      buttonNext.setDisable(value);
      buttonReset.setDisable(value);
      buttonPreset1.setDisable(value);
      buttonPreset2.setDisable(value);
      buttonPreset3.setDisable(value);
      buttonPreset4.setDisable(value);
      buttonPreset5.setDisable(value);
      buttonPreset6.setDisable(value);
      threadCombo.setDisable(value);

    }

    /**
     * executable main method, it lauches the game window.
     * @param args           command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}


