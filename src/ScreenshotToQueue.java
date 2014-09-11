import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.nexuiz.demorecorder.application.DemoRecorderApplication;
import com.nexuiz.demorecorder.application.jobs.RecordJob;
import org.apache.commons.configuration.*;

public class ScreenshotToQueue {
    public static void main(String[] args) {
        // obtain configuration from the properties file
        Configuration config = null;
        try {
            config = new PropertiesConfiguration("ScreenshotToQueue.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        String dpVideoPath = config.getString("paths.dpvideo");

        // using the demo paths from the config, create demo instances
        // for every demo file we find in the path
        List<Demo> demos = getDemos(config.getString("paths.demos"));

        // the default duration of a clip
        float clipDuration = config.getFloat("clipDuration");

        // using the screenshot path from the config, create screenshot instances
        // for every screenshot file we find in the path
        List<FragScreenshot> screenshots = getScreenshots(config.getString("paths.screenshots"), clipDuration);

        // index the demos by date (using a key string of "YYYYMMDD")
        HashMap<String, List<Demo>> indexedDemos = indexDemos(demos);

        // map the screenshots to their demos and create
        // RecordJobs for the recorder
        CopyOnWriteArrayList<RecordJob> jobs = createRecordJobs(
                config,
                screenshots,
                indexedDemos);

        // Save the queue file
        saveToJobQueueFile(config, jobs);
    }

    public static List<Demo> getDemos(String path) {
        File dir = new File(path);
        File[] listing = dir.listFiles();

        ArrayList<Demo> demos = new ArrayList<Demo>();

        if (listing != null) {
            for (File f : listing) {
                if (f.getName().endsWith("dem")) {
                    try {
                        Demo d = new Demo(f.getName());
                        demos.add(d);
                    } catch(IllegalArgumentException e) {
                        System.out.println("Could not parse demo "
                                + f.getName() + ". Skipping it.");
                    }
                }
            }
        }

        Collections.sort(demos);
        return demos;
    }

    public static List<FragScreenshot> getScreenshots(String path, float clipDuration) {
        File dir = new File(path);
        File[] listing = dir.listFiles();

        ArrayList<FragScreenshot> screenshots = new ArrayList<FragScreenshot>();

        if (listing != null) {
            for (File f : listing) {
                if (f.getName().endsWith("jpg")) {
                    try {
                        FragScreenshot ss = new FragScreenshot(f.getName(), clipDuration);
                        screenshots.add(ss);
                    } catch(IllegalArgumentException e) {
                        System.out.println("Could not parse screenshot "
                                + f.getName() + ". Skipping it.");
                    }
                }
            }
        }

        Collections.sort(screenshots);
        return screenshots;
    }

    public static HashMap<String, List<Demo>> indexDemos(List<Demo> demos) {
        HashMap<String, List<Demo>> demoMap = new HashMap<String, List<Demo>>();
        for (Demo d : demos) {
            String key = d.getKey();
            List l;
            if (demoMap.containsKey(key)) {
                l = demoMap.get(key);
            } else {
                l = new ArrayList<Demo>();
            }
            l.add(d);
            demoMap.put(key, l);
        }

        return demoMap;
    }

    public static RecordJob createRecordJob(Configuration config, FragScreenshot fs, Demo d, int jobIndex) {

        // all of the jobs will be stored here
        List<RecordJob> jobs = new CopyOnWriteArrayList<RecordJob>();

        DemoRecorderApplication appLayer = null;
        String jobName = d.getMapName() + "_" + d.getKey() + "_" + fs.getEndSecond();
        File enginePath = new File(config.getString("paths.engine"));
        String engineParameters = null;
        File demoFile = new File(config.getString("paths.demos") + d.getPath());
        String relativeDemoPath = "demos";
        File dpVideoPath = new File(config.getString("paths.dpvideo"));
        File videoDestination = new File(config.getString("paths.output") + jobName + ".ogv");
        String executeBeforeCap = null;
        String executeAfterCap = null;
        float startSecond = fs.getStartSecond();
        float endSecond = fs.getEndSecond();

        RecordJob j = new RecordJob(appLayer, jobName, jobIndex, enginePath, engineParameters, demoFile,
                relativeDemoPath, dpVideoPath, videoDestination, executeBeforeCap, executeAfterCap, startSecond,
                endSecond);

        return j;
    }

    public static CopyOnWriteArrayList<RecordJob> createRecordJobs(
            Configuration config,
            List<FragScreenshot> screenshots,
            HashMap<String, List<Demo>> indexedDemos) {
        CopyOnWriteArrayList<RecordJob> jobs = new CopyOnWriteArrayList<RecordJob>();

        for (FragScreenshot s : screenshots) {
            String ssMap = s.getMapName();
            String ssKey = s.getKey();

            if (indexedDemos.containsKey(ssKey)) {
                List<Demo> l = indexedDemos.get(ssKey);

                Demo foundDemo = null;

                for (Demo d : l) {
                    if (d.getDateTime().compareTo(s.getDateTime()) > 0) {
                        break;
                    } else {
                        foundDemo = d;
                    }
                }

                int i = 0;

                RecordJob j;
                if (foundDemo != null && foundDemo.getMapName().equals(s.getMapName())) {
                    System.out.println("Matching demo for " + s.getFilename() + " is " + foundDemo.getFilename());
                    j = createRecordJob(config, s, foundDemo, i);
                    jobs.add(j);
                    i++;
                }
            } else {
                System.out.println("Unable to find a matching demo file for " + s.getFilename());
            }
        }

        return jobs;
    }

    public static void saveToJobQueueFile(Configuration config, CopyOnWriteArrayList<RecordJob> jobs) {
        File path = new File(config.getString("paths.output_jobfile"));
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                File parentDir = path.getParentFile();
                if (!parentDir.exists()) {
                    try {
                        if (parentDir.mkdirs() == true) {
                            try {
                                path.createNewFile();
                            } catch (Exception ex) {
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }
        try {
            FileOutputStream fout = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(jobs);
            oos.close();
        } catch (Exception e) {
        }
    }
}