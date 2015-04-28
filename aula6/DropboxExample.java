package aula6;

import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

/**
 * Exemplo de acesso ao servico dropbox.
 * <p>
 * A API REST do sistema esta disponivel em: <br>
 * https://www.dropbox.com/developers/core/docs
 * <p>
 * Para poder aceder ao servico dropbox, deve criar uma app em:
 * https://www.dropbox.com/developers/apps onde obtera a apiKey e a apiSecret a
 * usar na criacao do objecto OAuthService
 * <p>
 * Este exemplo usa a biblioteca OAuth Scribe, disponivel em:
 * https://github.com/fernandezpablo85/scribe-java
 * <p>
 * e a biblioteca json-simple, disponivel em:
 * http://code.google.com/p/json-simple/
 * <p>
 * e a biblioteca apache commons codec, disponivel em:
 * http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */
public class DropboxExample
{
	private static final String API_KEY = "y77jdgxjghny2f5";
	private static final String API_SECRET = "cqgryzv937jcmog";
	private static final String SCOPE = "dropbox";		//""
	private static final String AUTHORIZE_URL = "https://www.dropbox.com/1/oauth/authorize?oauth_token=";

	public static void main(String[] args) {
		try {
			OAuthService service = new ServiceBuilder().provider(DropBoxApi.class).apiKey(API_KEY)
					.apiSecret(API_SECRET).scope(SCOPE).build();
			Scanner in = new Scanner(System.in);

			// Obter Request token
			Token requestToken = service.getRequestToken();
			
			System.out.println("Tem de obter autorizacao para a aplicacao continuar acedendo ao link:");
			System.out.println(AUTHORIZE_URL + requestToken.getToken());
			System.out.println("E carregar em enter quando der autorizacao");
			System.out.print(">>");
			Verifier verifier = new Verifier(in.nextLine());

			// O Dropbox usa como verifier o mesmo segredo do request token, ao
			// contrario de outros
			// sistemas, que usam um codigo fornecido na pagina web
			// Com esses sistemas a linha abaixo esta a mais
			verifier = new Verifier(requestToken.getSecret());
			// Obter access token
			Token accessToken = service.getAccessToken(requestToken, verifier);

			// Obter listagem do directorio raiz
			OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.dropbox.com/1/metadata/dropbox/?list=true");
			service.signRequest(accessToken, request);
			Response response = request.send();

			if (response.getCode() != 200)
				throw new RuntimeException("Metadata response code:" + response.getCode());

			JSONParser parser = new JSONParser();
			JSONObject res = (JSONObject) parser.parse(response.getBody());

			JSONArray items = (JSONArray) res.get("contents");
			Iterator it = items.iterator();
			while (it.hasNext()) {
				JSONObject file = (JSONObject) it.next();
				System.out.println(file.get("path"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
