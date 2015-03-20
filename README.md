# cborConverterWithSHA


 Convert HTML to CBOR Files and CBOR to JSON (as per IST Schema in TargetModel.java). For memex project.
 * 
 * Reads html files with their URLs as their names grouped by directories with the hostname of URLs, 
 * this tool generates a single CBOR file which contains the data of the html pages.
 * 
 * Also, given a large CBOR file as input, this utility converts the large CBOR file into multiple
 * JSON output files grouped as described in the above scheme.
 * 
 * To use, run the .jar file with the following six arguments.
 *
 * 1) Path to CBOR File
 * 2) input Directory of HTML Pages
 * 3) output Directory of HTML Pages
 * 4) mode :
 * t when writing a large cbor file
 * f when reading from a large cbor File
 * 5) name of the User (refer IST Schema)
 * 6) email of the User (refer IST Schema)
 * 
 * 
 * 5 and 6 should be empty string when reading from a large cbor file 
 * 2 should be empty string when converting from a CBOR file and 3 should be null when converting to a large     * CBOR file. 
 * 
 * 
 * @author Rajat Pawar
