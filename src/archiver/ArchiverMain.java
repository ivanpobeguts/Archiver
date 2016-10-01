package Archiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.ZipOutputStream;

/**
 * This is a main class. When running the programm, you must follow the
 * instructions, which will help you. You must know the count of your files,
 * which you would like to zip. If you don't want set a comment, you don't have
 * to, but this option is also sugested. You MUST add only different files. The
 * case, when file already exist in the ZIP, isn't thrown.
 *
 * @author IvanP_000
 */
public class ArchiverMain {

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        ArchiverImpl arc = new ArchiverImpl();
        Scanner sc = new Scanner(System.in);
        System.out.println("Wellcome to the Archiver! If you wish to make an zip, press '1',"
                + " if you wish to extract files from archive, press '2'");
        String choise = sc.nextLine();
        if ("1".equals(choise)) {
            System.out.println("Archive name:");
            String zipName = sc.nextLine();
            System.out.println("Set number of files:");
            int size = Integer.parseInt(sc.nextLine());
            String[] s2 = new String[size];
            int i;
            //Reading the files and adding them to array
            System.out.println("Print files' names:");
            for (i = 0; i < s2.length; i++) {
                s2[i] = sc.nextLine();
            }
            File[] f = new File[size];
            int j;
            int k;
            int t;
            for (j = 0; j < s2.length; j++) {
                f[j] = new File(s2[j]);
                if (!f[j].exists()) {
                    System.out.println("\nNot found: " + s2[j]);
                    System.exit(0);
                }
            }
            // Creating temp folder and temporary zipfile to copy existing files there
            File zip = new File(zipName);
            File tmpFile = new File("temp");
            File tmpZip = new File("temp.zip");
            if (zip.exists()) {
                tmpFile.mkdir();
                //Extracting existing files to a folder
                arc.extractFromZip(zipName, "temp");
                try (ZipOutputStream zout1 = new ZipOutputStream(new FileOutputStream("temp.zip"))) {
                    String com = arc.extractZipComment(zipName);
                    File[] files = tmpFile.listFiles();
                    //Adding existing files to temporary zip
                    for (t = 0; t < files.length; t++) {
                        if (files[t].exists()) {
                            arc.addFileToZip(zout1, files[t].getName());
                        } else {
                            break;
                        }
                    }
                    // Adding new files
                    zout1.setComment(com);
                    for (k = 0; k < s2.length; k++) {
                        arc.addFileToZip(zout1, s2[k]);
                    }
                    System.out.println("Do you want to set a comment to ZIP file? (Y/N)");
                    String choise2 = sc.nextLine();
                    if ("Y".equals(choise2)) {
                        System.out.println("Comment:");
                        zout1.setComment(sc.nextLine());
                        System.out.println("New comment: " + arc.extractZipComment("temp.zip"));
                    }
                }
                // Delete old ZIP and rename the new
                arc.delete(tmpFile);
                arc.delete(zip);
                tmpZip.renameTo(zip);
                System.out.println("Old comment: " + arc.extractZipComment(zipName));
            }
            if (!zip.exists()) {

                try (ZipOutputStream zout2 = new ZipOutputStream(new FileOutputStream(zipName))) {
                    System.out.println("Add a comment, if you want:");
                    String comment = sc.nextLine();
                    zout2.setComment(comment);
                    for (k = 0; k < s2.length; k++) {
                        arc.addFileToZip(zout2, s2[k]);

                    }

                }

            }
        } else if ("2".equals(choise)) {
            System.out.println("Enter archive name and destonation path:");
            String zipName = sc.nextLine();
            String dstName = sc.nextLine();
            arc.extractFromZip(zipName, dstName);
        }

    }
}
