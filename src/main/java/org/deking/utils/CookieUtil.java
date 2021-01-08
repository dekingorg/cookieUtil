package org.deking.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;

public class CookieUtil {
	public static final int cookieLifeTime = 1 * 24 * 60 * 60;

	public static final boolean addCookie(HttpServletRequest request, HttpServletResponse response, Object member,
			String... domain) {
		JSONObject j = (JSONObject) JSONObject.toJSON(member);
		for (@SuppressWarnings("rawtypes")
		Map.Entry entry : j.entrySet()) {
			Cookie c = null;
			try {
				String value = entry.getValue().toString();
				if (entry.getKey().toString().startsWith("pass")) {
					value = DigestUtils.md5Hex(value.getBytes("utf-8"));
				}
				c = new Cookie(entry.getKey().toString(), URLEncoder.encode(value, "utf-8"));
			} catch (java.lang.NullPointerException e) {
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (c != null) {
					c.setMaxAge(cookieLifeTime);
					c.setPath(request.getContextPath() + "/");
					if (domain.length != 0) {
						System.out.println("domain[0]：" + domain[0]);
						c.setDomain(domain[0]);
					}
					response.addCookie(c);

				}
			}
		}
		return true;
	}

	public static final boolean removeCookie(HttpServletRequest request, HttpServletResponse response,
			String... domain) {
		Cookie[] cookies = request.getCookies();
		if (hasCookie(request)) {
			for (Cookie cookie : cookies) {
				cookie = new Cookie(cookie.getName(), null);
				cookie.setMaxAge(0);
				cookie.setPath(request.getContextPath() + "/");
				if (domain.length != 0)
					cookie.setDomain(domain[0]);
				response.addCookie(cookie);
			}
		}

		return true;

	}

	public static final boolean hasCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		return cookies != null && !(cookies.length == 1 && cookies[0].getName().equals("JSESSIONID"))
				&& !(cookies.length == 3 && cookies[0].getName().equals("_uab_collina")
						&& cookies[1].getName().equals("_umdata") && cookies[2].getName().equals("rtk")) ? true : false;
	}

	public static final String getCookieValue(HttpServletRequest request, String key) {
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}
		throw new RuntimeException("cant find cookie value!");
	}
}
