import com.fasterxml.jackson.annotation.JsonProperty;

public class Texts {
	@JsonProperty("@i18n@1")
	private String i18n1;
	public String getI18n1() {
		return i18n1;
	}
	public void setI18n1(String i18n1) {
		this.i18n1 = i18n1;
	}
	public String getI18n2() {
		return i18n2;
	}
	public void setI18n2(String i18n2) {
		this.i18n2 = i18n2;
	}
	@JsonProperty("@i18n@2")
	private String i18n2;
	
	@JsonProperty("@i18n@3")
	private String i18n3;
	
	public String getI18n3() {
		return i18n3;
	}
	public void setI18n3(String i18n3) {
		this.i18n3 = i18n3;
	}
}
