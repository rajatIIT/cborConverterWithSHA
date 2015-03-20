package cborExtractorWithSHA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;


/**
 * 
 * Class used to convert a set of HTML Pages to a CBOR File.
 * 
 * 
 * @author Rajat
 *
 */
public class CBORWriter {
	
	private String suppliedContactName;
	private String suppliedContantEmail;
	private String CborOutputPath;
	private CBORGenerator cborFileGenerator;
	private CBORFactory myCborFactory;
	private ObjectMapper mapper;
	
	
	public CBORWriter(String userName, String userEmail){
		myCborFactory = new CBORFactory();
		mapper = new ObjectMapper(myCborFactory);
		suppliedContactName = userName;
		suppliedContantEmail = userEmail;
	}
	
	public void multipleHTMLToSingleCBOR(String htmlPath, String cborOutputPath) throws IOException, NoSuchAlgorithmException{
		
		cborFormalities(cborOutputPath);
		
		File htmlDirectory = new File(htmlPath);
		File[] subDirectories = htmlDirectory.listFiles();
		for(File currentDirectory : subDirectories) {
		
			File[] allHTMLFiles = currentDirectory.listFiles();
			
			for (File singleHTMLFile : allHTMLFiles) {
				
				System.out.println("Converting " + singleHTMLFile.getName());
				
				String fileHTMLContent = getContent(singleHTMLFile.getAbsolutePath());
				
				TargetModel currentModel = createModelFromSource(fileHTMLContent, singleHTMLFile.getName());
				
				cborFileGenerator.writeObject(currentModel);
			}
		}
		closeCBORArray();
	}
	
	
	private void closeCBORArray() throws IOException {
		cborFileGenerator.writeEndArray();
		cborFileGenerator.writeEndObject();
		cborFileGenerator.close();
		
	}

	private void cborFormalities( String cborPath) throws IOException{
		// TODO Auto-generated method stub
		CborOutputPath = cborPath;
		
		if (!((new File(CborOutputPath)).exists())){
			(new File(CborOutputPath)).createNewFile();
		}
		
		
		OutputStream fos = new FileOutputStream(new File(CborOutputPath), true);
		cborFileGenerator = myCborFactory.createGenerator(fos);
		
		cborFileGenerator.writeStartObject();
		cborFileGenerator.writeFieldName("Objects");
		cborFileGenerator.writeStartArray();
	}

	public TargetModel createModelFromSource(String pageSource, String fileName) throws NoSuchAlgorithmException{
		TargetModel myModel = new TargetModel(suppliedContactName, suppliedContantEmail);
		myModel.setContent(pageSource);
		myModel.setUrl(fileName);
		myModel.timestamp=1421064000;
		HashMap<String, Object> h = (HashMap)myModel.request.get("client");
		h.put("hostname", "gray17.poly.edu");
		h.put("address","128.238.182.77");
		h.put("robots", "classic");
		
		HashMap<String, Object> myHeader = (HashMap)myModel.request.get("client");
		String shaKey = "ebola-" + computeSHA1(myModel.url);
		myHeader.put("key", shaKey);
		
		return myModel;
	}
	
	
	private static String computeSHA1(String inputURL) throws NoSuchAlgorithmException {
		//TODO Auto-generated method stub
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(inputURL.getBytes());

		byte byteData[] = md.digest();
		
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
		
		return sb.toString();
	}
	
	
	/**
	 * Read the file content in a String.
	 * 
	 * @param inputfilePath
	 * @return
	 */
	private String getContent(String inputfilePath){
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(inputfilePath));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		String content = contentBuilder.toString();
		return content;
	}
}

