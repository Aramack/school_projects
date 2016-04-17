import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class ExtractorThread extends Thread {

	private URL url;
	private MyQueue<URL> linkQueue, picQueue;
	private MySet<URL> beenThere, doneThat;

	public ExtractorThread(URL url, MyQueue<URL> linkQueue, MyQueue<URL> picQueue, MySet<URL> beenThere, MySet<URL> doneThat) {
		this.url = url;
		this.linkQueue = linkQueue;
		this.picQueue = picQueue;
		this.beenThere = beenThere;
		this.doneThat = doneThat;
	}

	public String getCurrentURL() {
		return url.toString();
	}

	private static Pattern LINK_PATTERN = Pattern.compile("href *= *\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
	private static Pattern IMAGE_PATTERN = Pattern.compile("<( )*(img|IMG)( )+([^<>])*(src|SRC)( )*=( )*\"([^\"]+)\"[^>]*>");

	private static Set<URL> extractLinks(Pattern toMatch, String s, URL currentURL, int group) {
		Matcher m = toMatch.matcher(s);
		Set<URL> links = new HashSet<URL>();
		while ( m != null && s!= null && m.find()) {
			String found = m.group(group);
			try {
				links.add(new URL(currentURL, found));
			} catch (MalformedURLException e) {
				// just ignore
			}
		}
		return links;
	}

	private static Set<URL> getLinks(String s, URL currentURL) {
		return extractLinks(LINK_PATTERN, s, currentURL, 1);
	}

	private static Set<URL> getPicURLs(String s, URL currentURL) {
		return extractLinks(IMAGE_PATTERN, s, currentURL, 8);
	}

	public void run() {
		URLConnection connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Opens connection with URL

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}//Gets html code

		String inputLine;

		synchronized(linkQueue){
			try {
				while ((inputLine = reader.readLine()) != null){
					Set<URL> myLinks = getLinks(inputLine, url);
					for(URL thisURL: myLinks){
						if(thisURL == null){//Checks for URL == null
							continue;
						}
						//Get its protocol (a String).
						String protocol = thisURL.getProtocol();
						//If the protocol is "http" or "file"
						if(protocol.equals("http") || protocol.equals("file")){
							if(beenThere.contains(thisURL)){
								continue;
							}
							linkQueue.enqueue(thisURL);//add the url to the queue
							beenThere.add(thisURL);//We have now been to the link
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//Close the connection 
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Start Over
		//Reestablish the connection.

		connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader = null;
		//Connect to url

		try {
			reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Reads through the code. 
		synchronized(picQueue){
			try {
				while ((inputLine = reader.readLine()) != null){
					Set<URL> myPics = getPicURLs(inputLine, url);
					for(URL thisURL: myPics){
						if(thisURL == null){//Checks for URL == null
							continue;
						}

						//Check if this URL is already contained in the doneThat set.
						//If not, then add it to the doneThat set and also to the picQueue.
						if(doneThat.contains(thisURL)){
							continue;
						}
						picQueue.enqueue(thisURL);
						doneThat.add(thisURL);

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//Close the connection
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}