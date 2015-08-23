# PseudoMaterialHeader
A library that simulated the material header. Guide by UltraPullToRefresh.
The mechanism is similar to Android NestedScrolling and provided interface to make it compatible with any custom view.

![](https://github.com/captainbupt/PseudoMaterialHeader/blob/master/screenrecord.gif)
# Update
 * Added more callback functions to listener
 * Fixed a bugs, so that PullToRefresh can worked properly

# Usage
In layout:
    
    <!-- define the attribute you need -->
    <com.captainhwz.layout.MaterialHeaderLayout xmlns:myattr="http://schemas.android.com/apk/res-auto"
        android:id="@+id/layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        myattr:content="@+id/content"
        myattr:header="@+id/header"
        myattr:minHeight="50dp">

        <com.captainhwz.demo.header.TranslationHeader
            android:id="@+id/header"
            android:layout_width="match_parent"
            android:layout_height="200dp" />

        <com.captainhwz.demo.content.ViewPagerContentLayout
            android:id="@+id/content_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </com.captainhwz.layout.MaterialHeaderLayout>
For header:
    
    public class TranslationHeader extends RelativeLayout implements HeaderHandler {
	    ...
	
	    @Override
	    public void onChange(float ratio, float offsetY) {
	        // animate as you wish
	    }
    }

For content(Only custom layout need implement ContentHandler. ListView, ScrollView and other basic layout are supported automatically):

	public class ViewPagerContentLayout extends LinearLayout implements ContentHandler {
	    ...
	
	    @Override
	    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
			// return true if the content view cannot pull down
	    }
	}
# Reference
 * [UltraPullToRefresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)
 * [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)
