package io.choerodon.hap.system.dto;

/**
 * 系统信息.
 *
 * @author qiang.zeng
 * @since 2018/12/26.
 */
public class SystemInfo {
    private String logoImageSrc;
    private String faviconImageSrc;
    private String title;

    public String getLogoImageSrc() {
        return logoImageSrc;
    }

    public void setLogoImageSrc(String logoImageSrc) {
        this.logoImageSrc = logoImageSrc;
    }

    public String getFaviconImageSrc() {
        return faviconImageSrc;
    }

    public void setFaviconImageSrc(String faviconImageSrc) {
        this.faviconImageSrc = faviconImageSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
