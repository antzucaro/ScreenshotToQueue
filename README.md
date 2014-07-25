ScreenshotToQueue is a small Java application to assist in recording Xonotic video clips. As [my blog post](http://www.xonotic.org/2014/06/capturing-your-favorite-xonotic-moments/) describes, there is some setup involved before you get up and running. I'd highly recommend a quick read through that before starting with this. 

Done reading? Okay. One thing to remember about this project is that it aims to replace the Python script piece of the blog post. Instead of saving a file that you then have to manually enter into the demo recorder tool, this application generates a Nexuiz Demo Recorder-compatible job queue file for you. Nice! Before you get to that point, though, you should start downloading the dependencies for this project. They are as follows:

- Apache Commons Configuration, version 1.1 (with Commons Lang version 2.6)
- Nexuiz Demo Recorder, version 0.3 (available [here](http://forums.xonotic.org/showthread.php?tid=1447))

If you aren't up for getting these dependencies and compiling from source, please use one of the premade JAR files available on the Xonotic forums.

After compiling this using your build tool or IDE of choice (this project was exported using Intellij IDEA), it is then time to move on to setting up your properties file so that the app can find what it needs on your system. Open up the ScreenshotToQueue.properties file and make edits as you see fit, making sure to abide by the comments in the file. Once done, you can run the tool like so:

    java -jar ScreenshotToQueue.jar

It will spit the mappings out to your command line and will also write to the file you specified in your properties file. Open this output file using Nexuiz Demo Recorder, modify your clip settings as needed, and hit "start processing" to begin! You'll have a directory full of fantastic Xonotic clips in no time at all. 

Happy fragging!
-Antibody
