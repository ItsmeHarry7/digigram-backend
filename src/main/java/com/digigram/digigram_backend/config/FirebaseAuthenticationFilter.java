package com.digigram.digigram_backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FirebaseAuthenticationFilter implements Filter {

	@Override
	public void doFilter(
	        ServletRequest request,
	        ServletResponse response,
	        FilterChain chain
	) throws IOException, ServletException {

	    HttpServletRequest httpRequest = (HttpServletRequest) request;
	    HttpServletResponse httpResponse = (HttpServletResponse) response;

	    if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
	        chain.doFilter(request, response);
	        return;
	    }

	    String path = httpRequest.getRequestURI();

	    // ✅ Public endpoints
	    if (path.contains("/verify") || path.contains("/api/chat")) {
	        chain.doFilter(request, response);
	        return;
	    }

	    String authHeader = httpRequest.getHeader("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Token");
	        return;
	    }

	    String token = authHeader.substring(7);

	    try {
	        FirebaseToken decodedToken =
	                FirebaseAuth.getInstance().verifyIdToken(token);

	        request.setAttribute("uid", decodedToken.getUid());

	    } catch (Exception e) {
	        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
	        return;
	    }

	    chain.doFilter(request, response);
	}
}