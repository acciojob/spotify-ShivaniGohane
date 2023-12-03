package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newuser = new User(name, mobile);
        users.add(newuser);
        return newuser;
    }

    public Artist createArtist(String name) {
        Artist newartist = new Artist(name);
        artists.add(newartist);
        return newartist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist currArtist = null;
        for(Artist ast : artists){
            if(artistName.equals(ast)){
                currArtist = ast;
                break;
            }
        }

        if(currArtist==null){
            currArtist = createArtist(artistName);
        }
        Album newalbum = new Album(title);
        albums.add(newalbum);

        List<Album> currAlbumList = new ArrayList<>();
        for(Artist ast : artistAlbumMap.keySet()){
            if(ast.getName().equals(artistName)){
                currAlbumList = artistAlbumMap.get(ast);
            }
        }
        currAlbumList.add(newalbum);
        artistAlbumMap.put(currArtist, currAlbumList);
        return newalbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album newalbum = null;
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                newalbum = album;
                break;
            }
        }

        if(newalbum==null){
            throw new Exception("Album does not exist");
        }

        Song newsong = new Song(title,length);
        newsong.setLikes(0);
        songs.add(newsong);

        List<Song> newsongList = new ArrayList<>();
        for(Album album : albumSongMap.keySet()){
            if(albumSongMap.containsKey(newalbum)){
                newsongList = albumSongMap.get(newalbum);
            }
        }
        newsongList.add(newsong);
        albumSongMap.put(newalbum, newsongList);
        return newsong;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User newUser = null;
        for (User user : users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist newPlayList = new Playlist(title);
            playlists.add(newPlayList);

            List<Song> songList = new ArrayList<>();
            for(Song song : songs){
                if(song.getLength()==length){
                    songList.add(song);
                }
            }
            playlistSongMap.put(newPlayList, songList);

            List<User> userList = new ArrayList<>();
            userList.add(newUser);

            creatorPlaylistMap.put(newUser, newPlayList);
            playlistListenerMap.put(newPlayList, userList);

            if(userPlaylistMap.containsKey(newUser)){
                List<Playlist> playlists1 = userPlaylistMap.get(newUser);
                playlists1.add(newPlayList);
                userPlaylistMap.put(newUser, playlists1);
            }
            else{
                List<Playlist> playlists1 = new ArrayList<>();
                playlists1.add(newPlayList);
                userPlaylistMap.put(newUser, playlists1);
            }
            return newPlayList;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
     //public HashMap<Playlist, List<User>> playlistListenerMap;
        User newUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                break;
            }
        }

        if(newUser==null){
            throw new Exception("User does not exist");
        }
        else{
            Playlist newPlayList = new Playlist(title);
            playlists.add(newPlayList);

            List<Song> songList = new ArrayList<>();
            for (String songtitle : songTitles){
                for (Song song : songs){
                    if(song.getTitle().equals(songtitle)){
                        songList.add(song);
                    }
                }
            }
            playlistSongMap.put(newPlayList, songList);

            List<User> users1 = new ArrayList<>();
            users1.add(newUser);
            creatorPlaylistMap.put(newUser, newPlayList);
            playlistListenerMap.put(newPlayList, users1);

            if (userPlaylistMap.containsKey(newUser)){
                List<Playlist> playlists1 = userPlaylistMap.get(newUser);
                playlists1.add(newPlayList);
                userPlaylistMap.put(newUser, playlists1);
            }
            else {
                List<Playlist> playlists1 = new ArrayList<>();
                playlists1.add(newPlayList);
                userPlaylistMap.put(newUser, playlists1);
            }
            return newPlayList;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User newUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                break;
            }
        }
        if(newUser==null){
            throw new Exception("User does not exist");
        }
        Playlist newPlayList = null;
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                newPlayList = playlist;
                break;
            }
        }

        if(newPlayList==null){
            throw new Exception("PlayList Does not exist");
        }

        if(creatorPlaylistMap.containsKey(newUser)){
            return newPlayList;
        }

        List<User> newListeners = playlistListenerMap.get(newPlayList);
        for (User u : newListeners){
            if(u == newUser){
                return newPlayList;
            }
        }

        newListeners.add(newUser);
        playlistListenerMap.put(newPlayList, newListeners);

        List<Playlist> playlists1 = userPlaylistMap.get(newUser);
        if(playlists1 == null){
            playlists1 = new ArrayList<>();
        }

        playlists1.add(newPlayList);
        userPlaylistMap.put(newUser, playlists1);

        return newPlayList;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User newUser = null;
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                newUser = user;
                break;
            }
        }

        if(newUser == null){
            throw new Exception("User does not exist");
        }

        Song newSong = null;
        for (Song song : songs){
            if(song.getTitle().equals(songTitle)){
                newSong = song;
                break;
            }
        }
        if(newSong == null){
            throw new Exception("Song does not exist");
        }

        List<User> likedUsers = new ArrayList<>();
        if(songLikeMap.containsKey(newSong)){
            likedUsers = songLikeMap.get(newSong);
        }

        if(likedUsers.contains(newUser)){
            return newSong;
        }
        else {
            int likes = newSong.getLikes()+1;
            newSong.setLikes(likes);
            likedUsers.add(newUser);
            songLikeMap.put(newSong, likedUsers);

            Album newAlbum = null;
            for (Album album : albumSongMap.keySet()){
                List<Song> songs1 = albumSongMap.get(album);
                if(songs1.contains(newSong)){
                    newAlbum = album;
                    break;
                }
            }

            Artist newArtist = null;
            for(Artist artist : artistAlbumMap.keySet()){
                List<Album> albums1 = artistAlbumMap.get(artist);
                if(albums1.contains(newAlbum)){
                    newArtist = artist;
                    break;
                }
            }

            int artistLikes = newArtist.getLikes() + 1;
            newArtist.setLikes(artistLikes);

            return newSong;
        }

    }

    public String mostPopularArtist() {
        int max = 0;
        Artist popularArtist = null;
        for (Artist artist : artists){
            if(artist.getLikes() > max){
                max = artist.getLikes();
                popularArtist = artist;
            }
        }

        if(popularArtist == null){
            return null;
        }
        return popularArtist.getName();
    }

    public String mostPopularSong() {
        int max = 0;
        Song popularSong = null;

        for (Song song : songLikeMap.keySet()){
            int likes = songLikeMap.get(song).size();
            if(likes>max){
                max = likes;
                popularSong = song;
            }
        }

        if(popularSong == null){
            return null;
        }
        return popularSong.getTitle();
    }
}
