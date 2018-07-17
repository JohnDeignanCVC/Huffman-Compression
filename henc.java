/* John Deignan CS610 1426 prp */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class henc1426 {
	public static BinHeapNode[] huffCodes = new BinHeapNode[256];
	public static String[] huff = new String[256];
	public static int index = 0;
	
	public static void main(String[] args){		
		if(args.length == 0){
			System.out.println("No file specified.");
			System.exit(0);
		} else{		
			try{		
				String file = args[0];
				File del = new File(file);
				byte[] buf = read(file);
				del.delete();
				if(!file.substring(file.length()-3).equals("txt")){
					int[] ibuf = new int[buf.length];
					
					for(int i = 0; i < buf.length; i++){
						int temp = buf[i];
						ibuf[i] = temp & 0xff;
					}	
					
					BinHeap bh = createFreqHeap(ibuf);				
					bh.minHeap();	
					BinHeapNode bhn = getHuffTree(bh);
					getHuffCode(bhn, ""); 
					
					BinOut bo = new BinOut(file+".huf");
					writeTreeTo(bhn, bo);
					bo.write(ibuf.length);
					writeTo(bo, ibuf);
					bo.close();
				} else{				
					BinHeap bh = createFreqHeap(buf);				
					bh.minHeap();	
					BinHeapNode bhn = getHuffTree(bh);
					getHuffCode(bhn, "");
					
					BinOut bo = new BinOut(file+".huf");
					writeTreeTo(bhn, bo);
					bo.write(buf.length);
					writeTo(bo, buf);	
					bo.close();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
	
	public static byte[] read(String file){
		try{
			FileInputStream in = new FileInputStream(file);
			byte[] buf = new byte[(int) new File(file).length()];
			
			in.read(buf);
			in.close();
			return buf;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeTo(BinOut bo, byte[] buf){
		try{			
			String code = "";
			
			for(int x = 0; x < buf.length; x++){
				code = huff[buf[x]];
				for(int i = 0; i < code.length(); i++){
					if(code.charAt(i) == '0'){
						bo.write(false);
					} else if(code.charAt(i) == '1'){
						bo.write(true);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static void writeTo(BinOut bo, int[] ibuf){
		try{			
			String code = "";
			
			for(int x = 0; x < ibuf.length; x++){
				code = huff[ibuf[x]];
				for(int i = 0; i < code.length(); i++){
					if(code.charAt(i) == '0')
						bo.write(false);
					else if(code.charAt(i) == '1')
						bo.write(true);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static void writeTreeTo(BinHeapNode bhn, BinOut bo){
		if(bhn.left == null && bhn.right == null){
			bo.write(true);
			bo.write(bhn.data, 8);
			return;
		}
		bo.write(false);
		
		writeTreeTo(bhn.left, bo);
		writeTreeTo(bhn.right, bo);
	}
	
	public static BinHeap createFreqHeap(byte[] buf){
		BinHeapNode bhn = new BinHeapNode(Integer.MIN_VALUE, Integer.MIN_VALUE);
		BinHeap bh = new BinHeap(256, bhn);
		
		BinHeapNode init = new BinHeapNode(buf[0], 1);
		bh.insert(init);
		
		for(int i = 1; i < buf.length; i++){
			bh.containsThenInc(buf[i]);
		}
		
		return bh;
	}
	
	public static BinHeap createFreqHeap(int[] ibuf){
		BinHeapNode bhn = new BinHeapNode(Integer.MIN_VALUE, Integer.MIN_VALUE);
		BinHeap bh = new BinHeap(256, bhn);	
		
		BinHeapNode init = new BinHeapNode(ibuf[0], 1);
		bh.insert(init);
		
		for(int i = 1; i < ibuf.length; i++){
			bh.containsThenInc(ibuf[i]);
		}
		
		return bh;
	}
	
	public static BinHeapNode getHuffTree(BinHeap heap){
		BinHeap bh = heap;
		
		while(bh.getSize() > 1){
			BinHeapNode bhn1 = heap.removeMin();
			BinHeapNode bhn2 = heap.removeMin();
			
			BinHeapNode newNode = new BinHeapNode(555, (bhn1.freq + bhn2.freq));
			
			newNode.left = bhn1;
			newNode.right = bhn2;
			
			bh.insert(newNode);			
		}
		
		return bh.extractRoot();
	}
	
	public static void getHuffCode(BinHeapNode bhn, String s){
		String left = s;
		String right = s;
		
		if(bhn.left != null){
			left += "0";
			getHuffCode(bhn.left, left);
		}
		
		if(bhn.right != null){
			right += "1";
			getHuffCode(bhn.right, right);
		}
		
		if(bhn.left == null && bhn.right == null){
			bhn.code = s;
			huff[bhn.data] = s;
			index++;
		}
	}
	
	public static class BinHeap{
		private BinHeapNode[] heap;
		private int size;
		private int max;
		private int most = 0;
		private int mostIndex = 0;
		private final int front = 1;
		
		public BinHeap(int m, BinHeapNode i){
			this.max = m;
			this.size = 0;
			heap = new BinHeapNode[this.max+1];
			heap[0]=i;
		}
		
		private int parent(int i){
			return (i/2);
		}
		
		private int left(int i){
			return (2*i);
		}
		
		private int right(int i){
			return (2*i)+1;
		}
		
		public int getSize(){
			return size;
		}
		
		private boolean isLeaf(int i){
			if(i == front){
				return false;
			}
			
			if(i >= (size/2) && i <= size){
				return true;
			}
			
			return false;
		}
		
		private boolean rightExists(int i){
			if((i*2)+1 > size){
				return false;
			}
			
			return true;
		}
		
		public void containsThenInc(int data){	
			if(heap[mostIndex].data == data){
				heap[mostIndex].freq++;
				most++;
				return;
			}	
			
			boolean match = false;
			for(int i = 1; i < size+1; i++){
				if(heap[i].data == data){
					match = true;
					heap[i].freq++;
					
					if(heap[i].freq > most){
						mostIndex = i;
						most = heap[i].freq;
					}
				}
			}	
			
			if(!match){
				int s = ++size;
				BinHeapNode e = new BinHeapNode(data, 1);
				heap[s] = e;
				
				while(heap[s].freq < heap[parent(s)].freq){
					swap(s, parent(s));
					s = parent(s);
				}
			}
		}
		
		private void swap(int i1, int i2){
			BinHeapNode temp;
			temp = heap[i1];
			heap[i1] = heap[i2];
			heap[i2] = temp; 
		}
		
		private void heapify(int i){
			if(!isLeaf(i)){
				if(rightExists(i)){
					if(heap[i].freq > heap[left(i)].freq || heap[i].freq > heap[right(i)].freq){
						if(heap[left(i)].freq < heap[right(i)].freq){
							swap(i, left(i));
							heapify(left(i));
						} else{
							swap(i, right(i));
							heapify(right(i));
						}
					}
				} else{
					if(heap[i].freq > heap[left(i)].freq && size != 1){
						swap(i, left(i));
						heapify(left(i));
					}
				}
			}
		}
		
		public void insert(BinHeapNode bhn){
			heap[++size] = bhn;
			int curr = size;	
			
			while(heap[curr].freq < heap[parent(curr)].freq){
				swap(curr, parent(curr));
				curr = parent(curr);
			}
		}
		
		public void minHeap(){
			for(int i = (size/2); i >= 1; i--)
				heapify(i);
		}
		
		public BinHeapNode removeMin(){
			BinHeapNode p = heap[front];
			heap[front] = heap[size--];
			heapify(front);
			return p;
		}
		
		public BinHeapNode extractRoot(){
			return heap[1];
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
	    private int b;    
	    
	    public BinOut(String filename){
	        try {
	            out = new BufferedOutputStream(new FileOutputStream(filename));
	        } catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	    
	    private void clearBuffer(){
	        if (b == 0) 
	        	return;
	        
	        if (b > 0) 
	        	buffer <<= (8 - b);
	        
	        try{
	            out.write(buffer);
	        } catch(Exception e){
	            e.printStackTrace();
	        }
	        
	        b = 0;
	        buffer = 0;
	    }

	    private void flush(){
	        clearBuffer();
	        
	        try{
	            out.flush();
	        } catch(Exception e){
	            e.printStackTrace();
	        }
	    }

	    public void close(){
	        flush();
	        try{
	            out.close();
	        } catch(Exception e){
	            e.printStackTrace();
	        }
	    }
	    
	    private void writeBit(boolean bool){
	        buffer <<= 1;
	        if(bool) 
	        	buffer |= 1;

	        b++;
	        if(b == 8) 
	        	clearBuffer();
	    }
	    
	    private void writeByte(int x){
	        if(b == 0){
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

	    public void write(boolean bool){
	        writeBit(bool);
	    } 
	    
	    public void write(int i){
	        writeByte((i >>> 24) & 0xff);
	        writeByte((i >>> 16) & 0xff);
	        writeByte((i >>>  8) & 0xff);
	        writeByte((i >>>  0) & 0xff);
	    }
	    
	    public void write(int x, int r){
	        if(r == 32){
	            write(x);
	            return;
	        }
	        for(int i = 0; i < r; i++) {
	            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
	            writeBit(bit);
	        }
	    }
	}
}