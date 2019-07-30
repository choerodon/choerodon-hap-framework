package io.choerodon.hap.function.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 菜单对象.
 *
 * @author chenjingxiong
 */
public class MenuItem implements Comparable<MenuItem>, Serializable {
    private static final long serialVersionUID = -1412546289282861225L;

    private List<MenuItem> children;

    private boolean expand = false;

    private String functionCode;

    private String icon;

    private Long id;

    private Boolean ischecked;

    @JsonIgnore
    private MenuItem parent;

    private long score;

    private Long shortcutId;

    private String text;

    private String url;

    private String symbol;

    @Override
    public int compareTo(MenuItem o) {
        return (int) (this.score - o.score);
    }

    public List<MenuItem> getChildren() {
        return children;
    }

    public String getIcon() {
        return icon;
    }

    public Long getId() {
        return id;
    }

    public Boolean getIschecked() {
        return ischecked;
    }

    public MenuItem getParent() {
        return parent;
    }

    public long getScore() {
        return score;
    }

    public Long getShortcutId() {
        return shortcutId;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setChildren(List<MenuItem> children) {
        this.children = children;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIschecked(Boolean ischecked) {
        this.ischecked = ischecked;
    }

    public void setParent(MenuItem parent) {
        this.parent = parent;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setShortcutId(Long shortcutId) {
        this.shortcutId = shortcutId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
