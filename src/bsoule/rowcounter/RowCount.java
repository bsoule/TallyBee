package bsoule.rowcounter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Uses a TextSwitcher.
 */
public class RowCount extends Activity implements ViewSwitcher.ViewFactory,
        View.OnClickListener {

	private static final String TAG = "TALLYBEE!";
	private static final int RESET_ID = 0;
	private static final int DEC_ID = 1;
	private static final int INC_ID = 2;
	private static final int REG_SIZE = 128;
	private static final int SM_SIZE = 84;
	
	private Vibrator mVibe;
    private TextSwitcher mSwitcher;
    private LinearLayout mLay;
    private SharedPreferences mPrefs;
    private int mCounter;
   
    private static int mDec;
    private static int mColor;
    private static final long haptic = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up screen
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // initialize counter from memory
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCounter = mPrefs.getInt("COUNT", 0);
        mDec = mPrefs.getInt("DEC", 1);        
        
        // initialize member variables
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSwitcher = (TextSwitcher) findViewById(R.id.counter);
        mSwitcher.setFactory(this);
        mLay = (LinearLayout) findViewById(R.id.lay);
        if (mDec == -1) mColor = Color.RED;
        else mColor = Color.BLUE;

        // set up the switcher's animations
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);
        
        // set the onClickListener
        mLay.setOnClickListener(this);
        //mLay.setOnTouchListener(this);
        // set the counter view
        updateCounter();
    }

    public void onClick(View v) {
        mCounter += mDec;
        updateCounter();
        mVibe.vibrate(haptic);
    }
    
    private void updateCounter() {
        mSwitcher.setText(String.valueOf(mCounter));
        TextView t = (TextView) mSwitcher.getCurrentView();
        t.setGravity(Gravity.CENTER);
        if (mCounter > 9999 || mCounter < -999) {
        	t.setTextSize(SM_SIZE);
        } else {
        	t.setTextSize(REG_SIZE);
        }
        t.setTextColor(mColor);
    }

    public View makeView() {
        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(REG_SIZE);
        return t;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean res = super.onCreateOptionsMenu(menu);
    	menu.add(0, RESET_ID, 0, R.string.menu_reset);
    	menu.add(0, DEC_ID, 0, R.string.menu_dec);
    	menu.add(0, INC_ID, 0, R.string.menu_inc);
    	setMenuCountDir(menu);
    	return res;
    }
    
    private void setMenuCountDir(Menu menu) {
    	MenuItem dec = menu.findItem(DEC_ID);
    	MenuItem inc = menu.findItem(INC_ID);
    	dec.setEnabled(mDec == 1);
    	inc.setEnabled(mDec == -1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case RESET_ID:
        	mCounter = 0;
        	updateCounter();
        	return true;
        case DEC_ID:
        	mDec = -1;
        	mColor = Color.RED;
        	break;
        case INC_ID:
        	mDec = 1;
        	mColor = Color.BLUE;
        	break;
        }
        updateCounter();
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putInt("COUNT", mCounter);
		edit.putInt("DEC", mDec);
		edit.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCounter = mPrefs.getInt("COUNT", 0);
		mDec = mPrefs.getInt("DEC", 1);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		setMenuCountDir(menu);
		return super.onPrepareOptionsMenu(menu);
	}
    
}
