package com.rag.peer2peer;


import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

public class Client {
    static HashMap<String, Integer> peerIndexes = new HashMap<>();
    static ArrayList<String> peers = new ArrayList<>();
    public static boolean heartBeat(String checkPeer){
        if(peers.contains(checkPeer))
            return true;
        return false;
    }
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of clients:");
        int n = Integer.parseInt(sc.next().trim());
        boolean success = (new File( "C:\\Users\\ragha\\Desktop\\Napster")).mkdirs();
        if(!success){
            System.out.println("Problem with client java file");
            System.exit(0);
        }
        for(int i=0; i<n;i++){
            peers.add("client"+i);
            peerIndexes.put("client"+i, i);
            //Create a folder beforehand
            success = (new File( "C:\\Users\\ragha\\Desktop\\Napster\\client"+i )).mkdirs();
            if(!success){
                System.out.println("Folder not created");
                System.exit(0);
            }
        }


        for(int i=0; i<n;i++){
            ArrayList<String> bookIndexes = new ArrayList<>();
            Random random = new Random();
            for(int j=0; j< 5;j++){
                String temp = "book"+random.nextInt(5)+""+random.nextInt(10);
                if(!bookIndexes.contains(temp)){
                    bookIndexes.add(temp);
                    try{
                        PrintWriter writer = new PrintWriter("C:\\Users\\ragha\\Desktop\\Napster\\client"+i+"\\"+temp+".txt");
                        writer.append("This books is with client "+i+". And the book name is "+temp);
                        writer.close();
                    }catch(Exception ie){
                        ie.printStackTrace();
                    }

                }

            }

            //Register with server
            String[] books = new String[bookIndexes.size()];
            for(int j=0; j<bookIndexes.size();j++){
                books[j] = bookIndexes.get(j);
            }
            Server.register("client"+i,books);
        }

        while(true){
            System.out.println("Which client do you want to act like. Just give his ID between 0 to "+(n-1)+":");
            int searchIndex =  Integer.parseInt(sc.next().trim());
            if(searchIndex > n-1 || searchIndex<0){
                System.out.println("You entered invalid client ID!");
                System.out.println("What to start over again(y) or exit(n)?");
                String answer = sc.nextLine().trim();
                if(answer.equals("y"))
                    continue;
                else{
                    System.out.println("Thank you for using the system");
                    System.exit(0);
                }
            }
            else{
                String actPeer = "client"+searchIndex;
                if(!peerIndexes.containsKey(actPeer)){
                    System.out.println("Peer doesn't exist!");
                    System.out.println("Do you want to exit?(y/n");
                    String temp1 = sc.nextLine().trim();
                    if(temp1.equals("y")){
                        System.out.println("Thank you!");
                        break;
                    }
                    else
                        continue;
                }

                String resultPeer = "";
                String searchBookName = "";
                while(true){
                    System.out.println("You have the following options:*You need to mention a number associated with this*");
                    System.out.println("1. Search for a book");
                    System.out.println("2. Download the book");
                    System.out.println("3. Leave the network");
                    System.out.println("4. Exit the option list and go back");
                    String selection = sc.nextLine();
                    boolean stayFlag = false;

                    switch(selection){
                        case "1":
                            System.out.println("Enter the book you want to search");
                            searchBookName = sc.nextLine().trim();
                            if(searchBookName.equals("")){
                                System.out.println("Please enter the book name properly. Going back to options");
                                continue;
                            }
                            System.out.println("Searching the server index....");
                            ArrayList<String> resultPeers = Server.searchBook(actPeer, searchBookName);

                            if(resultPeers == null){
                                System.out.println("Sorry no peers have this book");
                                resultPeer = "";
                                continue;
                            }
                            else{
                                if(resultPeers.contains(actPeer)){
                                    System.out.println("The books is present with you. Please find in your directory");
                                    continue;
                                }

                                resultPeer = resultPeers.get(0);
                                System.out.println("Book found and is at peer "+resultPeer);
                             }
                             break;
                        case "2":
                            if(resultPeer.equals("")){
                                System.out.println("Wrong option because the peers are not present for this book in the server");
                                continue;
                            }

                            System.out.println("Downloading the file from the client "+resultPeer);
                            File source = new File("C:\\Users\\ragha\\Desktop\\Napster\\"+resultPeer+"\\"+searchBookName+".txt");
                            File dest = new File("C:\\Users\\ragha\\Desktop\\Napster\\"+actPeer);
                            try {
                                FileUtils.copyFileToDirectory(source, dest);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //You need to register with the server when the file is downloaded
                            Server.register(actPeer, new String[]{searchBookName});
                            break;
                        case "3":
                            int index = peerIndexes.get(actPeer);
                            peers.remove(index);
                            peerIndexes.remove(actPeer);

                           /* //Remove the directory

                            try {
                                FileUtils.deleteDirectory(new File("C:\\Users\\ragha\\Desktop\\Napster\\"+actPeer));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/

                        case "4":
                            System.out.println("Thank you for . Going back to first option");
                            stayFlag = true;
                            break;

                  }

                  if(stayFlag)
                      break;


                }
            }
            System.out.println("Do you want to exit?(y/n");
            String temp1 = sc.nextLine().trim();
            if(temp1.equals("y")){
                System.out.println("Thank you!");
                 break;
            }

        }
        sc.close();


    }
}
