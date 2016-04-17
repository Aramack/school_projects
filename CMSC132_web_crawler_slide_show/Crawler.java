import java.net.*;
import java.io.*;

public class Crawler {
	
	public static void main(String[] args) {
		
		MyQueue<URL> linkQueue = new MyQueue<URL>();
		MyQueue<URL> picQueue = new MyQueue<URL>();
		MySet<URL> beenThere = new MySet<URL>();
		MySet<URL> doneThat = new MySet<URL>();
		
		final int MAX_NUM_EXTRACTORS = 5;  // Change this to whatever you want
		
		ExtractorThread[] extractors = new ExtractorThread[MAX_NUM_EXTRACTORS];
		
		new SlideShowGUI(picQueue);
		new CrawlerGUI(linkQueue, picQueue, beenThere, doneThat, extractors);
		
		URL url;
		
		while(true) {
			for(int itt =0; itt < MAX_NUM_EXTRACTORS; itt++){
				ExtractorThread thisThread = extractors[itt];
				if(thisThread!=null && thisThread.isAlive()){
					continue;
				}
				//Take a URL out of the linkQueue
				
				url= linkQueue.dequeue();
				//Acquire a lock on the extractors array. 
				
				synchronized(extractors){
					URLConnection connection = null;
					try {
						connection = url.openConnection();
					} catch (IOException e) {
						e.printStackTrace();
					}//connect to the url
					String thisConnection = connection.getContentType();
					//Gets the connection type
					while((thisConnection == null) || (!thisConnection.substring(0, 4).equals("text")
							&& !thisConnection.substring(0, 4).equals("html"))){
						url = linkQueue.dequeue();//dequeue url
						connection = null;//connect to url
						try {
							connection = url.openConnection();
						} catch (IOException e) {
							e.printStackTrace();
						}
						thisConnection = connection.getContentType();
					}
					//instantiate an ExtractorThread for it, assign this thread to the dead/unused entry in the array
					thisThread = new ExtractorThread(url,linkQueue, picQueue, beenThere, doneThat);
					extractors[itt] = thisThread;
					//Start the thread
					thisThread.start();
				}
				
				
				
			
			}
			try {
				Thread.sleep(400);//Changed from 500 to 400
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}