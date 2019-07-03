package Assignment2;

import java.util.ArrayList;
import java.util.Random;

class transactions extends Thread{
	int val;
	database d;
	static int numtransactions=0;
	static int numres=0;
	static int numcan=0;
	static int nummyfl=0;
	static int numtotres=0;
	static int numtra=0;
	public transactions(int v, database base) {
//		System.out.println("((((((((((((");
		val=v;
		d=base;
	}
	
	public void run() {
		long t= System.currentTimeMillis();
		long end = t+10000;
		int m;
		while(System.currentTimeMillis() < end) {
//			synchronized(d) {
				// do something
				// pause to avoid churning
				//		  Thread.sleep( xxx );
				val=new Random().nextInt(5);
				//		int num=new Random().nextInt(5);
				//		System.out.println("//////");
				if (val==0) {
					try {
//						this.Reserve(0, 150, d);
//						this.Reserve(0, 151, d);
						int flno=new Random().nextInt(5);
						int pasno=new Random().nextInt(20)+101;
						
						this.Reserve(flno, pasno, d);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				else if(val==1) {
					try {
//						this.Cancel(0, 101, d);
						int flno=new Random().nextInt(5);
						int pasno=new Random().nextInt(20)+101;
						
						this.Cancel(flno, pasno, d);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(val==2) {
					try {
						//				int flno=new Random().nextInt(5);
						int pasno=new Random().nextInt(20)+101;
						
						this.My_Flights(pasno);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(val==3) {
					try {
						
						this.Total_Reservations();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(val==4) {
					try {
						int flno1=new Random().nextInt(5);
						int flno2=new Random().nextInt(5);
						while (flno1==flno2) {
							flno2=new Random().nextInt(5);
						}
						int pasno=new Random().nextInt(20)+101;
						
						this.Transfer(flno1, flno2, pasno, d);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}
	}
	
	public void Reserve(int F, int i, database d) throws InterruptedException {
		Thread.sleep(100);
		System.out.println("RESERVE STARTS");
				
		System.out.println("RESERVE DATABASE SLOCK ACQUIRED");
			d.setslock(true);
			for (int k=0;k<d.myflights.size();k++) {
				if (F==d.myflights.get(k).fid) {
					
					if (d.myflights.get(k).getslock()==true || d.myflights.get(k).getlock()==true) {
						while(d.myflights.get(k).getslock()==true || d.myflights.get(k).getlock()==true) {
							Thread.sleep(10);
							System.out.println(d.myflights.get(k).fid);
//							System.out.println("############");
						}
					}
					synchronized(d.myflights.get(k)) {
					d.myflights.get(k).setlock(true);
					System.out.println("RESERVE FLIGHT XLOCK ACQUIRED");
					if (d.myflights.get(k).reserved<d.myflights.get(k).numseats) {
						d.myflights.get(k).mypass.add(new passenger(i));
						d.myflights.get(k).reserved++;
						database.resvs++;
//						System.out.println("#######");
					}
					else {
						System.out.println("Reservation denied! Already full reservation in this flight!");
					}
//					}
					d.myflights.get(k).setlock(false);
					}
					System.out.println("RESERVE FLIGHT XLOCK GIVEN");
					break;
				}
			}
			System.out.println("RESERVE DATABASE SLOCK GIVEN");
			System.out.println("RESERVE ENDS");
			numres++;
			numtransactions++;
			d.setslock(false);
	}
	
	public void Cancel(int F, int i, database d) throws InterruptedException {
		Thread.sleep(100);
		System.out.println("CANCEL STARTS");
		System.out.println("CANCEL DATABASE SLOCK ACQUIRED");
		d.setslock(true);
		boolean flag=false;
		for (int k=0;k<d.myflights.size();k++) {
			if (F==d.myflights.get(k).fid) {
				
				while(d.myflights.get(k).getslock()==true || d.myflights.get(k).getlock()==true) {
					Thread.sleep(10);
					
//					System.out.println("--------");
				}
				synchronized(d.myflights.get(k)) {
//				System.out.println("######");
				d.myflights.get(k).setlock(true);
//				System.out.println("@@@@@@2");
				System.out.println("CANCEL FLIGHT XLOCK ACQUIRED");
				int po=-1;
				for (int j=0;j<d.myflights.get(k).mypass.size();j++) {
					while(d.myflights.get(k).mypass.get(j).getslock()==true || d.myflights.get(k).mypass.get(j).getlock()==true) {
						Thread.sleep(10);
					}
					d.myflights.get(k).mypass.get(j).setslock(true);
					System.out.println("CANCEL PASSENGER SLOCK ACQUIRED");
					if (d.myflights.get(k).mypass.get(j).pid==i) {
						//							System.out.println("*********/////");
//						d.myflights.get(k).mypass.get(j).setlock(true);
//						System.out.println("CANCEL PASSENGER XLOCK ACQUIRED");
						d.myflights.get(k).mypass.remove(j);
						d.myflights.get(k).reserved--;
						database.resvs--;
						po=j;
						flag=true;
						break;
						//							System.out.println("aaaaaaaaaaaaaa");
					}
				}
				
				for (int j=0;j<d.myflights.get(k).mypass.size();j++) {
					if (j==po) {
//						d.myflights.get(k).mypass.get(j).setlock(false);
						d.myflights.get(k).mypass.get(j).setslock(false);
						break;
					}
					d.myflights.get(k).mypass.get(j).setslock(false);
				}
//				}
//				d.myflights.get(k).mypass.get(po).setlock(false);
				d.myflights.get(k).setlock(false);
			}
				System.out.println("CANCEL FLIGHT XLOCK GIVEN");
			}
		}
		
		if (flag==false) {
			System.out.println("Cancel Denied! No passenger with this id in the flight or no flight with such id!");
		}
		System.out.println("CANCEL ENDS");
		numtransactions++;
		numcan++;
		d.setlock(false);
		System.out.println("CANCEL DATABASE SLOCK GIVEN");
	}
	
	public void My_Flights(int id)  throws InterruptedException {
		Thread.sleep(100);
		System.out.println("My_Flight STARTS");
		d.setslock(true);
		System.out.println("My_Flight database SLOCK ACQUIRED");
		ArrayList<Integer> myfl=new ArrayList<Integer>();
		for (int k=0;k<d.myflights.size();k++) {
			while(d.myflights.get(k).getslock()==true || d.myflights.get(k).getlock()==true) {
//				System.out.println("^^^^");
				Thread.sleep(10);
			}
			d.myflights.get(k).setslock(true);
			for (int j=0;j<d.myflights.get(k).mypass.size();j++) {
				d.myflights.get(k).mypass.get(j).setslock(true);
				if (d.myflights.get(k).mypass.get(j).pid==id) {
					myfl.add(d.myflights.get(k).fid);
					break;
				}
			}
		}
		for (int k=0;k<d.myflights.size();k++) {
			for (int j=0;j<d.myflights.get(k).mypass.size();j++) {
				d.myflights.get(k).mypass.get(j).setslock(false);
//				System.out.println("PID lock given: "+d.myflights.get(k).mypass.get(j).pid);
			}
			d.myflights.get(k).setslock(false);
//			System.out.println("FID lock given: "+d.myflights.get(k).fid);
		}
		System.out.println("List of flights for passenger no "+id+" are: "+myfl);
		System.out.println("My_Flight ENDS");
		numtransactions++;
		nummyfl++;
		d.setslock(false);
	}
	
	public void Total_Reservations() throws InterruptedException {
		Thread.sleep(100);
		System.out.println("Total_Reservations STARTS");
		
		System.out.println("Total_Reservations LOCK ACQUIRED");
			d.setslock(true);
			System.out.println(database.resvs-101);
			System.out.println("Total_Reservations ENDS");
			numtransactions++;
			numtotres++;
			d.setslock(false);
		
	}
	public void Transfer(int f1, int f2, int id, database d)  throws InterruptedException {
		Thread.sleep(100);
		System.out.println("Transfer STARTS");
		System.out.println("Transfer LOCK ACQUIRED");
		d.setslock(true);
		boolean flag=false;
		for (int k=0;k<d.myflights.size();k++) {
			if (d.myflights.get(k).fid==f1) {
				while(d.myflights.get(k).getslock()==true || d.myflights.get(k).getlock()==true) {
//					System.out.println("^^^^");
					Thread.sleep(10);
				}
				
				synchronized (d.myflights.get(k)) {
					d.myflights.get(k).setlock(true);
				for (int l=0;l<d.myflights.get(k).mypass.size();l++) {
					if (d.myflights.get(k).mypass.get(l).pid==id) {
						for (int j=0;j<d.myflights.size();j++) {
							if (d.myflights.get(j).fid==f2) {
								while(d.myflights.get(j).getslock()==true || d.myflights.get(j).getlock()==true) {
//									System.out.println("^^^^");
									Thread.sleep(10);
								}
								
								synchronized(d.myflights.get(j)) {
									d.myflights.get(j).setlock(true);
								if (d.myflights.get(j).reserved<d.myflights.get(j).numseats) {
									d.myflights.get(k).mypass.remove(l);
									d.myflights.get(j).mypass.add(new passenger(id));
									d.myflights.get(k).reserved--;
									d.myflights.get(j).reserved++;
									flag=true;
								}
								d.myflights.get(j).setlock(false);
								break;
								
							}
							}
						}
						break;
					}
				}
				d.myflights.get(k).setlock(false);
				break;
			}
			}
		}
		if (!flag) {
			System.out.println("Transfer denied! Either no such flight/passenger id or there is no seats left in F2!");
		}
//		System.out.println("List of my flights are: "+myfl);
		numtransactions++;
		numtra++;
		System.out.println("Transfer ENDS");
		d.setslock(false);
	}
}
public class db2pl {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		database base=new database(101);
		int flights=5;
//		int res=101;
		for (int i=0;i<flights;i++) {
			flight fl=new flight(i);
			int num=new Random().nextInt(5)+5;
			fl.numseats=num;
			int resv=new Random().nextInt(num);
			fl.reserved=resv;
			for (int j=0;j<resv;j++) {
				passenger pa=new passenger(database.resvs++);
				fl.mypass.add(pa);
			}
			base.myflights.add(fl);
			base.numflights++;
			
		}
		for (int i=0;i<base.myflights.size();i++) {
			System.out.println("Fid: "+base.myflights.get(i).fid+"\tNumseats: "+base.myflights.get(i).numseats+"\tReserved: "+base.myflights.get(i).reserved);
			for (int j=0;j<base.myflights.get(i).mypass.size();j++) {
				System.out.println("\tId: "+base.myflights.get(i).mypass.get(j).pid);
			}
		}
		System.out.println();
		System.out.println("-----------");
		System.out.println();
//		transactions t1=new transactions(0,base);
//		transactions t2=new transactions(1,base);
//		transaction t3=new transaction(2,base);
//		transaction t4=new transaction(3,base);
//		transaction t5=new transaction(4,base);
//		t1.start();
//		t2.start();
//		t3.start();
//		t4.start();
//		t5.start();
		ArrayList<transactions> ts=new ArrayList<transactions>();
		for (int m=0;m<25;m++) {
			transactions t1=new transactions(0,base);
			ts.add(t1);
		}
//		long t= System.currentTimeMillis();
//		long end = t+10000;
		int m;
//		while(System.currentTimeMillis() < end) {
			for (m=0;m<ts.size();m++) {
				ts.get(m).start();
			}
//		}
		Thread.sleep(10000);
		for (m=0;m<ts.size();m++) {
			ts.get(m).stop();
		}
		
		Thread.sleep(500);
		System.out.println();
		System.out.println("------------");
		System.out.println();
		for (int i=0;i<base.myflights.size();i++) {
			System.out.println("Fid: "+base.myflights.get(i).fid+"\tNumseats: "+base.myflights.get(i).numseats+"\tReserved: "+base.myflights.get(i).reserved);
			for (int j=0;j<base.myflights.get(i).mypass.size();j++) {
				System.out.println("\tId: "+base.myflights.get(i).mypass.get(j).pid);
			}
		}
		System.out.println("Total TRANSACTIONS: "+transactions.numtransactions);
		System.out.println("Total Reservations: "+transactions.numres);
		System.out.println("Total Cancels: "+transactions.numcan);
		System.out.println("Total My Flights: "+transactions.nummyfl);
		System.out.println("Total Check total transaction: "+transactions.numtotres);
		System.out.println("Total Transfers: "+transactions.numtra);
	}

}
