package com.cxylk.agent.filter;

import com.cxylk.agent.IFilter;
import com.cxylk.agent.common.JsonUtil;

import java.io.Serializable;

/**
 * @author cxylk
 */
public class JSONFormat implements IFilter {
    @Override
    public Object doFilter(Object value) {
        if (value == null)
            return null;
        else if (!(value instanceof Serializable)) {
            return null;
        }
        return JsonUtil.toJson(value);
    }
}
