
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import sun.util.locale.StringTokenIterator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




    

/**
 *
 * @author megadave
 */
public class Main 
{
    static final boolean debug=false;
    static final boolean general=true; // for print messages
    public static void main(String g[])
    {
        String filename = "songRaagList.txt";
        SongList songList;
        try
        {
            songList = loadFile(filename);
            //if(debug) 
                songList.printSongList();

            String userSong = getUserSong();
            SongList closeSongs = recommendSongs(userSong,songList);
            closeSongs.printSongList(); // these are the recommended songs
            
        }
        catch (Exception e)
        {
            e.printStackTrace(); // stack trace will help in debugging
        }
        
        
    }

    private static String getUserSong()
    {
        System.out.println("Please enter a song of your choice to find similar songs");
        Scanner sc = new Scanner(System.in);
        String userSong = sc.nextLine();
        return userSong;
    }
    
    private static Song parseLine(String line) 
    {
        
                if(debug)
                System.out.println(line);
                StringTokenIterator st = new StringTokenIterator(line,"\t");
                
                if(debug)
                System.out.println(" -------------- ");
                
                String songName = st.current();
                String Raag =  st.next();
                Song newSong = new Song(songName,Raag);
                
                if(debug)
                System.out.println("creating song .... "+newSong.toString());
                return newSong;
        
    }

    private static SongList loadFile(String filename) throws Exception
    {
            SongList songList= new SongList();
        
            if(general)
                System.out.println("PHASE-I ... Preparing load");
            
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            
            
            if(general)
                System.out.println("PHASE-II ... Loading");
            
            String line = br.readLine(); // read the first line of the file
            while(line!=null) // read lines till end of file
            {
                songList.addSong(parseLine(line));
                line=br.readLine(); // read another line
            }
            
            if(general)
                System.out.println("File Loaded");
            
            return songList;
            
    }

    private static SongList recommendSongs(String userSong,SongList songList) 
    {
        if(general)  System.out.println("Searching...");
        
        SongList closeSongs = songList.getCloseSong(userSong);
        return closeSongs;
        
    }
}

class Song 
{

    private String songName;
    private Raag raag;
    
    public Song() 
    {
        songName=null;
        raag=null;
    }
    
    public Song(Song s) 
    {
        this.songName=s.getSongName();
        this.raag=new Raag(s.getRaag().getRaagName());       
    }
    
    public Song(String songName,String raagName) 
    {
        this.songName=songName;
        this.raag=new Raag(raagName);
    }
    
    public String toString()
    {
        return ("<"+songName+" - "+raag.toString()+">");
    }    

    public Raag getRaag() {
        return raag;
    }
        
    public String getSongName() {
        return songName;
    }        
}
    
class Raag
{
    private String raagName;
    private int raagId;
    private static HashMap <String,Integer> raagMap = new HashMap <String,Integer>();
    private static int maxRaagIndex=1; // how many raags till now
    
    public Raag(String raagName)
    {
        this.raagName = raagName;
        
        if(raagMap.containsKey(raagName))
        {
            raagMap.put(raagName, raagMap.get(raagName)); // map song to an new integer [denoting Raag]
        }
        else
        {
            raagMap.put(raagName, maxRaagIndex++); // map song to an new integer [denoting Raag]
        }
        this.raagId = raagMap.get(raagName);
        
    }
    
    
    public Raag(String raagName,int raagId)
    {
        this.raagName = raagName;
        this.raagId = raagId;
        
    }
    
    public String toString()
    {
        return (raagName+"-"+raagId);
    }
    public int getRaagId()
    {return raagId;}
    
    public String getRaagName()
    {return raagName;}
    
    public static void printRaagMapping()
    {
        Iterator iterator = raagMap.keySet().iterator();  
           
        while (iterator.hasNext()) {  
           String key = iterator.next().toString();  
           String value = raagMap.get(key).toString();  
           System.out.println(key + " - " + value);  
        }  
    }
    
}
class SongList
{
 
    private ArrayList <Song> songList ;
    
    public SongList()
    {
        songList=new ArrayList <Song> ();
    }
    

    
    public SongList getSongsWithRaagId(int id)
    {
        SongList songListWithMatchingRaagId = new SongList();
        for(int i=0;i<songList.size();i++)
        {
            Song curSong = songList.get(i);
            if(curSong.getRaag().getRaagId()==id)
            {
                songListWithMatchingRaagId.addSong(curSong);
            }
        }
        return songListWithMatchingRaagId;
    }
        
    public SongList getCloseSong(String songName)
    {
        SongList closeSongs= new SongList();
        ArrayList<Integer> potentialRaags=new ArrayList<Integer>();
        for(int i=0;i<songList.size();i++) // O(n)
        {
            Song curSong =  songList.get(i);
            String curSongName = curSong.getSongName();
            int ld = getLev(songName,curSongName); // levenshtein distance
            if(ld<=5)
            {
                int raagId = curSong.getRaag().getRaagId(); //  get raag of the song with similar name
                potentialRaags.add(raagId);
                System.out.println("Found raag = "+curSong.getRaag().getRaagName()+" id = "+raagId);
                
            }
            
        }
        
        for(int i=0;i<potentialRaags.size();i++)
        {
            SongList songListWithMatchingRaagId = getSongsWithRaagId(potentialRaags.get(i));
            for(int j=0;j<songListWithMatchingRaagId.songList.size();j++)
                closeSongs.addSong(songListWithMatchingRaagId.songList.get(j));
        }
        return closeSongs;
    }

    
    private int getLev(String a,String b) // don do this
    {
        int d=0;
        a=a.toUpperCase();b=b.toUpperCase();
        for(int i=0;i<a.length() && i<b.length();i++)
        {
            if(a.charAt(i)!=b.charAt(i))
            {
                d++;
            }
        }
        return d;
    }
    
    public void addSong(Song newSong)
    {
        songList.add(newSong);
        
    }
    
    
    public void printSongList()
    {
        for(int i=0;i<songList.size();i++)
        {
            Song curSong=songList.get(i);
            System.out.println(curSong.toString()+" ");
        }
    }
    
    
}