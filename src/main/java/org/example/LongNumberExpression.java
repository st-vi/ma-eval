package org.example;


import de.vill.model.Feature;
import de.vill.model.building.VariableReference;
import de.vill.model.expression.Expression;
import de.vill.model.expression.NumberExpression;

import java.util.*;

public class LongNumberExpression extends Expression {

    private long number;

    public long getNumber() {
        return this.number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public LongNumberExpression(long number) {
        this.number = number;
    }

    public String toString() {
        return this.toString(true, "");
    }

    public String toString(boolean withSubmodels, String currentAlias) {
        return Long.toString(this.number);
    }

    public List<Expression> getExpressionSubParts() {
        return Collections.emptyList();
    }

    public void replaceExpressionSubPart(Expression oldSubExpression, Expression newSubExpression) {
        if (oldSubExpression instanceof LongNumberExpression && ((LongNumberExpression)oldSubExpression).getNumber() == this.number && newSubExpression instanceof LongNumberExpression) {
            this.number = ((LongNumberExpression)newSubExpression).number;
        }

    }

    public double evaluate(Set<Feature> selectedFeatures) {
        return this.number;
    }

    public int hashCode() {
        int prime = 1;
        int result = super.hashCode();
        result = 31 * result + Objects.hash(new Object[]{this.number});
        return result;
    }

    public int hashCode(int level) {
        return 31 * level + Double.hashCode(this.number);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            LongNumberExpression other = (LongNumberExpression)obj;
            return this.number == other.number;
        } else {
            return false;
        }
    }

    public List<VariableReference> getReferences() {
        return new ArrayList();
    }

    public String getReturnType() {
        return "number";
    }

    public Expression clone() {
        return new NumberExpression(this.getNumber());
    }
}
