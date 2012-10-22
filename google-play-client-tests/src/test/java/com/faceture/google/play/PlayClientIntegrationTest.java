
/*
 * Copyright (c) 2012. Faceture Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.faceture.google.play;

import com.faceture.google.play.domain.Playlist;
import com.faceture.google.play.domain.SearchResults;
import com.faceture.google.play.domain.Song;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Properties;

/**
 * Integration tests for the PlayClient
 */
public class PlayClientIntegrationTest extends TestCase {

    // class under test
    PlayClient playClient;

    // other
    Properties testProperties = new Properties();

    public PlayClientIntegrationTest() throws IOException {
        testProperties.load(new FileInputStream("Test.properties"));
    }


    public void setUp() throws Exception {
        super.setUp();

        PlayClientBuilder playClientBuilder = new PlayClientBuilder();
        playClient = playClientBuilder.create();
    }

    /**
     * End-to-end test that shows how to use the PlayClient
     * @throws IOException
     * @throws URISyntaxException
     */
    public void testEndToEnd() throws IOException, URISyntaxException {
        // get our properties
        String emailAddress = (String) testProperties.get(PropertyConsts.EMAIL_ADDRESS);
        String password = (String) testProperties.get(PropertyConsts.PASSWORD);
        String queryString = (String) testProperties.get(PropertyConsts.QUERY);

        assertNotNull("Did you delete the property from the properties file?", emailAddress);
        assertFalse("Did you forget to set the properties in the properties file?", emailAddress.isEmpty());
        assertNotNull("Did you delete the property from the properties file?", password);
        assertFalse("Did you forget to set the properties in the properties file?", password.isEmpty());
        assertNotNull("Did you delete the property from the properties file?", queryString);
        assertFalse("Did you forget to set the properties in the properties file?", queryString.isEmpty());

        // login
        LoginResponse loginResponse = playClient.login(emailAddress, password);

        assertNotNull(loginResponse);
        assertEquals(LoginResult.SUCCESS, loginResponse.getLoginResult());

        PlaySession playSession = loginResponse.getPlaySession();

        // search
        SearchResults searchResults = playClient.search(queryString, playSession);

        // find a song to play in the search results
        Collection<Song> albumMatch = searchResults.getAlbums();
        Collection<Song> artistMatch = searchResults.getArtists();
        Collection<Song> songMatch = searchResults.getSongs();

        Song song = (Song) (!albumMatch.isEmpty() ? albumMatch.toArray()[0] :
                            !artistMatch.isEmpty() ? artistMatch.toArray()[0] :
                            !songMatch.isEmpty() ? songMatch.toArray()[0] : null);

        assertNotNull("No songs found with the query '" + queryString + "'", song);

        // get the playable URL
        URI uri = playClient.getPlayURI(song.getId(), playSession);
        assertNotNull("Playable URI not found", uri);

        // get all of the tracks
        Collection<Song> songs = playClient.loadAllTracks(playSession);
        assertNotNull(songs);
        assertTrue(songs.size() > 0);

        // get all of the playlists
        Collection<Playlist> playlists = playClient.loadAllPlaylists(playSession);

        assertNotNull(playlists);
        Playlist playlist = playlists.iterator().next();
        Collection<Song> playlistSongs = playlist.getPlaylist();
        assertNotNull(playlistSongs);
        Song playlistSong = playlistSongs.iterator().next();
        assertNotNull(playlistSong);
        String title = playlistSong.getTitle();
        assertNotNull(title);
        assertFalse(title.isEmpty());
    }

    public void testLoginFailure() throws IOException, URISyntaxException {
        LoginResponse loginResponse = playClient.login("badEmail", "badPassword");

        assertNotNull(loginResponse);
        assertEquals(LoginResult.BAD_CREDENTIALS, loginResponse.getLoginResult());
    }
}
