package cmsc433.p1;

/**
 *  @author Mark Penny
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();
	private Object MasterLock = new Object();
	//private Object BidderLock = new Object();

	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 


	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  //System.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs){
		//System.out.println(sellerName + " wants to put " +itemName + " up for bid for $"+ lowestBiddingPrice +" for " + biddingDurationMs +"ms");
		synchronized(MasterLock){
			if(itemsUpForBidding.size() >= serverCapacity){
				//System.out.println(sellerName + " cannot put " +itemName + " up for bid because of server capacity");
				return -1;
			}

			synchronized(itemsPerSeller){
				if(itemsPerSeller.containsKey(sellerName)){
					if (itemsPerSeller.get(sellerName) < maxSellerItems){
						itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName)+1);
					}else{
						//System.out.println(sellerName + " cannot put " +itemName + " up for bid because they are already selling too much");
						return -1;
					}
				}else if(!itemsPerSeller.containsKey(sellerName)){
					itemsPerSeller.put(sellerName, 1);
				}else{
					//System.out.println(sellerName + " cannot put " +itemName + " up for bid because they are already selling too much");
					return -1;
				}
			}
			lastListingID++;
			Item newItem = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
			synchronized(highestBids){
				highestBids.put(lastListingID, lowestBiddingPrice);
			}
			synchronized(itemsAndIDs){
				itemsAndIDs.put(lastListingID, newItem);
			}
			synchronized(itemsUpForBidding){
				itemsUpForBidding.add(newItem);
			}
			//System.out.println(sellerName + " has put " +itemName + " up for bid with listing ID: " + lastListingID + " with a starting bid of: $" +newItem.lowestBiddingPrice());
			return lastListingID;
		}
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public List<Item> getItems(){
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		//    Returns a copy of the itemsUpForBidding list;
		synchronized(itemsUpForBidding){
			ArrayList<Item> returnValue = new ArrayList<Item>(itemsUpForBidding);
			return returnValue;
		}
	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount){
		//System.out.println(bidderName + " wants to put a bid on listing ID: " +listingID + " for $"+ biddingAmount);
		synchronized(MasterLock){
			synchronized(itemsAndIDs){
				if(!itemsAndIDs.containsKey(listingID)){
					//System.out.println(bidderName + " can't put a bid on ID: " +listingID + " because the ID is invalid exist");
					return false; //Check if item exists
				}else if(!itemsAndIDs.get(listingID).biddingOpen()){
					//System.out.println(bidderName + " can't put a bid on ID: " +listingID + " because the bidding is closed");
					return false; //Checks if bidding open
				}
			}
			synchronized(itemsPerBuyer){
				if(!itemsPerBuyer.containsKey(bidderName)){
					itemsPerBuyer.put(bidderName, 0);// Creates record of the bidder.
				}else if(itemsPerBuyer.get(bidderName)>=maxBidCount){
					//System.out.println(bidderName + " can't put a bid on " +listingID + " because it has bid on too many items");
					return false; //Already has too many bids
				}
			}
			boolean noBidders = true;
			synchronized(highestBidders){
				if(!highestBidders.containsKey(listingID)){
					noBidders = false;
				}else if(highestBidders.get(listingID) ==bidderName){
					//System.out.println(bidderName + " can't put a bid on " +listingID + " because they already have the highest bid");
					return false;
				}
			}
			synchronized(highestBids){					
				if(noBidders && highestBids.get(listingID) > biddingAmount){
					//System.out.println(bidderName + " can't put a bid on " +listingID + " because it is less than the highest bid");
					return false; 
				}else if(!noBidders && highestBids.get(listingID) >= biddingAmount){
					//System.out.println(bidderName + " can't put a bid on " +listingID + " because it is below the starting bid");
					return false;
				}
			}
			synchronized(itemsPerBuyer){
				//Change record of how many bids each person has.
				itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)+1);
				if(highestBidders.containsKey(listingID)){
					itemsPerBuyer.put(highestBidders.get(listingID), itemsPerBuyer.get(highestBidders.get(listingID))-1);
				}	
			}
			synchronized(highestBids){
				highestBids.put(listingID, biddingAmount);
			}
			synchronized(highestBidders){
				highestBidders.put(listingID, bidderName);
			}
			// Some reminders:
			//   See if the item exists.
			//   See if it can be bid upon.
			//   See if this bidder has too many items in their bidding list.
			//   Get current bidding info.
			//   See if they already hold the highest bid.
			//   See if the new bid isn't better than the existing/opening bid floor.
			//   Decrement the former winning bidder's count
			//   Put your bid in place
			//System.out.println(bidderName + " has put a bid on ID:" +listingID + " for $"+ biddingAmount);

			return true;
		}
	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public int checkBidStatus(String bidderName, int listingID){
		synchronized(MasterLock){
			int returnValue = 2;
			synchronized(itemsAndIDs){
				if(!itemsAndIDs.containsKey(listingID)){
					//System.out.println("ID: " +listingID +" is not valid");
					return 3; //Item does not exist
				}
				if(itemsAndIDs.get(listingID).biddingOpen()){
					return 2; //Bidding is still open
				}
			}
			synchronized(highestBidders){
				if(highestBidders.get(listingID) == bidderName){
					returnValue = 1;
				}else{
					returnValue = 3;
				}
			}
			if(returnValue == 1 || returnValue == 3){
				//System.out.println(highestBidders.get(listingID) + " has won the bid on ID: " +listingID + " for $" +highestBids.get(listingID));
				if(itemsUpForBidding.contains(itemsAndIDs.get(listingID))){
					
					synchronized(itemsPerSeller){
						itemsPerSeller.put(itemsAndIDs.get(listingID).seller(), itemsPerSeller.get(itemsAndIDs.get(listingID).seller())-1);
					}
					synchronized(itemsPerBuyer){
						itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName)-1);
					}
					synchronized(itemsUpForBidding){
						itemsUpForBidding.remove(itemsAndIDs.get(listingID));
					}
					revenue += highestBids.get(listingID);
					//System.out.println("Total Revenue: $" + revenue);
					soldItemsCount++;
					//System.out.println("Bidding on item ID: " + listingID + " has closed");
				}
			}

			return returnValue;
		}
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller
	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)	{
		synchronized(highestBids){
			if(!highestBids.containsKey(listingID)){
				return -1;		
			}else{
				return highestBids.get(listingID);
			}
		}
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID) {
		synchronized(highestBidders){
			if(highestBidders.containsKey(listingID)){return true;}
		}
		return false;
	}


}
