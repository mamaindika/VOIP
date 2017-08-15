import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class DeSerialize {  
    public static Reading1 Deserialize(byte[] tempBuffer) {
        Reading1 r;
        
        try {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(tempBuffer);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
               r = (Reading1) ois.readObject();
             
                return r;
            }
        } catch(IOException | ClassNotFoundException ex) {
            System.out.println("Cann't deserialize the voice packet."+ex);
        }
        
        return null;
    }
}
