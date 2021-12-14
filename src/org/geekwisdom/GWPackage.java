/* *************************************************************************************
' Script Name: GWPackage.java
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the GEEK WISDOM PACKAGE MANAGER. It's purpose is allow a quick
' @(#)    file/io repositoiry using the ZIP compression standard
' @(#)    It is used by other objects such as GWSecSharedKeyCrypt to store encrypted data.
' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2020-07-04 - Initial Architecture
' TODO: Improve Error Handling and allow adding of multiple files with appending!
' Add ability to add FROM FILE and not jsut FROM STRNIG
' **************************************************************************************/


package org.geekwisdom;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GWPackage
{
    private String PackagePath = "";
    public GWPackage(String FileName)
    {
        PackagePath = FileName;
    }

    public int AddFileFromString(String FileName, String FileData)
    {
        try
        {
   			 FileOutputStream fos = new FileOutputStream(PackagePath);
   		        ZipOutputStream zipOut = new ZipOutputStream(fos);
   		        File fileToZip = new File(FileName);
   		        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
   		        zipOut.putNextEntry(zipEntry);
   		        zipOut.write(FileData.getBytes("UTF-8"));
   		        zipOut.close();
   		        fos.close();  
        }
        catch (Exception e)
        {
            return 0;
        }
        return 1;
    }


    public String GetStringFromFile(String FileName)
    {
        try
        {
        	ZipFile zipfile = new ZipFile(PackagePath);
        	if (zipfile == null) return null;
        	ZipEntry entry = zipfile.getEntry(FileName);
        	if (entry == null) return null;
        	InputStream stream = zipfile.getInputStream(entry);
            InputStreamReader isReader = new InputStreamReader(stream);
             BufferedReader reader = new BufferedReader(isReader);
                     StringBuffer sb = new StringBuffer();
                     String str;
                     while((str = reader.readLine())!= null)
                     {
                        sb.append(str + "\n");
                     }
                     String strdata= sb.toString();
                    isReader.close();
                    stream.close();
                    zipfile.close();
                     return strdata;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}

