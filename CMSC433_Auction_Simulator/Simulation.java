package cmsc433.p1;

import java.util.List;


/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation{
	public static void main(String[] args){
		AuctionServer testServer = AuctionServer.getInstance();
		testServer.submitItem("seller1", "seller1#1", 100, 1000);
		List<Item> itemList = testServer.getItems();
		System.out.println("Items left over: " +itemList.size());
		System.out.println("Total Items Sold: "+testServer.soldItemsCount());
		System.out.println("Total Revenue: $" +testServer.revenue());	
	}
	private static void function1(){
		
	}
	private static void function2(){
		
	}
	/*    public static void main(String[] args){
    	Thread sellerThreads = new Thread();
    	Thread bidderThreads = new Thread();
    	Seller seller = new Seller(AuctionServer.getInstance(), 
        		"Seller1", 
        		100, 50, 144);
    	Bidder bidder = new Bidder(AuctionServer.getInstance(), 
        		"Buyer1", 
        		1000, 20, 150, 69);
    	sellerThreads = new Thread(seller);
    	sellerThreads.start();
    	bidderThreads = new Thread(bidder);
    	bidderThreads.start();
    	try
        {
            sellerThreads.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }try
        {
            bidderThreads.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Finished");

    }
	 
	public static void main(String[] args){

		int nrSellers = 10;
		int nrBidders = 10;

		Thread[] sellerThreads = new Thread[nrSellers];
		Thread[] bidderThreads = new Thread[nrBidders];
		Seller[] sellers = new Seller[nrSellers];
		Bidder[] bidders = new Bidder[nrBidders];

		// Start the sellers
		for (int i=0; i<nrSellers; ++i)
		{
			sellers[i] = new Seller(
					AuctionServer.getInstance(), 
					"Seller"+i, 
					100, 50, i
			);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i=0; i<nrBidders; ++i)
		{
			bidders[i] = new Bidder(
					AuctionServer.getInstance(), 
					"Buyer"+i, 
					1000, 20, 150, i
			);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i=0; i<nrSellers; ++i)
		{
			try
			{
				sellerThreads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i=0; i<nrBidders; ++i){
			try{

				bidderThreads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// TODO: Add code as needed to debug
		AuctionServer testServer = AuctionServer.getInstance();
		List<Item> itemList = testServer.getItems();        
		System.out.println("Items left over: " +itemList.size());
		System.out.println("Total Items Sold: "+testServer.soldItemsCount());
		System.out.println("Total Revenue: $" +testServer.revenue());
	}
	*/
}
