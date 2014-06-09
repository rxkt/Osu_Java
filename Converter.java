import java.io.*;
import java.util.*;

public class Converter{
    public static void main(String[] args){
	if (args.length!=1){
	    System.out.println("give me a file");
	    return;
	}
	try{
	    File file= new File(args[0]);
	    int fileLength = file.getName().length();
	    String directory = file.getPath().substring(0,file.getPath().length()-fileLength);
	    File newFile = new File(directory + file.getName().substring(0,file.getName().length()-4)+".desu");
	    Scanner scanner = new Scanner(file);
	    BufferedWriter output = new BufferedWriter(new FileWriter(newFile));
	    String s = "";
	    final String findStr= ":";
	    while (!s.contains("Objects")){
	        s=scanner.nextLine();
	    }
	    s=scanner.nextLine();
	    //write music, video
	    File dir = new File(directory);
	    for (File f:dir.listFiles()){
		if (f.getName().contains(".mp3")){
		    output.write("song\n");
		    output.write(f.getName());
		}
	    }
	    for (File f:dir.listFiles()){
		if (f.getName().contains(".avi")){
		    output.write("video\n");
		    output.write(f.getName());
		}
	    }
	    output.write("\n");
	    while (scanner.hasNext()){

		
		//write copied stuff
		s=s.replaceAll(","," ");
		s=s.replaceAll("\\|"," ");
		//count :'s
		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){
		    
		    lastIndex = s.indexOf(findStr,lastIndex);

		    if( lastIndex != -1){
			count ++;
			lastIndex+=findStr.length();
		    }
		}
	        if (count ==0){
		    output.write("C\n"+s+"\n");
		}else{
		    s=s.replaceAll(":"," ");
		    output.write("S\n"+s+"\n"+(count-1)+"\n");
		    //format of
		    //S
		    //x y time (omit) (omit) char x1 y1 x2 y2....
		    //bezier control points
		    //C
		    //....
		}
		s=scanner.nextLine();
	    }
	    output.close();
	    System.out.println("done.");
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
}
		   
