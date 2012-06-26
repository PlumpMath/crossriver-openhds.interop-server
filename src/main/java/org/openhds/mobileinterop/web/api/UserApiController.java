package org.openhds.mobileinterop.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.AuditDao;
import org.openhds.mobileinterop.model.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/user")
public class UserApiController {

	private AuditDao audit;

	@Autowired
	public UserApiController(AuditDao audit) {
		this.audit = audit;
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.GET)
	public void authenticateUser(HttpServletResponse response) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		audit.addAudit(Audit.now("Authenticated: " + name));
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.flushBuffer();
		} catch (IOException e) {
		}
	}
}
