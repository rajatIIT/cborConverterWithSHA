package cborExtractorWithSHA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

/**
 * 
 * Read a CBOR file containing multiple pages and write the info about pages in a separate folder.  
 * 
 * @author vaio
 *
 */
public class CBORReader {
	
	String currentFile;
	private CBORFactory myFactory;
	private FileInputStream fis;
	private ObjectMapper mapper;
	private CBORParser myCborParser;
	private JsonFactory myJsonFactory;
	private String cborOutputDirectory,cborFilePath;
	private int objectOpen=0;
	private JsonToken currentToken;
	private JsonGenerator myJsonGenerator;
	private boolean responseFlag;
	private ObjectMapper cborMapper;
	private FileOutputStream cborOutputStream;
	
	
	
	public CBORReader() throws IOException{
	
		
		
	}
	
	
	@SuppressWarnings("deprecation")
	public void singleCBORtoMultipleHTML(String cborFilePath, String outputDirectory) throws IOException{
		
		// read the CBOR File sequentially
		this.cborFilePath = cborFilePath ;
		this.cborOutputDirectory = outputDirectory ;
				
		JsonFactory myJsonFactor = new JsonFactory();
		
		(new File(cborOutputDirectory + File.separator + "allFields")).mkdir();
		
		initializeInputFile();
		
		initializeOutputFile();
		
		while(objectOpen!=0){
			
			// write  a general code for every next token
			if (currentToken.equals(JsonToken.START_OBJECT))
			{
				if (objectOpen==1)
					 {
					
					//do not write anything because we will be writing this in the next step of URL
					
					 } else {
						 myJsonGenerator.writeStartObject();
					}
				objectOpen++;
				advanceToken();
			} else if (currentToken.equals(JsonToken.FIELD_NAME)){
			
				if(myCborParser.getText().equals("response"))
				{	responseFlag=true;
			
				}
				
				// if filed name is URL, we create a new file
				// also if fieldName is body and responseFlag is set write to the sources file
			if (myCborParser.getText().equals("url") && objectOpen==2){
				
				advanceToken();
				String currentCBORURL = myCborParser.getValueAsString();
				System.out.println("Processing " + currentCBORURL);
				refreshFile(currentCBORURL);
				myJsonGenerator.writeStartObject();
				myJsonGenerator.writeStringField("url",currentCBORURL);
				advanceToken();
			} else {
				myJsonGenerator.writeFieldName(myCborParser.getText());
				advanceToken();
			} 
			} else if (currentToken.equals(JsonToken.END_OBJECT)){
				
				 if (objectOpen==2) {
					 // close the generator too here
					 myJsonGenerator.writeEndObject();
					 myJsonGenerator.close();
				 } else if (objectOpen==1 || objectOpen==0){	 
				 }else {
					 // simply write an end object
					 myJsonGenerator.writeEndObject();
				 }
				 advanceToken();
				 objectOpen--;
			} else if (currentToken.equals(JsonToken.VALUE_STRING)) {
				
				myJsonGenerator.writeString((myCborParser.getText()));
				advanceToken();
			}  else if (currentToken.equals(JsonToken.VALUE_NUMBER_INT)) {
			
				myJsonGenerator.writeNumber(myCborParser.getText());
				advanceToken();
			} else if (currentToken.equals(JsonToken.VALUE_NULL)) {
				
				myJsonGenerator.writeNull();
				advanceToken();
			} else {
				advanceToken();
			}
		}
			myCborParser.close();
	}
		
	private void refreshFile(String valueAsString) throws IOException {
		// TODO Auto-generated method stub

		URL fileURL = new URL(URLDecoder.decode(valueAsString));
		
		File currentDirectoryPath = new File(cborOutputDirectory + File.separator + "allFields" + File.separator + fileURL.getHost());
		if(!(currentDirectoryPath).exists())
			currentDirectoryPath.mkdir();
		
		
		
		File currentAllSourceFile = new File(cborOutputDirectory + File.separator + "allFields" + File.separator + fileURL.getHost() + File.separator +  valueAsString);
		
		if(!currentAllSourceFile.exists())
			currentAllSourceFile.createNewFile();
		
		
		
		FileOutputStream myFOS = new FileOutputStream(currentAllSourceFile);
		myJsonGenerator = myJsonFactory.createGenerator(myFOS);
		myJsonGenerator.useDefaultPrettyPrinter();
		// Now the generator is ready for writing
		
		
	}


	private void initializeOutputFile() throws IOException {
		myJsonFactory = new JsonFactory();
		// cbor parser at start object
		advanceToken();
		objectOpen++;
		// cbor parser at name objects
		advanceToken();
		// cbor parser at start array
		advanceToken();
		//cbor parser at start object for a new file 
		advanceToken();
	}

	private void advanceToken() throws IOException {
	//	System.out.println("Passing token: " + myCborParser.getText());
		currentToken = myCborParser.nextToken();
	}

	private void initializeInputFile() throws IOException {
		myFactory = new CBORFactory();
		fis = new FileInputStream(cborFilePath);
		mapper = new ObjectMapper(myFactory);
		myCborParser = myFactory.createParser(fis);
}
}

