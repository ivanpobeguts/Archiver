package archiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Description
 */
/**
 * The given interface declarates some methods for extracting files from ZIP
 * file and adding them to ZIP. This programm can add files to ZIP, including
 * directories. All the files must be in the working directory. Extracting
 * realises to a directory, which user needs. Also user can add and read
 * comments to ZIP.
 *
 * @author IvanP_000
 *
 */
public interface Archiver {

    /**
     * Adds file to ZIP with recursion for folders
     *
     * @param zout
     * @param szName - file name
     * @throws IOException
     */
    public void addFileToZip(ZipOutputStream zout, String szName) throws IOException;

    /**
     * Extract files with recursion.
     *
     * @param zipFileName
     * @param dstDirectory
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void extractFromZip(String zipFileName, String dstDirectory) throws IOException, FileNotFoundException;

    /**
     * Delete file or directory
     *
     * @param file
     */
    public void delete(File file);

    /**
     *
     * @param filename - ZIP file name
     * @return Returns a ccomment to ZIP file, because class ZipOutputStream
     * haven't got such declared method to read a comment
     * @see ArchiverImpl#getZipCommentFromBuffer(byte[] buffer, int len)
     */
    public String extractZipComment(String filename);
}
