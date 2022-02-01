import com.sterlingcommerce.woodstock.workflow.Document; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.BufferedReader; 
import java.io.FileOutputStream; 
import com.sterlingcommerce.woodstock.util.Base64; 
import java.io.IOException;
import java.io.File; 
String line = null; 
String fileExtension = null; 
InputStream is = null;
BufferedReader br = null;
try{  
   String workFlowId = (String)wfc.getWFContent("workFlowId"); 
   String  mountPointAntivirus =  (String)wfc.getWFContent("mountPointAntivirus");  
   Document document = new Document();      
   Document srcDoc = wfc.getPrimaryDocument(); 
   is = srcDoc.getInputStream(); 
   br = new BufferedReader(new InputStreamReader(is)); 
   StringBuffer sb = new StringBuffer(); 
   int fileIndex = 0; 
   String id =""; 
   StringBuffer processData = new StringBuffer();  
   processData.append("<FIES>"+ System.getProperty("line.separator"));   
   while((line = br.readLine()) != null){    
      sb = sb.append(line + System.getProperty("line.separator"));    
      if(line.contains("binario")) {       
         line = line.trim();       
         String[] split = line.split(":");       
         line = split[1];       
         line = line.replaceAll("\"", "");       
         line = line.replaceAll(",", "");        
         line = line.trim();         
         byte data[] = line.toString().getBytes();       
         byte newData[]  = Base64.decode(data);       
         FileOutputStream fos = new FileOutputStream(new File(mountPointAntivirus+workFlowId+"FIES_"+fileIndex));        
         fos.write(newData);        
         fos.close();       
	     processData.append("<FILENAME>"+workFlowId+"FIES_"+fileIndex+"</FILENAME>"+ System.getProperty("line.separator"));        
         fileIndex+=1;    
      }      
     if(line.contains("dossie_cliente")){       
        id = line.trim();       
        String[] split = id.split(":");       
        id = split[1];       
        id = id.replaceAll(",", "");    
     } 
  } 
  processData.append("<ID>"+id.trim() +"</ID>"+ System.getProperty("line.separator"));  
  processData.append("</FIES>"+ System.getProperty("line.separator"));    
  document.setBody(processData.toString().getBytes());  
  wfc.setAdvancedStatus("Finish!");  
  wfc.setBasicStatus(000);  
  wfc.putPrimaryDocument(document);  
}catch(NullPointerException ex){
 log.log("SIFES_FIES-JSON INVALIDO " + ex.getMessage());    
 wfc.addWFContent("INVALID_JSON","JSON_INVALIDO"); 
}catch(IOException e){
 log.log("SIFES_FIES-FALHA IO EXCEPTION " + e.getMessage());    
 wfc.addWFContent("IO_FAIL",e.getMessage()); 
}finally{ 
  br.close(); 
}
return "000";