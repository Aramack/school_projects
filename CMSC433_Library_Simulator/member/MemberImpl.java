//Mark Penny; 111477906;
package member;

//import java.net.MalformedURLException;
//import java.rmi.Naming;
//import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

import library.*;

public class MemberImpl implements Member{

	/**
	 * Default constructor of the member client. Initializes variables. 
	 * You may add other constructors if you need.
	 * 
	 */
	private LibraryServer server;
	private MemberData myData;

	public MemberImpl() {
		myData = new MemberData("Default Name", new LinkedList<Book>(),new Integer(0), new LinkedList<String>()); 
	}

	public MemberImpl(String name) {
		myData = new MemberData(name, new LinkedList<Book>(),new Integer(-1), new LinkedList<String>()); 
	}

	public MemberImpl(MemberImpl m){
		try {
			myData = new MemberData(m.getName(), m.getBooksCurrCheckedOut(), 
					m.myData.getMemberId(), m.myData.getBooksRead());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see member.Member#getName()
	 */
	public String getName() throws RemoteException{
		return myData.getName();
	}

	/* (non-Javadoc)
	 * @see member.Member#register()
	 */
	public boolean register() throws RemoteException{
		Integer newID = server.registerMember(myData);
		if(newID == null){
			return false;
		}else{
			myData.setMemberId(newID);
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see member.Member#checkoutBook(java.lang.String)
	 */
	public boolean checkoutBook(String bookName) throws RemoteException{
		Book bookRequest = server.checkoutBook(bookName, myData);
		if(bookRequest == null){
			return false;
		}else{
			myData.getBooksCurrCheckedOut().add(bookRequest);
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see member.Member#returnBook(java.lang.String)
	 */
	public boolean returnBook(String bookName) throws RemoteException{
		boolean returned = server.returnBook(bookName, myData);
		if(returned){
			for(Book itt: myData.getBooksCurrCheckedOut()){
				if(itt.getName().equals(bookName)){
					myData.getBooksCurrCheckedOut().remove(itt);
					myData.getBooksRead().add(bookName);
					break;
				}
			}
			return true;

		}else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see member.Member#getServer()
	 */
	public LibraryServer getServer() throws RemoteException{
		return server;
	}

	/* (non-Javadoc)
	 * @see member.Member#setServer(library.LibraryServer)
	 */
	public void setServer(LibraryServer server) throws RemoteException {
		this.server = server;
	}

	/* (non-Javadoc)
	 * @see member.Member#getBooksCheckedOut()
	 */
	public List<Book> getBooksCurrCheckedOut() throws RemoteException{
		return myData.getBooksCurrCheckedOut();
	}

	/* (non-Javadoc)
	 * @see member.Member#getBooksRead()
	 */
	public List<String> getBooksRead() throws RemoteException {
		return myData.getBooksRead();
	}

	public static void main(String[] args) {
		/*
		// Assign security manager
		if (System.getSecurityManager() == null){
			System.setSecurityManager
			(new RMISecurityManager());
		}

		// Call registry for libraryServer
*/		 
		try {
			Registry registry = LocateRegistry.getRegistry();
			LibraryServer stub = (LibraryServer) registry.lookup("LibraryServer");
			for(int itt = 0; itt < 2; itt++){
				new Thread(new Student(new MemberImpl("s"+itt), stub)).start();
			}/*
			Student s1 = new Student(new MemberImpl("s1"), stub);
			Thread t1 = new Thread(s1);
			t1.start();
			Student s2 = new Student(new MemberImpl("s2"), stub);
			Thread t2 = new Thread(s2);
			t2.start();
			*/
			/*MemberImpl m1 = new MemberImpl();
			m1.setServer(stub);
			m1.register();
			System.out.println("New Member ID: " + m1.myData.getMemberId());
			List<String> booksAv = m1.server.getAvailableBookListings();
			for(int itt = 0; itt < 10; itt++){
				for(String s: booksAv){
					System.out.println(s);

					if(m1.checkoutBook(s)){
						System.out.println("Checked Out:" +s);
					}else{
						System.out.println("Failed to checkout: "+ s);
					}
				}
				System.out.println("BooksCheckedOut");
				for(Book b:  m1.getBooksCurrCheckedOut()){
					System.out.println(b.getName());
				}
				String r1 = m1.getBooksCurrCheckedOut().get(0).getName();
				if(m1.returnBook(r1)){
					System.out.println("Returned: "+r1);
				}else{
					System.out.println("Failed to return: "+r1);
				}
				System.out.println("BooksCheckedOut");
				for(Book b:  m1.getBooksCurrCheckedOut()){
					System.out.println(b.getName());
				}
				for(String s: m1.getBooksRead()){
					System.out.println("I have read: " + s);
				}
			}*/
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();

		}
	}
}
