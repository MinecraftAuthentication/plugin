package me.minecraftauth.plugin.common.feature.gatekeeper;

import lombok.Getter;

public class Expression extends com.udojava.evalex.Expression {

    @Getter int successCount = 0;

    public Expression(String expression) {
        super(expression);
    }

    public int incrementSuccessCount() {
        return ++successCount;
    }

}
