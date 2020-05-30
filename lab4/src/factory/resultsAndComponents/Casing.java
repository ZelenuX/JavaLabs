package factory.resultsAndComponents;

public class Casing extends Storable {
    @Override
    public Storable getInstance() {
        return new Casing();
    }
    @Override
    public String getKindName() {
        return "Casing";
    }
    @Override
    public String getFullInfo(){
        return "Casing " + getId();
    }
}
