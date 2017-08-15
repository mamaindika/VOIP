/*
 * CO 324 - Network and Web programming
 * Project I
 * Skeleton Code
 */

import java.net.* ;
import java.io.*;
import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class RecordPlayback1 {

boolean stopCapture = false;
ByteArrayOutputStream byteArrayOutputStream;
AudioFormat audioFormat;
TargetDataLine targetDataLine;
AudioInputStream audioInputStream;
SourceDataLine sourceDataLine;
byte tempBuffer[] = new byte[500];
String mes1,mes;
Thread serverThread;
Thread captureThread;
PlayThread playThread;
DatagramSocket socket1;
InetAddress add;
public static  String IpAddress ;
public static int sendingPort;
public static int receivingPort;
MulticastSocket socket=null;
public static  String Address ;
public static int sendPort;
public static int receivePort;
public static String Token;




public static void main(String[] args) {


   if( args.length != 0 ){
         System.out.println( "usage: java DatagramClient host sendingPort receivingPort" ) ;
         return ;
   }

   else{
  
        RecordPlayback1 u = new RecordPlayback1();
   }
}

public RecordPlayback1(){

    
  
  while (true) {
        
        

        System.out.println("Enter 1 to SEND");
        System.out.println("Enter 2 to RECEIVE");
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();
        captureAudio();
	
	if(choice==1){
	 
		 System.out.println("Enter IpAddress");
		 Address = scan.next();
		 System.out.println("Sending Port");
		 sendPort = scan.nextInt();
		 System.out.println("Enter  tokenIpAddress");
		 Token = scan.next();
		 new SendRequest(Address,sendPort,Token);
		 captureThread = new CaptureThread(Address,sendPort); 		 // call serverThread to send audio

		 captureThread.start();
		
	}
	else if(choice==2){
	
			
		 System.out.println("Enter Multicast IpAddress");
		 IpAddress = scan.next();
		 System.out.println("Enter Receiving Port");
		 receivingPort = scan.nextInt();
		 new StartServer(IpAddress,receivingPort);
		
	}
	else{}
	
  }
  
  
 }


  
//....................................
	public  class StartServer implements Runnable{

		public StartServer(String IpAddress,int receivingPort){
		    serverThread = new Thread(this);
		    serverThread.start();
		}
		
	        int count;
	        Reading1 r;
	        
		public void run(){
		
		// Server side that receive the message

			try {  
				 //Prepare to join multicast group
	 			socket = new  MulticastSocket(6789);
		                add = InetAddress.getByName("224.2.2.0");
			        socket.joinGroup(add);
				System.out.println( "The server is ready..." ) ;
				   
				while(true){
				
				
					try{
					    //Receive request from client
					    byte[] buffer = new byte[1000];
					    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, add,6789);
					   
					    socket.receive(packet);
					    
					    String addressclint= packet.getAddress().toString();
					    
					    String msg = new String(buffer, 0,packet.getLength());//get the token message
					    System.out.println(msg);
					    // call serverThread to receive audio
					    			
		                            playThread = new PlayThread(IpAddress,receivingPort,msg);
		                            playThread.start();
					    count++;  
					 
					}
					catch(UnknownHostException ue){System.out.println("UnknownHostException!"+ue);}
				 
				}
				 
			}
			catch(IOException e){System.out.println("IOException1!"+e);}
		}
	}

//....................................


//------------------------------------
	public class SendRequest{ 
		SendRequest(String Address,int sendPort,String token){
		
		// client side that send the request packets
		int i;
		try{
		
		    add = InetAddress.getByName("224.2.2.0");
		    DatagramSocket socket1 = new DatagramSocket();
		    byte[] buffer = new byte[65535];
		    String mess =token;
		    buffer = mess.getBytes(); 
		    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, add,6789);
		    socket1.send(packet);
		    socket1.close();
		  
		}
		catch(IOException io){System.out.println("IOException2!"+io);}
		}
	}
//------------------------------------

	private void captureAudio() {
	    
	    try {
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
		System.out.println("Available mixers:");
		Mixer mixer = null;
		for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
		    System.out.println(cnt + " " + mixerInfo[cnt].getName());
		    mixer = AudioSystem.getMixer(mixerInfo[cnt]);
		    Line.Info[] lineInfos = mixer.getTargetLineInfo();
		    
		    if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
		        System.out.println(cnt + " Mic is supported!");
		        break;
		    }
		}

		audioFormat = getAudioFormat();     //get the audio format
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

		targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
		targetDataLine.open(audioFormat);
		targetDataLine.start();
		//send audio to speaker
		DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
		sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
		sourceDataLine.open(audioFormat);
		sourceDataLine.start();
		
		//Setting the maximum volume
		FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(control.getMaximum());
		
	 
	    } catch (LineUnavailableException e) {
		System.out.println("LineUnavailableException!"+e);
		System.exit(0);
	    }
	  
	}


//-------------------------------------

    // sending audio to server(not our)
	class CaptureThread extends Thread {
      
	int count=0;
	    
        CaptureThread(String Address,int sendPort){}
        
        byte tempBuffer[] = new byte[1024];
        
		  public void run() {
		      
			try {
			        DatagramSocket client_socket = new  DatagramSocket();

			        InetAddress IPAddress =InetAddress.getByName(Address);
			     
			        // scheduler to monitor statistics every minute
				final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
				service.scheduleWithFixedDelay(new Runnable() {
				        @Override
				        public void run() {
				            //if you wont to see number of packets per 10 seconds remove the comment
				           // System.out.println("total send " + count );
				            count = 0;                            
				        }
				}, 5, 5, TimeUnit.SECONDS);
			    
				while (true) {
				      
				    int cnt =targetDataLine.read(tempBuffer, 0,tempBuffer.length);//put audio to tempBuffer
				    
				    byte sendBuffer[] = new byte[2100];
				   
				    Reading1 p = new Reading1(tempBuffer,count);//creating a object to serialize
				   
      				    byte[] array= Reading1.Serialize(p);//serialize the object
	 				
	                            sendBuffer = Arrays.copyOf(array,array.length);
									   
				    DatagramPacket send_packet = new DatagramPacket(sendBuffer, sendBuffer.length,IPAddress,sendPort);
				 
				    client_socket.send(send_packet);
				   
				    count++;
				}
				
				
			} catch (Exception e) {
			     System.out.println("Exception1!"+e);
			     System.exit(0);
			}
		}
	}
	
//-------------------------------------



// coding and format audio
private AudioFormat getAudioFormat() {
    float sampleRate = 16000.0F;
    int sampleSizeInBits = 16;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = true;
    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
}


//************************************
	// recieving audio from ourserver and play it
	class PlayThread extends Thread {
	
	      String token ="receive";
	      PlayThread (String IpAddress,int receivingPort,String token){
		      this.token=token;
	      }
	
	      byte tempBuffer[] = new byte[2100];
	      int count;
	      
		  public void run() {
	                  MulticastSocket server_socket;
			   try {
				server_socket= new MulticastSocket(receivingPort);
				 add = InetAddress.getByName(IpAddress);
			         server_socket.joinGroup(add);
			   }catch (IOException e) {
				e.printStackTrace();
				return ;
			   }
		           System.out.println("token is "+token);
		           if(token.equals("send")){
		           
		           
				   final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
				   service.scheduleWithFixedDelay(new Runnable() {
				        @Override
				        public void run() {
				            System.out.println("total received: " + count );
				            count = 0;                            
				        }
				   }, 5, 5, TimeUnit.SECONDS);
		           
				   while (true){
					 try{
					    DatagramPacket receive_packet = new DatagramPacket(tempBuffer,tempBuffer.length);
					    
					    server_socket.receive(receive_packet);
					   
					    byte array[]=receive_packet.getData();
					    
					    Reading1 f =DeSerialize.Deserialize(array);
					    
					    byte array1[]=Arrays.copyOf(f.tempBuffer,f.tempBuffer.length);
					    
					    count = f.getCount();
					    
					    sourceDataLine.write(array1,0,array1.length);
					  
					 }catch(Exception ee){
					    System.out.println("Exception2!"+ee);
					 }
				  }
			  }
			  else{
			     System.out.println("Can not play audio !");
			  }
		}
		 
	  }	

//************************************

}

