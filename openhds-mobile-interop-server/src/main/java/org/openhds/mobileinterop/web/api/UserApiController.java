package org.openhds.mobileinterop.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/user")
public class UserApiController {
	
	@RequestMapping(value="/authenticate", method=RequestMethod.GET)
	public void authenticateUser(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.flushBuffer();
		} catch (IOException e) { }
	}
}
