package util;

import model.Varieable;

import java.util.Collections;
import java.util.List;

/**
 * Created by guichi on 30/05/15.
 */
public class VariableCombination {
    List<Varieable> varieableList;
    public VariableCombination(List<Varieable> v)
    {
        varieableList=v;
        Collections.sort(varieableList);
    }


}
