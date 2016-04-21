//Mark Penny; 111477906;
package library;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
//import java.rmi.server.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
//import java.util.Scanner;

import member.MemberData;

public class LibraryServerImpl implements LibraryServer {

	/**
	 * Constructor for the library server. It is given a number total books to have, number of
	 * copies per book, and maximum books per member. 
	 * Creates a number of Book objects based on numBooks to give them to members when checking them out.  
	 * The server maintains the properties and enforces them for future transactions.
	 * 
	 * @param numBooks
	 * @param copiesPerBook
	 * @param booksPerMember
	 */

	private final int copiesPerBook;
	private final int booksPerMember;
	private HashMap<Integer, MemberData> userLookup;
	private HashMap<String, Book> bookLookup;
	private HashMap<String, Integer> booksAvailable;
	private int memberID;

	public LibraryServerImpl(int numBooks, int copiesPerBook, int booksPerMember) {
		this.copiesPerBook = copiesPerBook;
		this.booksPerMember = booksPerMember;
		userLookup = new HashMap<Integer, MemberData>();
		bookLookup = new HashMap<String, Book>();
		booksAvailable = new HashMap<String, Integer>();
		memberID = 0;
		for(int itt =1; itt <= numBooks; itt++){
			orderNewBook(new String("Book " + Integer.toString(itt)));
		}
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#registerMember(member.Member)
	 */
	@Override
	public Integer registerMember(MemberData memberdata) throws RemoteException {
		if(memberdata == null){
			return null;
		}
		Integer newUser;
		synchronized(userLookup){
			for(Integer m: userLookup.keySet()){ //Users must have unique name
				if(userLookup.get(m).getName().equals(memberdata.getName())){
					return null;
				}
			}
			newUser = new Integer(memberID);
			userLookup.put(newUser, memberdata);
			memberID++;
		}
		return newUser;
	}


	private Object checkOutLock= new Object();
	/* (non-Javadoc)
	 * @see library.LibraryServer#checkoutBook(java.lang.String, member.Member)
	 */
	@Override
	public Book checkoutBook(String bookName, MemberData memberdata) throws RemoteException {
		synchronized(checkOutLock){
			if(bookName == null || memberdata == null){
				return null;
			}
			synchronized(userLookup){
				if(!userLookup.containsKey(memberdata.getMemberId())){
					return null;
				}
				List<Book> bl = userLookup.get(memberdata.getMemberId()).getBooksCurrCheckedOut();
				for(Book b: bl){
					if(b.isSameBookAs(bookName)){
						return null; //book already checked out according to my records
					}
				}
				bl = memberdata.getBooksCurrCheckedOut();
				for(Book b: bl){
					if(b.isSameBookAs(bookName)){
						return null; //book already checked out according to their records.
					}
				}
				List<Book> newList = userLookup.get(memberdata.getMemberId()).getBooksCurrCheckedOut();
				if(newList.size()>=booksPerMember){
					return null; //User has too many books checked out
				}
			}
			synchronized(booksAvailable){
				if(booksAvailable.containsKey(bookName) && booksAvailable.get(bookName) > 0){
					booksAvailable.put(bookName, booksAvailable.get(bookName)-1);
				}else{
					return null;
				}

			}
			synchronized(userLookup){
				List<Book> newList = userLookup.get(memberdata.getMemberId()).getBooksCurrCheckedOut();
				newList.add(bookLookup.get(bookName));
				userLookup.get(memberdata.getMemberId()).setBooksCurrCheckedOut(newList);
			}
			return bookLookup.get(bookName);
		}
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#returnBook(java.lang.String, member.Member)
	 */
	@Override
	public boolean returnBook(String bookName, MemberData memberdata) throws RemoteException {
		synchronized(checkOutLock){
			if(bookName == null || memberdata == null){//bad input
				return false;
			}
			synchronized(userLookup){
				if(!userLookup.containsKey(memberdata.getMemberId())){ //Member?
					return false;
				}

				//Do they have the book checked out according to our records?
				List<Book> temp = userLookup.get(memberdata.getMemberId()).getBooksCurrCheckedOut();
				boolean hasCheckedOut = false;
				for(Book b: temp){
					if(b.isSameBookAs(bookName)){
						hasCheckedOut = true;
					}

				}
				if(!hasCheckedOut){
					return false;
				}

				//Do they have the book checked out according the their records?
				temp = memberdata.getBooksCurrCheckedOut();
				hasCheckedOut = false;
				for(Book b: temp){
					if(b.isSameBookAs(bookName)){
						hasCheckedOut = true;
					}

				}
				if(!hasCheckedOut){
					return false;
				}
			}
			synchronized(booksAvailable){
				booksAvailable.put(bookName, booksAvailable.get(bookName)+1);
			}
			synchronized(userLookup){
				List<Book> newList = userLookup.get(memberdata.getMemberId()).getBooksCurrCheckedOut();
				newList.remove(bookLookup.get(bookName));
				userLookup.get(memberdata.getMemberId()).setBooksCurrCheckedOut(newList);
			}
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#getBookListings()
	 */
	@Override
	public List<String> getBookListings() throws RemoteException {
		List<String> returnVal = new LinkedList<String>();
		synchronized(bookLookup){
			for(String itt :bookLookup.keySet()){
				returnVal.add(itt);
			}
		}
		return returnVal;
	}

	/* (non-Javadoc)
	 * @see library.LibraryServer#getAvailableBookListings()
	 */
	@Override
	public List<String> getAvailableBookListings() throws RemoteException {
		List<String> returnVal = new LinkedList<String>();
		synchronized(bookLookup){
			synchronized(booksAvailable){
				for(String itt: bookLookup.keySet()){
					if(booksAvailable.get(itt)> 0){
						returnVal.add(itt);
					}
				}
			}
		}
		return returnVal;
	}

	private void orderNewBook(String name){
		synchronized(bookLookup){
			if(bookLookup.containsKey(name)){
				return;
			}else{
				bookLookup.put(name, new Book(name));
				synchronized(booksAvailable){
					booksAvailable.put(name, copiesPerBook);
				}
			}
		}
	}
	/*//for debugging purposes.
	private void printOutAvailability(){
		List<String> returnVal = new LinkedList<String>();
		synchronized(bookLookup){
			synchronized(booksAvailable){
				for(String itt: bookLookup.keySet()){
					returnVal.add(itt+" C: "+booksAvailable.get(itt));
				}
			}
		}
		System.out.println(returnVal);
	}
	 */
	public static void main(String[] args) {
		try{
			//if (System.getSecurityManager() == null)
			//	System.setSecurityManager ( new RMISecurityManager() );

			// Create an instance of LivraryServerImpl ...
			LibraryServerImpl svr = new LibraryServerImpl(10, 10, 5);
			LibraryServer stub = (LibraryServer) UnicastRemoteObject.exportObject(svr, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			//registry = LocateRegistry.getRegistry();

			registry.bind("LibraryServer", stub);

			System.out.println ("Service bound....");
			/*//For debugging purposes
			while(true){
				Scanner scanner = new Scanner( System.in );
				String input = scanner.nextLine();
				if(input.equals("c")){
					svr.printOutAvailability();
				}
			}
			 */
		}catch(Exception e){
			//System.err.println("Server exception: " + e.toString());
			e.printStackTrace();

		}


	}	
}
