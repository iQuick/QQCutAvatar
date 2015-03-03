# CutAvatarView
===

## 使用

![](https://github.com/iQuick/QQCutAvatar/blob/master/arc/image.jpg)

xml文件布局文件

	<?xml version="1.0" encoding="utf-8"?>
	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
		android:layout_width="match_parent"
		android:layout_height="match_parent" >

		<me.imli.qqcutavatar.view.CutAvatarView
			android:id="@+id/cut_avatar_view"
			android:layout_width="match_parent"
			android:layout_height="match_parent" />

		<Button
			android:id="@+id/btn_cut"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content"
			android:layout_alignParentBottom="true"
			android:layout_alignParentRight="true"
			android:text="截取" />

	</RelativeLayout>
	
Activity代码

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