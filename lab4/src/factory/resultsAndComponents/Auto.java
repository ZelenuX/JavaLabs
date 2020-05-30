package factory.resultsAndComponents;

public class Auto extends Storable {
    private Casing casing;
    private Motor motor;
    private Accessory accessory;

    public Auto(Casing casing, Motor motor, Accessory accessory){
        this.casing = casing;
        this.motor = motor;
        this.accessory = accessory;
    }
    @Override
    public Storable getInstance() {
        return new Auto(new Casing(), new Motor(), new Accessory());
    }
    @Override
    public String getKindName() {
        return "Auto";
    }
    @Override
    public String getFullInfo(){
        return "Auto " + getId() + " (Casing: " + casing.getId() + ", Motor: " + motor.getId() + ", Accessory: " + accessory.getId() + ")";
    }
}
