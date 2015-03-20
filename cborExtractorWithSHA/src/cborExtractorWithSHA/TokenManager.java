package cborExtractorWithSHA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;

/**
 * 
 * Manager CBOR Tokens. Used to writer a single Object to file.
 * 
 * input : a reference to JSONParser
 * 
 * The input file should contain CBOR Objects of the field : URL, timeStamp, request and response.
 * 
 * @author vaio
 *
 */
public class TokenManager {
	
	CBORParser myJsonParser;
	String url, urlHost;
	String timestamp;
	JsonToken currentToken;
	String outputPath;
	private File allFieldsFile;
	private File allSourceFile;
	JsonGenerator myJsonGenerator;
	JsonFactory myJsonFactory; 
	
	
	
	
	public TokenManager(CBORParser inputJSONParser, String targetOutputDirectory) throws JsonParseException, IOException{
		
		myJsonParser = inputJSONParser;
		outputPath = targetOutputDirectory;
		myJsonFactory = new JsonFactory();
		

		// using JSON Parser first secure required info
		// get URL
		// get timeStamp
		getURLandTimeStamp();
		

		// Then, initialize file 
		initializeBothFiles();
		
 
		// write URL and timestamp to file
		writeURLandTimeStamp();
		
		
		writeRequestBlock();
				// get and write request
				// get and write response
			
		
		
		// Then, write
		
	}



	private void writeRequestBlock() throws IOException {
		// TODO Auto-generated method stub
		
		
	// next token is Requst
		
		myJsonGenerator.writeFieldName("request");
		currentToken = myJsonParser.nextToken();		// start object for request block
		myJsonGenerator.writeStartObject();
		
		currentToken = myJsonParser.nextToken();		
		
		for (int i=0;i<4;i++) {							// request has 4 parameters: headers, body, client and method
		if (currentToken.toString().equals("headers"))
			handleHeader();
		else if (currentToken.toString().equals("client"))
			handleClient();
		else {
			// methods or body : for either write the keyw and the value
			myJsonGenerator.writeStringField(currentToken.toString(), myJsonParser.nextValue().toString());
		}
		}
	}
	
	
	private void handleClient(){
		
		
		
	}

	private void handleHeader() throws IOException{
		
		// next token is headers
		myJsonGenerator.writeFieldName("headers");
		myJsonGenerator.writeStartObject();
		
		currentToken = myJsonParser.nextToken();	// advance to start symbol
		
		// now use a loop to advace, keep writing until encountered end-of object
		while (currentToken!=JsonToken.END_OBJECT) {
			// write current token to String
			myJsonGenerator.writeStringField(currentToken.toString(), myJsonParser.nextValue().toString());
			// write next value to String
			currentToken = myJsonParser.nextToken();		// advance to next value
			currentToken = myJsonParser.nextToken();		// advance to next value name or end object as the case may be
		}
		if (currentToken==JsonToken.END_OBJECT) 
			myJsonGenerator.writeEndObject();				// now we are done with writing the request block
		
	}


	private void writeURLandTimeStamp() throws FileNotFoundException, IOException {

		myJsonGenerator = myJsonFactory.createGenerator(new FileOutputStream(allFieldsFile));
		myJsonGenerator.useDefaultPrettyPrinter();
		myJsonGenerator.writeStartObject();
		myJsonGenerator.writeStringField("url", url);
		myJsonGenerator.writeStringField("timestamp", timestamp);
	}



	private void initializeBothFiles() throws IOException {
		// TODO Auto-generated method stub
		allFieldsFile = new File(outputPath + File.separator + "allFields" + File.separator + urlHost);
		allSourceFile = new File(outputPath + File.separator + "allSource" + File.separator + urlHost);
		
		if (!allFieldsFile.exists())
			allFieldsFile.mkdir();
		if (!allSourceFile.exists())
			allSourceFile.mkdir();
		
		allFieldsFile = new File(outputPath + File.separator + "allFields" + File.separator + urlHost + File.separator + url);
		allSourceFile = new File(outputPath + File.separator + "allSource" + File.separator + urlHost + File.separator + url);
		
		if (!allFieldsFile.exists())
			allFieldsFile.createNewFile();
		if (!allSourceFile.exists())
			allSourceFile.createNewFile();
	}



	private void getURLandTimeStamp() throws IOException {
		
		currentToken = myJsonParser.nextToken();
		
		JsonToken startObject = myJsonParser.nextToken(); 	// advance the start object
		if (startObject!=JsonToken.START_OBJECT)
		errorAndExit();
		
		currentToken = myJsonParser.nextToken();		 	// advance to first string (URL)	
		
			if (currentToken==JsonToken.FIELD_NAME && (currentToken.toString()).equals("url"))
			{
			url = myJsonParser.nextValue().toString();
			urlHost = (new URL(URLDecoder.decode(url))).getHost();
			}
			currentToken = myJsonParser.nextToken();		// advance to URL Value
			currentToken = myJsonParser.nextToken();		// advance to timestamp field_name
		
			if (currentToken==JsonToken.FIELD_NAME && (currentToken.toString()).equals("timestamp")) {
			timestamp = myJsonParser.nextValue().toString();	
			}
		
			currentToken = myJsonParser.nextToken();		// advance to timestamp value
			currentToken = myJsonParser.nextToken();		// advance to request value
	}
	
	
	private void errorAndExit(){
		System.out.println("Supplied CBOR File not in the proper CBOR Array Format ! Should be a CBOR Array of the name Objects.");
		System.exit(0);
	}

}
