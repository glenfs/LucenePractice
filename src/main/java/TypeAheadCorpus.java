/**
 * Created by GXS8916 on 2/18/2016.
 */
public class TypeAheadCorpus implements java.io.Serializable
{
private String term  ;
    private String categorylist;
    private String payload;
    private float weight;

    public TypeAheadCorpus(String t, String cList, String pLoad, float w)
    {
        this.term=t;
        this.categorylist=cList;
        this.payload=pLoad;
        this.weight=w;
    }

    public String getTerm()
    {
        return term;
    }

    public float getWeight()
    {
        return weight;
    }
}
