package io.choerodon.hap.core.util;

/**
 * declare Holder as final to hold an item.
 *
 * @author shengyang.zhou@hand-china.com
 */
public class Holder<T> {
    public T item;

    public Holder() {

    }

    public Holder(T object) {
        this.item = object;
    }

}
