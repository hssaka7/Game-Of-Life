/**
 *Aakash Basnet
 * This class extend the canvas class and creates
 * the canvas for the gave. It has a constructor which takes
 * width and height of the canvas. This class also set the mouse
 * listener to the canvas and also contains the method that draws
 * th grid.
 */


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class ResizableCanvas extends Canvas
{
  public GraphicsContext gtx;
  public double recentposX;
  public double recentposY;
  public int recentArrayIndX;
  public int recentArrayIndY;


  /**
   * This is a constructor for ResizableCanvas class.It
   * sets the inital height and width of canvas. Then it also
   * performs acton whenever a mouse is moved, clicked or scrolled
   * inside the canvas. It toggles life of a cell whenever a clicked
   * and zoom/in out whenever mouse is scrolled inside a canvas.
   * @param initWidth         initial width of a canvas
   * @param initHeight        initial height of a canvas
   */
  public ResizableCanvas(double initWidth, double initHeight)
  {
    this.setHeight(initHeight);
    this.setWidth(initWidth);

    gtx = this.getGraphicsContext2D();
    this.setOnMouseClicked(e ->
    {
      int tempX = (int) (e.getX() / Main.GridPixal);
      int tempY = (int) (e.getY() / Main.GridPixal);

      int arrayPosX = tempX + Main.scrollX;
      int arrayPosY = tempY + Main.scrollY;

      if(Main.boardState[arrayPosX][arrayPosY] == 0){
        Main.boardState[arrayPosX][arrayPosY] = 1;
        gtx.setFill(Color.web("rgb(140, 170,150)"));

      }
      else
      {
        Main.boardState[arrayPosX][arrayPosY] = 0;
        gtx.setFill(Color.GREY);
      }

      double posX = (tempX * Main.GridPixal) + 1.3;
      double posY = (tempY * Main.GridPixal) + 1.3;
      gtx.fillRect(posX, posY, Main.GridPixal - 2.6, Main.GridPixal - 2.6);

    });

    this.setOnMouseMoved(e ->
    {
      recentposX = e.getX();
      recentposY = e.getY();
      recentArrayIndY =(int) (recentposY/Main.GridPixal) + Main.scrollY;
      recentArrayIndX =(int) (recentposX/Main.GridPixal) + Main.scrollX ;
    });

    this.setOnScroll(e ->
    {

      int tempx = 0;
      int tempy = 0;
      int diffX = 0;
      int diffY = 0;

      if(e.getTextDeltaY() > 0 && Main.GridPixal < 50){
        Main.GridPixal = Main.GridPixal + 2;

        tempx = (int)(recentposX/Main.GridPixal) + Main.scrollX; //gridx
        tempy = (int)(recentposY/Main.GridPixal) + Main.scrollY; // grid y
        //System.out.println("X = " +tempx + " Y = " + tempy);

        diffX =recentArrayIndX - tempx;
        diffY= recentArrayIndY - tempy;
        //System.out.println("diffX = " +diffX + " diffY = " + diffY);


        Main.scrollX =Main.scrollX +diffX;
        Main.scrollY= Main.scrollY + diffY;

      }
      else if(e.getTextDeltaY() < 0 && Main.GridPixal>5)
      {
        Main.GridPixal = Main.GridPixal - 2;
        tempx = (int)(recentposX/Main.GridPixal) + Main.scrollX; //gridx
        tempy = (int)(recentposY/Main.GridPixal) + Main.scrollY; // grid y
        diffX = tempx - recentArrayIndX;
        diffY = tempy - diffY;
        int x = Main.scrollX - diffX;
        int y = Main.scrollY - diffY;

        if(x > 0 && y>0){
          Main.scrollX = x;
          Main.scrollY = y;
        }

      }
      Main.hScroll.setMax(Main.N-(this.getWidth()/Main.GridPixal));
      Main.hScroll.setBlockIncrement(Main.GridPixal*5);
      Main.hScroll.setBlockIncrement(Main.GridPixal);
      Main.hScroll.setValue(Main.scrollX);

      Main.vScroll.setMax(Main.N-(this.getHeight()/Main.GridPixal));
      Main.vScroll.setBlockIncrement(Main.GridPixal*5);
      Main.vScroll.setBlockIncrement(Main.GridPixal);
      Main.vScroll.setValue(Main.scrollY);

      drawGrid(Main.scrollX,Main.scrollY);
    });


    this.widthProperty().addListener(ob -> drawGrid((int)Main.hScroll.getValue(),(int)Main.vScroll.getValue()));
    this.heightProperty().addListener(ob ->drawGrid((int)Main.hScroll.getValue(),(int)Main.vScroll.getValue()));

    drawGrid(0,0);
  }

  /**
   *This methods makes window resizable
   * @return true
   */
  public boolean isResizable()
  {
    return true;
  }


  /**
   * This method takes the age of current cell and returns the corresponding
   * shades of Green.
   * @param age    Age of current live cell
   * @return       the corresponding color for given age
   */
  public Color getAgeColor(Byte age)
  {
    Color color = null;
    if (age == 1) color =Color.web("rgb(140, 170,150)");
    else if (age == 2) color =Color.web("rgb(130,162,141)");
    else if (age == 3) color =Color.web("rgb(114, 151 ,96)");
    else if (age == 4) color =Color.web("rgb(104, 142,85)");
    else if (age == 5) color =Color.web("rgb(88, 128,68)");
    else if (age == 6) color =Color.web("rgb(77, 117,58)");
    else if (age == 7) color =Color.web("rgb(69,105,51)");
    else if (age == 8) color =Color.web("rgb(63,100,43)");
    else if (age == 9) color =Color.web("rgb(56,94,36)");
    else if (age > 9) color =Color.web("rgb(47,88,27)");


    return color;
  }

  /**
   * This methods draws grids according to the gridpixel and canvas size
   * and also draws the alive cells from given starting point
   * The ending poit of the array to be drawn depends on the size of the
   * canvas and grid pixel.
   * @param startX    starting X value of array to be drawn
   * @param startY    staring Y value of array to be drawn
   */
  public void drawGrid(int startX,int startY)
  {
    int ytemp = startY;
    gtx.setFill(Color.GREY);
    gtx.fillRect(0, 0, this.getWidth(), this.getHeight());


    gtx.setStroke(Color.BLACK);
    gtx.setLineWidth(1);

 // drawing the grid

    if(Main.GridPixal > 5)
    {
      for (int i = 0; i < this.getWidth(); i += Main.GridPixal)
      {
        gtx.strokeLine(i, 0, i, this.getHeight());
      }


      for (int j = 0; j < this.getHeight(); j += Main.GridPixal)
      {
        gtx.strokeLine(0, j, this.getWidth(), j);
      }
    }
    if(Main.GridPixal < 5){
      Main.strokeSize = 0;
    }
    else{
      Main.strokeSize = 1.3;
    }


    for (int i = 0; i < (this.getWidth()/Main.GridPixal); i++)
    {


      for (int j = 0; j <(this.getHeight()/Main.GridPixal); j++)
      {
        if (Main.boardState[startX][startY] >= 1)
        {
          gtx.setFill(getAgeColor(Main.boardState[startX][startY]));

          double posX = (i * Main.GridPixal) + Main.strokeSize;
          double posY = (j * Main.GridPixal) + Main.strokeSize;
          gtx.fillRect(posX, posY, Main.GridPixal - (2*Main.strokeSize), Main.GridPixal - (2 * Main.strokeSize));
        }
       if (startY < Main.N-2) startY ++;
      }
      startY = ytemp;
      if(startX < Main.N -2 )startX ++;
    }
  }
}
