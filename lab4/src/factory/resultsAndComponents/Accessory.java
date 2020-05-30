package factory.resultsAndComponents;

public class Accessory extends Storable {
    @Override
    public Storable getInstance() {
        return new Accessory();
    }
    @Override
    public String getKindName() {
        return "Accessory";
    }
    @Override
    public String getFullInfo(){
        return "Accessory " + getId();
    }
}
