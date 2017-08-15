import java.io.Serializable;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.sound.sampled.SourceDataLine;
//SourceDataLine sourceDataLine;





    public class Reading1 implements Serializable {
    
     public final byte []tempBuffer;
     public final int count;
		
	public Reading1(byte[] sin,int count) {
		 this.count=count;
		 byte[] arr2=Arrays.copyOf(sin, sin.length);		
		 this.tempBuffer =arr2 ;
	}
        
	public int getCount(){
	   return count;
	}	
	//======================
	public static byte[] Serialize(Reading1 r) {
        try {
            byte[] obj;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(2048)) {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(r);
                oos.close();
				obj = baos.toByteArray();		// get the byte array of the object
            }
            return obj;
        } catch(Exception e) {
            System.out.println("Can't serialize the voice packet.");
        }

        return null;
    }
	//======================
		
       public static void main(String[] args) {
       String str="vbfdvfhjjbj";
        
      Reading1 p= new Reading1(str.getBytes(),10);
      Reading1.Serialize(p);
    }
    }
//_________________________________________________________
