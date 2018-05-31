# AA241x Drone Controller App

## To-dos for teams

### TODO1: Setup appkey
- Create a new App on DJI Developer center with identifier “edu.stanford.aa.dronecontroller”
- Set the “com.dji.sdk.API_KEY” to the App key in AndroidManifest.xml

### TODO2: Modify onClickHandler in DroneControlActivity.java
- Set yaw / pitch-roll / throttle control modes
- Set coordinate system
- Set fail-safe behavior
- …

### TODO3: FlyTask
- For each “command” we received, we will create a FlyTask thread to control the flying process
- e.g. a PD / PID controller

### TODO4: MsgEvents
- Set the behavior when you receive a “command” message with a destination point and a velocity value.


## How to Run


### Step 0.

Enable "Android Debugging Mode" on your Android device. Download ADB at https://developer.android.com/studio/releases/platform-tools (Mac users can run `brew cask install android-platform-tools` if they are using homebrew).

### Step 1.

Assume the SioServer uses port 9090.

We run the following command in terminal (or Windows command prompt):
```
adb devices                         # to ensure the device is connected
adb reverse tcp:9001 tcp:9090       # set reverse port forwarding
```
To map the port 9090 on computer to port 9001 on mobile device. Every time you reconnect your mobile with the laptop, you need to run this again.

### Step 2.

Run app (MainActivity), set the address to http://127.0.0.1:9001 , click on "Connect". Then the app should be able to communicate with the SioServer on your laptop, and you should be able to see logs in Logcat panel in Android Studio.

### Step 3.

Connect to the drone's (or RC's) wireless network on your mobile. Then usually within minutes you will be able to click on the "open" button. You can try connecting / disconnecting to the cellular network if it doesn't respond.

### Step 4.

Press "Config" to configure the drone flying state.

After that you can try pressing the "Square" button to see if it can draw a square correctly.

### Step 5.

After pressing "Get Drone State", the App will print the drone state to Logcat (if you do not want this you can comment out Line 28 in DroneStateTask.java) and send the drone state message to SioServer. You can press "Stop" to let the app stop sending drone states.

### Step 6.

You are now all set! Every time when SioServer sends you a "command" message, the app will create a thread (`FlyTask`) to handle the "command" message with destination points and velocities. This should be implemented by your team.


