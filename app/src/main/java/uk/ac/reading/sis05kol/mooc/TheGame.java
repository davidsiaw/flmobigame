package uk.ac.reading.sis05kol.mooc;

//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class TheGame extends GameThread{

    //Will store the image of a ball
    private Bitmap mBall;
    private Bitmap mPaddle;

    //The X and Y position of the ball on the screen (middle of ball)
    private float mBallX = 0;
    private float mBallY = 0;

    //The speed (pixel/second) of the ball in direction X and Y
    private float mBallSpeedX = 0;
    private float mBallSpeedY = 0;

    private float mPaddleDestinationX = 0;
    private float mPaddleX = 0;
    private float mPaddleY = 0;
    private float mPaddleSpeedX = 0;

    private float mSmileyX = 0;
    private float mSmileyY = 0;


    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView) {
        //House keeping
        super(gameView);

        //Prepare the image so we can draw it on the screen (using a canvas)
        mBall = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.small_red_ball);

        mPaddle = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smiley_ball);
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {
        //Initialise speeds
        mBallSpeedX = 200;
        mBallSpeedY = 200;

        //Place the ball in the middle of the screen.
        //mBall.Width() and mBall.getHeigh() gives us the height and width of the image of the ball
        mBallX = mCanvasWidth / 2;
        mBallY = mCanvasHeight / 2;

        mPaddleX = mCanvasWidth / 2;
        mPaddleY = mCanvasHeight - mPaddle.getHeight() / 2;

        mSmileyX = mCanvasWidth / 2;
        mSmileyY = mCanvasHeight / 4;
    }

    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to draw on do nothing
        //It is ok not understanding what is happening here
        if(canvas == null) return;

        super.doDraw(canvas);

        //draw the image of the ball using the X and Y of the ball
        //drawBitmap uses top left corner as reference, we use middle of picture
        //null means that we will use the image without any extra features (called Paint)
        canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2, mBallY - mBall.getHeight() / 2, null);

        canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth() / 2, mPaddleY - mPaddle.getHeight() / 2, null);

        canvas.drawBitmap(mPaddle, mSmileyX - mPaddle.getWidth() / 2, mSmileyY - mPaddle.getHeight() / 2, null);
    }

    //This is run whenever the phone is touched by the user

	@Override
	protected void actionOnTouch(float x, float y) {
        mPaddleSpeedX = (x - mPaddleX) * 10;
        mPaddleDestinationX = x;
	}

	//This is run whenever the phone moves around its axises 
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {

    }

    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {

        float repulsionDist = mBall.getWidth() / 2 + mPaddle.getWidth() / 2;
        float repulsionDistSq = repulsionDist * repulsionDist;

        float ballPaddleDistX = mBallX - mPaddleX;
        float ballPaddleDistY = mBallY - mPaddleY;
        float ballPaddleDistSq = ballPaddleDistX * ballPaddleDistX + ballPaddleDistY * ballPaddleDistY;

        if (ballPaddleDistSq < repulsionDistSq) {
            // Collide!
            float mBallSpeed = (float)Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            mBallSpeedX = mBallX - mPaddleX;
            mBallSpeedY = mBallY - mPaddleY;

            float mBallNewSpeed = (float)Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            mBallSpeedX = mBallSpeedX * mBallSpeed / mBallNewSpeed;
            mBallSpeedY = mBallSpeedY * mBallSpeed / mBallNewSpeed;

        }

        float smileyPaddleDistX = mBallX - mSmileyX;
        float smileyPaddleDistY = mBallY - mSmileyY;
        float smileyPaddleDistSq = smileyPaddleDistX * smileyPaddleDistX + smileyPaddleDistY * smileyPaddleDistY;

        if (smileyPaddleDistSq < repulsionDistSq) {
            // Collide!
            float mBallSpeed = (float)Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            mBallSpeedX = mBallX - mPaddleX;
            mBallSpeedY = mBallY - mPaddleY;

            float mBallNewSpeed = (float)Math.sqrt(mBallSpeedX * mBallSpeedX + mBallSpeedY * mBallSpeedY);

            mBallSpeedX = mBallSpeedX * mBallSpeed / mBallNewSpeed;
            mBallSpeedY = mBallSpeedY * mBallSpeed / mBallNewSpeed;

            updateScore(1);
        }

        if ((mBallX - mBall.getWidth()/2 < 0 && mBallSpeedX < 0) ||
                (mBallX + mBall.getWidth()/2 > mCanvasWidth && mBallSpeedX > 0)) {
            mBallSpeedX = -mBallSpeedX;
        }

        if ((mBallY - mBall.getHeight()/2 < 0 && mBallSpeedY < 0)) {
            mBallSpeedY = -mBallSpeedY;
        }

        if (mBallY + mBall.getHeight()/2 > mCanvasHeight && mBallSpeedY > 0) {
            setState(GameThread.STATE_LOSE);
        }

        // Calculate the new ball position using the speed (pixel/sec)
        float newBallX = mBallX + secondsElapsed * mBallSpeedX;
        float newBallY = mBallY + secondsElapsed * mBallSpeedY;

        // Update ball position
        mBallX = newBallX;
        mBallY = newBallY;

        if ((mPaddleX - mPaddle.getWidth()/2 < 0 && mPaddleSpeedX < 0) ||
                (mPaddleX + mPaddle.getWidth()/2 > mCanvasWidth && mPaddleSpeedX > 0)) {
            mPaddleSpeedX = -mPaddleSpeedX;
        }

        if (mPaddleX < mPaddleDestinationX && mPaddleSpeedX < 0 || mPaddleX > mPaddleDestinationX && mPaddleSpeedX > 0)
        {
            mPaddleSpeedX = 0;
        }

        mPaddleX = mPaddleX + secondsElapsed * mPaddleSpeedX;
    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
