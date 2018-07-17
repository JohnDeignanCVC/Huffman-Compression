/* John Deignan CS610 1426 prp */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class hdec1426{	
	public static void main(String[] args){		
		if(args.length == 0){
			System.out.println("No file specified.");
			System.exit(0);
		} else{		
			try{		
				String file = args[0];	
				File del = new File(file);
				BinIn bi = new BinIn(file);				
				String result = file.substring(0, file.length()-4);
				
				BinHeapNode bhn = readTree(bi);
				int length = bi.readInt();				
				
				BinOut bo = new BinOut(result);	
				decode(bhn, bo, bi, length);				
				
				bo.close();	
				bi.close();
				del.delete();
			} catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
	
	public static BinHeapNode readTree(BinIn bi){
		boolean leaf = bi.readBoolean();
		if(leaf){
			return new BinHeapNode(bi.readChar(), -1);
		} else{
			return new BinHeapNode('\0', -1, readTree(bi), readTree(bi));
		}
	}
	
	public static void decode(BinHeapNode bhn, BinOut bo, BinIn bi, int length){
		for(int i = 0; i < length; i++){
			BinHeapNode x = bhn;
			
			while(!(x.left == null) && !(x.right == null)){
				boolean bit = bi.readBoolean();
				if(bit) x = x.right;
				else x = x.left;
			}
			
			bo.writeByte(x.data);
		}			
	}
	
	public static class BinHeapNode{
		int freq;
		int data;
		String code;
		BinHeapNode left;
		BinHeapNode right;
		
		BinHeapNode(int data, int freq){
			this.freq=freq;
			this.data=data;
			this.left = this.right = null;
			this.code = null;
		}
		
		BinHeapNode(int data, int freq, BinHeapNode left, BinHeapNode right){
			this.freq=freq;
			this.data=data;
			this.left = left;
			this.right = right;
			this.code = null;
		}
	}
	
	public static class BinOut{
	    private BufferedOutputStream out;

	    private int buffer; 
	    private int n;    
	    
	    public BinOut(String filename){
	        try{
	            out = new BufferedOutputStream(new FileOutputStream(filename));
	        } catch (Exception e){
	        	e.printStackTrace();
	        }
	    }	    

	    private void clearBuffer(){
	        if (n == 0) 
	        	return;
	        
	        if (n > 0) 
	        	buffer <<= (8 - n);
	        
	        try{
	        	out.write(buffer);
	        } catch(Exception e){
	        	e.printStackTrace();
	        }
	        
	        n = 0;
	        buffer = 0;
	    }

	    private void flush(){
	        clearBuffer();
	        
	        try{
	        	out.flush();
	        } catch (Exception e){
	        	e.printStackTrace();
	        }
	    }

	    public void close(){
	        flush();
	        
	        try {
	        	out.close();
	        } catch(Exception e){
	        	e.printStackTrace();
	        }
	    }
	    
	    private void writeBit(boolean bit){
	        buffer <<= 1;
	        
	        if (bit) 
	        	buffer |= 1;
	        
	        n++;
	        
	        if (n == 8) 
	        	clearBuffer();
	    } 
	    
	    private void writeByte(int x){	        
	        if(n == 0){
	            try{
	            	out.write(x);
	            } catch(Exception e){
	            	e.printStackTrace();
	            }
	            return;
	        }
	        
	        for(int i = 0; i < 8; i++){
	            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
	            writeBit(bit);
	        }
	    }
	    
	    public void write(char x){	        
	        writeByte(x);
	    }
	}
	
	public static class BinIn{
	    private final int end = -1;

	    private BufferedInputStream in;
	    private int buffer;
	    private int n;
	    
	    public BinIn(String name){
	        try{
	            File file = new File(name);
	            
	            if(file.exists()){
	                FileInputStream fis = new FileInputStream(file);
	                in = new BufferedInputStream(fis);
	                fillBuffer();
	                return;
	            }
	        } catch(Exception e){
	        	e.printStackTrace();
	        }
	    }
	    
	    public void close() {
	        try {
	            in.close();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private void fillBuffer(){
	        try {
	            buffer = in.read();
	            n = 8;
	        } catch(Exception e){
	        	e.printStackTrace();
	            buffer = end;
	            n = -1;
	        }
	    }

	    public boolean exists(){
	    	return in != null;
	    }

	    public boolean readBoolean(){
	        n--;
	        boolean bit = ((buffer >> n) & 1) == 1;
	        
	        if (n == 0) 
	        	fillBuffer();
	        
	        return bit;
	    }

	    public char readChar(){	        
	        if(n == 8){
	            int x = buffer;
	            fillBuffer();
	            return (char)(x & 0xff);
	        }
	        
	        int x = buffer;
	        x <<= (8 - n);
	        int oldN = n;
	        
	        fillBuffer();
	        
	        n = oldN;
	        x |= (buffer >>> n);
	        
	        return (char) (x & 0xff);
	    }

	    public int readInt(){
	        int x = 0;
	        
	        for (int i = 0; i < 4; i++){
	            char c = readChar();
	            x <<= 8;
	            x |= c;
	        }
	        
	        return x;
	    }
	}
}