package aula6;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Devem criar a vossa conta Last.FM apps em 
 * http://www.last.fm/api/account/create
 *
 */
public class LastFMArtistsJSON {
	public static void main( String[] args) {
		try {
			if( args.length > 1) {
				System.err.println("java LastFMArtists artist");
				return;
			}
			String artistName = args.length == 0 ? "Bauhaus" : args[0];
			URI uri = new URI("http",
					"ws.audioscrobbler.com",
					"/2.0/",
					"method=artist.search&artist=" + artistName + "&api_key=4cf23dfac51047cb72913d1507de9a25&format=json",
					null);

			URL url = uri.toURL();
			
			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(new InputStreamReader( url.openStream()));

			JSONArray artists = (JSONArray)((JSONObject)((JSONObject) res.get("results")).get( "artistmatches")).get("artist");
			Iterator it = artists.iterator();
			while (it.hasNext()) {
				JSONObject artist = (JSONObject) it.next();
				System.out.println( artist.get("name") + " -> " + artist.get("url"));
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
