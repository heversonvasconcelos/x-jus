package br.jus.trf2.xjus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import br.jus.trf2.xjus.IXjus.User;

import com.auth0.jwt.JWTVerifier;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Utils {
	public static byte[] calcSha1(byte[] content) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.reset();
		md.update(content);
		byte[] output = md.digest();
		return output;
	}

	public static byte[] calcSha256(byte[] content) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.reset();
		md.update(content);
		byte[] output = md.digest();
		return output;
	}

	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {

		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String makeSecret(String s) {
		if (s == null || s.length() == 0)
			return null;
		byte[] bytes = s.getBytes();
		return bytesToHex(calcSha1(bytes));
	}

	private static String sorn(String s) {
		if (s == null)
			return null;
		if (s.trim().length() == 0)
			return null;
		return s.trim();
	}

	public static Map<String, Object> jwtVerify(String token, String secret) {
		final JWTVerifier verifier = new JWTVerifier(secret);
		try {
			Map<String, Object> map = verifier.verify(token);
			return map;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao verificar token JWT", e);
		}
	}

	public static User assertUserCorrente() {
		User user = getUserCorrente();
		if (user == null)
			throw new RuntimeException("Usuário não está logado");
		if (!user.admin)
			throw new RuntimeException("Usuário não é administrador");
		return user;
	}

	public static User getUserCorrente() {
		UserService userService = UserServiceFactory.getUserService();
		if (!userService.isUserLoggedIn())
			return null;

		User u = new User();
		u.gmail = userService.getCurrentUser().getEmail().toLowerCase();
		u.admin = userService.isUserAdmin();
		u.logoutUrl = userService.createLogoutURL("/");
		u.loginUrl = userService.createLoginURL("/");
		return u;
	}

}
