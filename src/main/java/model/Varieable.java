package model;

/**
 * Created by guichi on 28/03/2015.
 */
public class Varieable implements Comparable {
    private String name;
    private boolean isPresent;
    private int num;
    //check the combination present in multiple implicants
    private boolean display;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }

    public String getName() {

        return name;
    }

    public Varieable(boolean isPresent, String name) {

        this.isPresent = isPresent;
        this.name = name;
    }

    @Override
    public String toString() {
        return name+"#"+num ;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Varieable varieable = (Varieable) o;

        return !(name != null ? !name.equals(varieable.name) : varieable.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    @Override
    public int compareTo(Object o) {
        String n=((Varieable)o).getName();
        return name.compareTo(n);
    }
}
