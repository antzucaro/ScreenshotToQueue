import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

public class Demo implements Comparable<Demo> {
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

    public Demo(String filename) {
        this.path = filename;

        int lastSlash = filename.lastIndexOf('/');

        if (lastSlash > -1) {
            this.filename = filename.substring(lastSlash+1);
        } else {
            this.filename = filename;
        }

        // pick apart the filename to construct when it occurred using a regex
        String r = "(\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2})_(.+).dem";
        Pattern p = Pattern.compile(r);

        Matcher m = p.matcher(this.filename);
        if (m.matches()) {
            // get the date and time portion
            String dateText = m.group(1);
            try {
                this.dateTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm").parse(dateText);
            } catch(Exception e) {
                this.dateTime = null;
            }

            // set the map name
            this.mapName = m.group(2);

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int compareTo(Demo d) {
        return this.dateTime.compareTo(d.getDateTime());
    }

    public static void main(String[] args) {
        Demo d = new Demo("/home/ant/2013-04-10_23-19_final_rage_duel_v2.dem");
        assert(d.getFilename().equals("2013-04-10_23-19_final_rage_duel_v2.dem"));
        assert(d.getMapName().equals("final_rage_duel_v2"));
        assert(d.getKey().equals(20130410));
    }
}
