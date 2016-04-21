//Mark Penny; 111477906;
package member;

//import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import library.Book;
import library.LibraryServer;

public class Student implements Runnable{
	MemberImpl me;
	public Student(MemberImpl m, LibraryServer stub){
		me = m;
		try{
			me.setServer(stub);
			me.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

		Random r = new Random();
		for(int itt = 0; itt < 1; itt++){
			int test = r.nextInt(3);
			if(test == 0){
				try {
					List<String> Listing = me.getServer().getBookListings();
					if(Listing.size()> 0){
						String b =Listing.get(r.nextInt(Listing.size()));
						if(me.checkoutBook(b)){
							System.out.println(me.getName() +" checked out: " + b);
						}else{
							System.out.println(me.getName() + " failed to check out: "+b);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (test == 1){
				try {
					List<String> Listing = me.getServer().getAvailableBookListings();
					if(Listing.size()>0){
						String b =Listing.get(r.nextInt(Listing.size()));
						if(me.checkoutBook(b)){
							System.out.println(me.getName() +" checked out: " + b);
						}else{
							System.out.println(me.getName() + " failed to check out: "+b);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(test == 2){
				try {
					List<Book> Listing = me.getBooksCurrCheckedOut();
					if(Listing.size() > 0){
						String b =Listing.get(r.nextInt(Listing.size())).getName();
						if(me.returnBook(b)){
							System.out.println(me.getName() +" returned: " + b);
						}else{
							System.out.println(me.getName() + " failed to return out: "+b);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			while(me.getBooksCurrCheckedOut().size()>0){
				Book b1 = me.getBooksCurrCheckedOut().get(0);
				String b = b1.getName();
				if(me.returnBook(b)){
					System.out.println(me.getName() +" returned: " + b);
				}else{
					System.out.println(me.getName() + " failed to return out: "+b);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}