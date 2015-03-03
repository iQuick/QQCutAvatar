package me.imli.qqcutavatar;

import me.imli.qqcutavatar.view.CutAvatarView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

public class CutAvatarActivity extends Activity {
	
	public static Bitmap bitmap;

	
	private CutAvatarView mCutAvatarView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cut_avatar);
		
		mCutAvatarView = (CutAvatarView) findViewById(R.id.cut_avatar_view);
		mCutAvatarView.setImageResource(R.drawable.avatar);
		
		findViewById(R.id.btn_cut).setOnClickListener(doCut());
	}
	
	
	private View.OnClickListener doCut() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
				bitmap = mCutAvatarView.clip(true);
				setResult(RESULT_OK);
				finish();
			}
		};
	}
	
}
