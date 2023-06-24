package ap.ex2.BookScrabbleServer;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class MainTrain {

	public static class ClientHandler1 implements SimpleClientHandler {
		PrintWriter out;
		Scanner in;

		@Override
		public void handleClient(InputStream inFromclient, OutputStream outToClient) {
			out=new PrintWriter(outToClient);
			in=new Scanner(inFromclient);
			String text = in.next();
			String str = new StringBuilder(text).reverse().toString();
			out.println(str);
			out.flush();
		}

		@Override
		public void close() {
			in.close();
			out.close();
		}

	}


	public static void client1(int port) {
		new Thread(
				()->{
					try{
						Socket server = new Socket("localhost", port);
						Random r = new Random();
						String text = String.valueOf(1000 + r.nextInt(100000));
						String rev = new StringBuilder(text).reverse().toString();
						PrintWriter outToServer = new PrintWriter(server.getOutputStream());
						Scanner in = new Scanner(server.getInputStream());
						outToServer.println(text);
						outToServer.flush();
						String response=in.next();
						if(response==null || !response.equals(rev))
							System.out.println("problem getting the right response from your server, cannot continue the test (-25)");
						in.close();
						outToServer.close();
						server.close();
					} catch (Exception e){
						System.out.println("Exception was thrown when running a client (-25)");
					}
				}).start();
	}

	public static boolean testServer() {
		boolean ok = true;
		Random r = new Random();
		int port = 6000 + r.nextInt(1000);
		GenericServer s = new GenericServer(port, new ClientHandler1(),3);//);
		int c = Thread.activeCount();
		s.start(); // runs in the background
		try {
			client1(port);
			client1(port);
			client1(port);
			client1(port);
		}catch(Exception e) {
			System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");
			ok = false;
		}
		try {Thread.sleep(2000);} catch (InterruptedException e) {}
		s.close();

		try {Thread.sleep(2000);} catch (InterruptedException e) {}

		if (Thread.activeCount()!=c) {
			System.out.println("you have a thread open after calling close method (-100)");
			ok = false;
		}
		return ok;
	}


	public static String[] writeFile(String name) {
		Random r=new Random();
		String[] txt = new String[10];
		for(int i=0;i<txt.length;i++)
			txt[i] = String.valueOf(10000 + r.nextInt(10000));

		try {
			PrintWriter out=new PrintWriter(new FileWriter(name));
			for(String s : txt) {
				out.print(s+" ");
			}
			out.println();
			out.close();
		} catch(Exception e) {}

		return txt;
	}

	public static void testDM() {
		String[] t1 =writeFile("t1.txt");
		String[] t2 =writeFile("t2.txt");
		String[] t3 =writeFile("t3.txt");

		DictionaryManager dm=DictionaryManager.get();

		if(!dm.query("t1.txt","t2.txt",t2[4]))
			System.out.println("problem for Dictionary Manager query (-5)");
		if(!dm.query("t1.txt","t2.txt",t1[9]))
			System.out.println("problem for Dictionary Manager query (-5)");
		if(dm.query("t1.txt","t3.txt","2"+t3[2]))
			System.out.println("problem for Dictionary Manager query (-5)");
		if(dm.query("t2.txt","t3.txt","3"+t2[5]))
			System.out.println("problem for Dictionary Manager query (-5)");
		if(!dm.challenge("t1.txt","t2.txt","t3.txt",t3[2]))
			System.out.println("problem for Dictionary Manager challenge (-5)");
		if(dm.challenge("t2.txt","t3.txt","t1.txt","3"+t2[5]))
			System.out.println("problem for Dictionary Manager challenge (-5)");

		if(dm.getSize()!=3)
			System.out.println("wrong size for the Dictionary Manager (-10)");

	}

	public static void runClient(int port,String query,boolean result) {
		try {
			Socket server=new Socket("localhost",port);
			PrintWriter out=new PrintWriter(server.getOutputStream());
			Scanner in=new Scanner(server.getInputStream());
			out.println(query);
			out.flush();
			String res=in.nextLine();
			if((result && !res.equals("true")) || (!result && !res.equals("false")))
				System.out.println("problem getting the right answer from the server (-10)");
			in.close();
			out.close();
			server.close();
		} catch (IOException e) {
			System.out.println("your code ran into an IOException (-10)");
		}
	}

	public static void testBSCH() {
		String[] s1 =writeFile("s1.txt");
		String[] s2 =writeFile("s2.txt");

		Random r=new Random();
		int port=6000+r.nextInt(1000);
		GenericServer s=new GenericServer(port, new BookScrabbleHandler(),1);
		s.start();
		runClient(port, "Q,s1.txt,s2.txt,"+s1[1], true);
		runClient(port, "Q,s1.txt,s2.txt,"+s2[4], true);
		runClient(port, "Q,s1.txt,s2.txt,2"+s1[1], false);
		runClient(port, "Q,s1.txt,s2.txt,3"+s2[4], false);
		runClient(port, "C,s1.txt,s2.txt,"+s1[9], true);
		runClient(port, "C,s1.txt,s2.txt,#"+s2[1], false);
		s.close();
	}

	public static void main(String[] args) {
		if(testServer()) {
			System.out.println("testServer passed.");
			testDM();
			System.out.println("testDM passed.");
			testBSCH();
			System.out.println("testBSCH passed.");
		}
		System.out.println("done!");
	}
}
