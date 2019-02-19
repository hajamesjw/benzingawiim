package com.jamesha.benzinga.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.json.JSONException;

import com.jamesha.benzinga.JsonReader;

public class InputServlet extends GenericServlet {

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String token = request.getParameter("Token");
		PrintWriter out = response.getWriter();
		String output = "";
		try {
			output = JsonReader.checkForInvalidArticles3(token);
		} catch (JSONException e) {
			
			output = e.toString();
		}
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>" + output + "<h1>");
		out.println("</body>");
		out.println("</html>");
	}

}
