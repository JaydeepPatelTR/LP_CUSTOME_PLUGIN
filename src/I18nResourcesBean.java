public class I18nResourcesBean {

	private String locale;
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public boolean isIs_default() {
		return is_default;
	}
	public void setIs_default(boolean is_default) {
		this.is_default = is_default;
	}

	private boolean is_default;
	private Texts texts;
	
	public Texts getTexts() {
		return texts;
	}
	public void setTexts(Texts texts) {
		this.texts = texts;
	}
	
}
