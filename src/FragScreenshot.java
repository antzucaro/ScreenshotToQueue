import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

public class FragScreenshot implements Comparable<FragScreenshot> {
    // the full path of the file
    private String path;

    // the provided raw filename, sans path
    private String filename;

    // the key used for storing this in hashtable data structures
    private String key;

    // the date and time the demo occurred
    private Date dateTime;

    // the map played in the demo
    private String mapName;

    // the start and end time of the frag that the screenshot is recording
    // note: this is in server seconds, not clock time
    private float startSecond;
    private float endSecond;

    // the overall duration of the clips to generate. used to calculate startSecond
    private float clipDuration;

    public FragScreenshot(String filename, float clipDuration) {
        this.path = filename;

        int lastSlash = filename.lastIndexOf('/');

        this.clipDuration = clipDuration;

        if (lastSlash > -1) {
            this.filename = filename.substring(lastSlash+1);
        } else {
            this.filename = filename;
        }

        // pick apart the filename to construct when it occurred using a regex
        String r = "(\\w+)-(\\d{1,5}).\\d+-(\\d+)-\\d+.jpg";
        Pattern p = Pattern.compile(r);

        Matcher m = p.matcher(this.filename);
        if (m.matches()) {
            // get the date and time portion
            String dateText = m.group(3);
            try {
                this.dateTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(dateText);
            } catch(Exception e) {
                this.dateTime = null;
            }

            // set the map name
            this.mapName = m.group(1);

            // grab the ending second from the filename
            this.endSecond = Float.parseFloat(m.group(2)) + 3f;

            // make the starting second 10s before the end
            this.startSecond = this.endSecond - this.clipDuration;

            // and finally, the key is based on the above parsed date
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            this.key = f.format(this.dateTime);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public float getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(float startSecond) {
        this.startSecond = startSecond;
    }

    public float getEndSecond() {
        return endSecond;
    }

    public void setEndSecond(float endSecond) {
        this.endSecond = endSecond;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int compareTo(FragScreenshot f) {
        return this.dateTime.compareTo(f.getDateTime());
    }

    public static void main(String[] args) {
        FragScreenshot f = new FragScreenshot("final_rage_duel_v2-548.133423-20140621234048-00.jpg", 10.0f);
        System.out.println(f.getMapName());
        System.out.println(f.getDateTime());
        System.out.println(f.getEndSecond());
    }
}
