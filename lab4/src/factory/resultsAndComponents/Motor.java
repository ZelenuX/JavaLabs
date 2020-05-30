package factory.resultsAndComponents;

public class Motor extends Storable {
    @Override
    public Storable getInstance() {
        return new Motor();
    }
    @Override
    public String getKindName() {
        return "Motor";
    }
    @Override
    public String getFullInfo(){
        return "Motor " + getId();
    }
}
