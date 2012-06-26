package org.openhds.mobileinterop;

import java.util.Map;

import org.springframework.web.util.UriTemplate;

/**
 * Converts a form type to an OpenHDS URL
 */
public class FormTypeConverter {
	
	private final String openhdsUrl;
	private final Map<String, String> mappings;

	public FormTypeConverter(String openhdsUrl, Map<String, String> mappings) {
		this.openhdsUrl = openhdsUrl;
		this.mappings = mappings;
	}
	

	public String getOpenHdsUrlForType(String formType, String uuid) {
		if (!mappings.containsKey(formType)) {
			throw new RuntimeException("No Form Type found for: " + formType);
		}
		
		UriTemplate template = new UriTemplate(openhdsUrl + mappings.get(formType));
		return template.expand(uuid).toString();
	}
}
