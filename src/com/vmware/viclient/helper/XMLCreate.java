package com.vmware.viclient.helper;

import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


import org.dom4j.DocumentHelper;
import org.dom4j.Element;



public class XMLCreate {
	
	private XMLWriter writer = null;
	public XMLCreate(String xmlFileName){
		
		 try {
			writer = new XMLWriter(
			            new FileWriter(xmlFileName )
			        );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public Document createDocument() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );

        Element author1 = root.addElement( "author" )
            .addAttribute( "name", "James" )
            .addAttribute( "location", "UK" )
            .addText( "James Strachan" );
        
        Element author2 = root.addElement( "author" )
            .addAttribute( "name", "Bob" )
            .addAttribute( "location", "US" )
            .addText( "Bob McWhirter" );

        return document;
    }
    
    public void write(Document document) throws IOException {

        // lets write to a file
        XMLWriter writer = new XMLWriter(
            new FileWriter( "output.xml" )
        );
        writer.write( document );
        writer.close();


        // Pretty print the document to System.out
        OutputFormat format = OutputFormat.createPrettyPrint();
        writer = new XMLWriter( System.out, format );
        writer.write( document );

        // Compact format to System.out
        format = OutputFormat.createCompactFormat();
        writer = new XMLWriter( System.out, format );
        writer.write( document );
    }
    
   
}




