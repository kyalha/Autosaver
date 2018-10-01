# Autosaver
An autosaver application made for copy/paste a file or a directory with a timeout.

Coded in Java with a simple structure:
JavaApplication (main function)
JFrame (UI)
Autosave (Logic)

You cna find the executable (jar) in the dist folder.

Some interestins functions:

1/ Load properties / choices from the user into a bat property file when we launch the application:
        
        table = new Properties();
        file = new File("property.dat");
        fileInputStream = new FileInputStream(file); 

        this.table.load(this.fileInputStream);
        this.table.getProperty("pathTo"); 

2/ Save properties: 
        
        FileOutputStream fr = new FileOutputStream(file);
        table.setProperty("minutes", String.valueOf(this.getMinutes()));
        table.setProperty("pathTo", this.getPathTo());
        p.store(fr, "Properties");
        fr.close();

Example of a property bat:
#Properties
#Sun Sep 30 20:52:34 CEST 2018
minutes=15
pathTo=C\:\\Users\\Documents

3/ Copy a file or a directory:
        
        File destDir = new File(pathTo);
        File srcFile = new File(pathFrom);
        FileUtils.copyFile(srcFile, destDir);
        FileUtils.copyDirectoryToDirectory(srcFile, destDir);

Some utils:
        Get free space from current hard disk:
        
        Math.round(FileSystemUtils.freeSpaceKb("/")


