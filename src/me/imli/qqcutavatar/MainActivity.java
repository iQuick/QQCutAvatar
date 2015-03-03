package me.imli.qqcutavatar;

import me.imli.qqcutavatar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private final int REQUEST_CODE = 1;
	
	private ImageView mIvAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
		mIvAvatar.setImageResource(R.drawable.ic_launcher);
		mIvAvatar.setOnClickListener(clickAvatar());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (CutAvatarActivity.bitmap != null) {
					mIvAvatar.setImageBitmap(CutAvatarActivity.bitmap);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private View.OnClickListener clickAvatar() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CutAvatarActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		};
	}
}
