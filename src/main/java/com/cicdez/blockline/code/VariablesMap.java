package com.cicdez.blockline.code;

import java.util.HashMap;
import java.util.Map;

public class VariablesMap {
    private final Map<Integer, Object> variables = new HashMap<>();
    public final CodeSession session;
    
    public VariablesMap(CodeSession session) {
        this.session = session;
    }
    
    public void putVariable(int name, Object obj) {
        Utils.log("Create Variable: " + name + "=" + obj);
        variables.put(name, obj);
    }
    public Object getVariable(int name) {
        return variables.getOrDefault(name, null);
    }
    
    public boolean hasVariable(int name) {
        return variables.containsKey(name);
    }
}
