package factory.resultsAndComponents;

public abstract class Storable {
    static private Integer globalStorableObgectsCounter = 0;
    private int id;

    {
        id = globalStorableObgectsCounter;
        ++globalStorableObgectsCounter;
    }
    public int getId(){
        return id;
    };

    public abstract Storable getInstance();
    public abstract String getKindName();
    public abstract  String getFullInfo();
}
