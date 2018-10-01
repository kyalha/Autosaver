/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rautosave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.lang.ProcessBuilder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.JFileChooser;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author ADMINIBM
 */
public class Autosave extends TimerTask {

    JFrame frame;
    ProcessBuilder pb;
    Properties table;
    FileInputStream fileInputStream;
    File file;

    String pathFrom;
    String pathTo;
    String fileOrFolder;
    String fileName;
    String commands;
    String properties;

    Boolean isDir;
    Boolean isBiggerThan1GB;
    Boolean start;
    Boolean stop;
    Boolean timerCancelled;
    Boolean continueDoing;

    Integer minutes;
    Integer size;
    Integer times;

    Date time;
    Timer timer;

    public Autosave() {
        commands = "xcopy";
        properties = "/e /i /h";
        times = 0;
        try {
            timer = new Timer();
            table = new Properties();
            file = new File("property.dat");
            file.createNewFile();
            fileInputStream = new FileInputStream(file);
            this.isBiggerThan1GB = false;
            this.loadProperties();
        } catch (IOException ex) {
            Logger.getLogger(Autosave.class.getName()).log(Level.SEVERE, null, ex);
        }
        continueDoing = true;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public String choose(Integer type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(type);
        String input = "";
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.setFileName(chooser.getSelectedFile().getName());
            this.setType(chooser.getSelectedFile().isDirectory());
            input = chooser.getSelectedFile().getAbsolutePath();
        }
        return input;
    }

    public void setFileInformation() {
        if (this.isDir) {
            double bytes = this.getFolderSize();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            double gigabytes = (megabytes / 1024);
            if (gigabytes >= 1) {
                this.isBiggerThan1GB = true;
            }
            this.frame.setFileSizeArea(Double.toString(kilobytes) + " kilobytes, " + Double.toString(megabytes) + " megabytes, " + Double.toString(gigabytes) + " gigabytes");
        } else {
            double bytes = this.getFileSize()[0];
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            double gigabytes = (megabytes / 1024);
            if (gigabytes >= 1) {
                this.isBiggerThan1GB = true;
            }
            this.frame.setFileSizeArea(Double.toString(kilobytes) + " kilobytes, " + Double.toString(megabytes) + " megabytes, " + Double.toString(gigabytes) + " gigabytes");
        }
    }

    public double[] getFileSize() {
        File srcFile = new File(this.getPathFrom());
        double[] values = new double[4];
        double bytes = srcFile.length();
        values[0] = bytes;
        double kilobytes = (bytes / 1024);
        values[1] = kilobytes;
        double megabytes = (kilobytes / 1024);
        values[2] = megabytes;
        double gigabytes = (megabytes / 1024);
        values[3] = gigabytes;
        return values;
    }

    public double getFolderSize() {
        File directory = new File(this.getPathFrom());
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += file.length();
            }
        }
        return length;
    }

    public boolean acceptToCopy(String text) {
        int input = new JOptionPane().showConfirmDialog(null, "<html><body><p style='width: 200px;'>" + text + "</p></body></html>");
        if (input == 1 || input == 2) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean minutesAreCorrect() {
        if (this.frame.getMinutes() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public Integer startTimer(Autosave autosave) throws IOException {
        if (this.minutesAreCorrect() == false) {
            JOptionPane.showMessageDialog(null,
                    "Error: Please enter number bigger than 0", "Error Massage",
                    JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        if (this.isBiggerThan1GB == true) {
            boolean accept = this.acceptToCopy("Are you sure you want to copy a file equal or bigger than 1GB?");
            if (accept == false) {
                return 0;
            }
        }
        if (autosave.continueDoing == false) {
            return 0;
        }

        this.timer.purge();
        this.continueDoing = false;
        this.save();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.setSaveArea("The process started.");
                try {
                    if (Math.round(FileSystemUtils.freeSpaceKb("/") * 0.000001) < 20) {
                        boolean accept = autosave.acceptToCopy("They are less than 20GB available, are you extremely sure about copying file bigger than 1GB?");
                        if (accept == false) {
                            return;
                        }
                    }
                } catch (IOException ex) {

                }
                autosave.times++;
                String pathTo = autosave.getPathTo();
                String pathFrom = autosave.getPathFrom();
                File destDir = new File(pathTo);
                File srcFile = new File(pathFrom);
                if (autosave.isDir) {
                    destDir = new File(pathTo + "\\" + autosave.getFileName() + autosave.times);
                } else {
                    destDir = new File(pathTo + "\\" + autosave.getFileName().replace(".", autosave.times + "."));
                }
                destDir.setReadable(true, false);
                destDir.setExecutable(true, false);
                destDir.setWritable(true, false);
                srcFile.setReadable(true, false);
                srcFile.setExecutable(true, false);
                srcFile.setWritable(true, false);
                if (autosave.isDir) {
                    try {
                        FileUtils.copyDirectoryToDirectory(srcFile, destDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileUtils.copyFile(srcFile, destDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String content = " Autosave " + autosave.getTimes() + " made at " + timeStamp + '\n';
                autosave.setContinueDoing(true);
                frame.setSaveArea(content);
            }
        }, 0, minutes * 60 * 1000);
        return this.times;
    }

    public Boolean loadProperties() {
        try {
            this.table.load(this.fileInputStream);
            if (this.table.getProperty("pathTo") != null && this.table.getProperty("minutes") != null) {
                this.pathTo = this.table.getProperty("pathTo");
                this.minutes = Integer.parseInt(this.table.getProperty("minutes"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveProperties(Properties p) throws IOException {
        FileOutputStream fr = new FileOutputStream(file);
        p.store(fr, "Properties");
        fr.close();
        System.out.println("After saving properties:" + p);
    }

    public void save() throws IOException {
        table.setProperty("minutes", String.valueOf(this.getMinutes()));
        table.setProperty("pathTo", this.getPathTo());
        System.out.println("Properties has been set in HashTable:" + table);
        //saving the properties in file
        saveProperties(table);
    }

    public Boolean getContinueDoing() {
        return continueDoing;
    }

    public void setContinueDoing(Boolean continueDoing) {
        this.continueDoing = continueDoing;
    }

    public Boolean getTimerCancelled() {
        return timerCancelled;
    }

    public void setTimerCancelled(Boolean timerCancelled) {
        this.timerCancelled = timerCancelled;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void stopTimer() {
        this.timer.cancel();
        this.timerCancelled = true;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public Boolean getType() {
        return isDir;
    }

    public void setType(Boolean isDir) {
        this.isDir = isDir;
    }

    public ProcessBuilder getPb() {
        return pb;
    }

    public void setPb(ProcessBuilder pb) {
        this.pb = pb;
    }

    public Boolean getStart() {
        return start;
    }

    public void setStart(Boolean start) {
        this.start = start;
    }

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPathFrom() {
        return pathFrom;
    }

    public void setPathFrom(String pathFrom) {
        this.pathFrom = pathFrom;
    }

    public String getPathTo() {
        return pathTo;
    }

    public void setPathTo(String pathTo) {
        this.pathTo = pathTo;
    }
}
