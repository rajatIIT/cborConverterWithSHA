package cborExtractorWithSHA;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;





/**
 * 
 * A utility to allow other memex people and applications to read and write the CBOR data that we generate.
 * 
 * Reads html files with their URLs as their names grouped by directories with the hostname of URLs, 
 * this tool generates a single CBOR file which contains the data of the html pages.
 * 
 * Also, given a large CBOR file as input, this utility converts the large CBOR file into multiple
 * HTML output files grouped as described in the above scheme.
 * 
 * Takes three arguements : 
 * 1) Path to CBOR File
 * 2) input Directory of HTML Pages
 * 3) output Directory of HTML Pages
 * 4) mode : t when writing a large cbor file
 * 			 f when reading from a large cbor File
 * 
 * 2 should be null when converting from a CBOR file and 3 should be null when converting to a large CBOR file. 
 * 
 * 
 * @author Rajat Pawar
 *	
 */
public class MainClass {

	private static String cborFilePath;
	private static String outputDirectory;
	private static String inputDirectory;
	private static String mode;
	private static String userName;
	private static String userEmail; 
	
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
		
		
		cborFilePath = args[0];
		inputDirectory = args[1];
		outputDirectory = args[2];
		mode=args[3];
		userName = args[4];
		userEmail = args[5];
		
		
		
		if(mode.equals("t")){
			CBORWriter myWriter = new CBORWriter(userName,userEmail);
			myWriter.multipleHTMLToSingleCBOR(inputDirectory, cborFilePath); 
			} else if (mode.equals("f")) {
			CBORReader myReader = new CBORReader();
			myReader.singleCBORtoMultipleHTML(cborFilePath, outputDirectory);
			}
	}
}
