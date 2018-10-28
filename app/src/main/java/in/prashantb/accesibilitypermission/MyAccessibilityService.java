package in.prashantb.accesibilitypermission;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.ArrayList;

public class MyAccessibilityService extends AccessibilityService {
  private static final String LOG_TAG = MyAccessibilityService.class.getSimpleName();
  private ArrayList<String> packages;
  private boolean shouldTrack;

  @Override
  protected void onServiceConnected() {
    Log.d("PGB","onServiceConnected");
    AccessibilityServiceInfo info = getServiceInfo();
    populateActivitesTotrack();
    info.packageNames = new String[]
        {
            "com.android.settings"
        };
    info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

    info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
    this.setServiceInfo(info);
  }

  private void populateActivitesTotrack() {
    packages = new ArrayList<>();
    packages.add("com.android.settings.Settings");
  }

  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
    Log.d("PGB","onAccessibilityEvent 0");
    if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      ComponentName componentName = new ComponentName(
          event.getPackageName().toString(),
          event.getClassName().toString()
      );

      ActivityInfo activityInfo = tryGetActivity(componentName);
      boolean isActivity = activityInfo != null;
      if (isActivity) {
        shouldTrack = isActivityToBeTracked(componentName.flattenToShortString());
        Toast.makeText(getApplicationContext(),"Application Open : "+activityInfo.packageName, Toast.LENGTH_LONG).show();
      }
    }
    if (!shouldTrack) {
      return;
    }
  }

  private boolean isActivityToBeTracked(String activityInfo) {
    return packages.contains(activityInfo);
  }

  private ActivityInfo tryGetActivity(ComponentName componentName) {
    try {
      return getPackageManager().getActivityInfo(componentName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      return null;
    }
  }

  @Override
  public void onInterrupt() {
    Log.d("PGB","onInterrupt");
  }
}
