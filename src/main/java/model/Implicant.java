package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by guichi on 28/03/2015.
 */
public class Implicant implements Iterator<Varieable>{


    private int size=0;

    public int getSize() {
        return size;
    }

    private int variableIndex=0;
    private List<Varieable> varieables;
    private List<Varieable> positiveVariable;
    private List<Varieable> negativeVariable;

    private boolean allNegetive=true;
    private boolean allPositive=true;

    private boolean isSingle=true;

    public Implicant()
    {
        varieables=new ArrayList<>();
        positiveVariable=new ArrayList<>();
        negativeVariable=new ArrayList<>();

    }

    public boolean isAllPositive() {
        return allPositive;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public List<Varieable> getPositiveVariable() {
        return positiveVariable;
    }

    public List<Varieable> getNegativeVariable() {
        return negativeVariable;
    }

    public void add(Varieable v)
    {
        varieables.add(v);
        if (v.isPresent())
        {
            positiveVariable.add(v);
            allNegetive=false;
            size++;
            if (size>=2)
            {
                isSingle=false;

            }
        }
        if (!v.isPresent())
        {
            allPositive=false;
            negativeVariable.add(v);


        }
    }

    @Override
    public String toString() {
        return "Should display : "+!allNegetive+ " Implicant{" +
                "varieables=" + varieables +
                '}';
    }

    public List<Varieable> getVarieables() {
        return varieables;
    }

    public boolean isAllNegetive() {
        return allNegetive;
    }

    public void setVarieables(List<Varieable> varieables) {
        this.varieables = varieables;
    }

    @Override
    public boolean hasNext() {
        if (variableIndex>=varieables.size())
        return false;

        else
            return true;
    }

    @Override
    public Varieable next() {
        Varieable tmp= varieables.get(variableIndex);
        variableIndex++;
        return tmp;

    }
}
