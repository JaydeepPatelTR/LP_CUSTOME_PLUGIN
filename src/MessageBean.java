public class MessageBean {
	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getOpen_id() {
		return open_id;
	}

	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}

	public String getCustom_title() {
		return custom_title;
	}

	public void setCustom_title(String custom_title) {
		this.custom_title = custom_title;
	}

	public String getCustom_content() {
		return custom_content;
	}

	public void setCustom_content(String custom_content) {
		this.custom_content = custom_content;
	}



	private String template_id;
	private String open_id;
	private String custom_title;
	private String custom_content;
	
	private I18nResourcesBean[] i18n_resources;
	private ActionsBean[] actions;
	
	public ActionsBean[] getActions() {
		return actions;
	}

	public void setActions(ActionsBean[] actions) {
		this.actions = actions;
	}

	public I18nResourcesBean[] getI18n_resources() {
		return i18n_resources;
	}

	public void setI18n_resources(I18nResourcesBean[] i18n_resources) {
		this.i18n_resources = i18n_resources;
	}

}
