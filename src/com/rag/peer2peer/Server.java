package com.rag.peer2peer;


import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

class FileInfo{
     String booKName;
     ArrayList<String> peers;
     FileInfo(){
         this.peers = new ArrayList<>();
     }

     String getBookName(){
         return this.booKName;
     }
     ArrayList<String> getPeers(){
         return this.peers;
     }

     void setName(String name){
         this.booKName = name;
     }

     void setPeers(ArrayList<String> peer){
         this.peers = peer;
     }


}

public class Server {
    static ArrayList<String> hIndex = new ArrayList<>();
    static ArrayList<FileInfo> centralIndex = new ArrayList<>();
    public static int bookNameCheck(String book){
        int i = 0;
        for(FileInfo element: centralIndex){
            if(element.getBookName().equals(book))
                return i;
            i++;
        }
        return -1;
    }




    public static void register(String peerId, String[] bookNames){

        //If Peer enters the server for the first time
        if(!hIndex.contains(peerId)){
            hIndex.add(peerId);
            for(int i=0; i< bookNames.length; i++){
                int index = bookNameCheck(bookNames[i]);
                if(index != -1){
                    FileInfo f1 = centralIndex.get(index);
                    f1.peers.add(peerId);

                }
                else{
                    FileInfo f1 = new FileInfo();
                    f1.booKName = bookNames[i];
                    f1.peers.add(peerId);
                    centralIndex.add(f1);
                }
            }



        }
        else{
            for(int i=0; i< bookNames.length; i++){
                int index = bookNameCheck(bookNames[i]);
                if(index!= -1){
                    FileInfo f1 = centralIndex.get(index);
                    if(!f1.peers.contains(peerId))
                        f1.peers.add(peerId);
                }
                else{
                    FileInfo f1 = new FileInfo();
                    f1.booKName = bookNames[i];
                    f1.peers.add(peerId);
                    centralIndex.add(f1);

                }
            }
        }
    }



    public static ArrayList<String> searchBook(String peerID, String book){
        if(!hIndex.contains(peerID)){
            System.out.println("You are have to register first in order to use my service!");
            return null;
        }
        int index = bookNameCheck(book);
        if(index == -1){
            System.out.println("Sorry the book is not available!");
            return null;
        }
        else{
            System.out.println("Book Found!");
            ArrayList<String> peers = centralIndex.get(index).getPeers();
            for(int i=0; i< peers.size(); i++){
                String peerId = peers.get(i);
                if(!Client.heartBeat(peerId)){
                    peers.remove(i);
                    i--;
                }
            }
            if(peers.size() == 0){
                System.out.println("No peers are available");
                hIndex.remove(index);
                centralIndex.remove(index);

                return null;
            }

            return peers;
        }



    }


}

