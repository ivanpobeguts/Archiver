package Archiver;

import archiver.Archiver;
import java.util.zip.*;
import java.io.*;

/**
 * This class implements public interface Archiver.
 *
 * @author IvanP_000
 */
public class ArchiverImpl implements Archiver {

    @Override
    public void addFileToZip(ZipOutputStream zout, String szName) throws IOException {
        File file = new File(szName);
        try {
            if (!file.isDirectory()) {
                ZipEntry ze;
                ze = new ZipEntry(szName);

                zout.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(szName);
                {
                    byte[] buf = new byte[8000];
                    int nLength;
                    while (true) {
                        nLength = fis.read(buf);
                        if (nLength < 0) {
                            break;
                        }

                        zout.write(buf, 0, nLength);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        if (file.isDirectory()) {
            String[] list = file.list();
            int i;
            for (i = 0; i < list.length; i++) {
                addFileToZip(zout, szName + File.separator + list[i]);
            }
        }
        zout.closeEntry();

    }

    @Override
    public void extractFromZip(String zipFileName, String dstDirectory) throws IOException, FileNotFoundException {
        byte[] buffer = new byte[8000];
        File dstDir = new File(dstDirectory);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFileName))) {
            ZipEntry ze1 = zis.getNextEntry();
            String nextFileName;
            while (ze1 != null) {
                nextFileName = ze1.getName();
                File nextFile = new File(dstDirectory + File.separator
                        + nextFileName);
                if (ze1.isDirectory()) {
                    nextFile.mkdir();
                } else {
                    new File(nextFile.getParent()).mkdirs();
                    try (FileOutputStream fos
                            = new FileOutputStream(nextFile)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                ze1 = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        } catch (IOException ex) {
            System.out.println("IOException");
        }
    }

    @Override
    public void delete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    @Override
    public String extractZipComment(String filename) {
        String retStr = null;
        try {
            File file = new File(filename);
            int fileLen = (int) file.length();

            /* The whole ZIP comment, including the magic byte sequence 
            (@see ArchiverImpl#getZipCommentFromBuffer(byte[] buffer, int len)),
             * MUST fit in the buffer
             * otherwise, the comment will not be recognized correctly
             *
             * You can safely increase the buffer size if you like
             */
            try (FileInputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[Math.min(fileLen, 8192)];
                int len;

                in.skip(fileLen - buffer.length);

                if ((len = in.read(buffer)) > 0) {
                    retStr = getZipCommentFromBuffer(buffer, len);
                }
            }
        } catch (Exception e) {
            e.toString();
        }
        return retStr;
    }

    /**
     * This private method reads n bytes from the end of the ZIP file and
     * searches (backward) for the magic sequence that indicates the end of the
     * ZIP contents.
     *
     * The ZIP file comment can be found 22 bytes after the beginning of that
     * magic sequence.
     *
     * @param buffer
     * @param len
     * @return
     */
    private static String getZipCommentFromBuffer(byte[] buffer, int len) {
        byte[] magicDirEnd = {0x50, 0x4b, 0x05, 0x06};
        int buffLen = Math.min(buffer.length, len);
        // Check the buffer from the end
        for (int i = buffLen - magicDirEnd.length - 22; i >= 0; i--) {
            boolean isMagicStart = true;
            for (int k = 0; k < magicDirEnd.length; k++) {
                if (buffer[i + k] != magicDirEnd[k]) {
                    isMagicStart = false;
                    break;
                }
            }
            if (isMagicStart) {
                // Magic Start found
                int commentLen = buffer[i + 20] + buffer[i + 21] * 256;
                int realLen = buffLen - i - 22;
                if (commentLen != realLen) {
                }
                String comment = new String(buffer, i + 22, Math.min(commentLen, realLen));
                return comment;
            }
        }
        return null;
    }

}
