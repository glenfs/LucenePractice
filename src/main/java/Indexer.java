import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.store.*;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;

/**
 * Created by GXS8916 on 2/18/2016.
 */
public class Indexer {

    private static Lookup.LookupResult lookupResult;

    public static void main(String[] args)throws Exception
    {
        Directory directory = FSDirectory.open(new File("c://Ford//indexdata//LuceneTestindex12//"));
       // Directory directory = new RAMDirectory();
        //index(directory);
        index_v2(directory);
       // searchSuggestions();
        //search(directory);

        search_v2();
    }

    public static void search_v2()throws IOException
    {
        AnalyzingSuggester suggester = new AnalyzingSuggester(new StandardAnalyzer(),new StandardAnalyzer(),0|0,256,-1,true);
        File path=new File("C://Ford//BrandCategoryData//storedIndex.suggester");
        InputStream is=new FileInputStream(path);
        suggester.load(is);
       // lookup(suggester, "drill", "US");
        //lookup(suggester, "hammer", "US");
        is.close();

        long startTime = new Date().getTime();
        lookup(suggester, "hammer", "US");

        long endTime = new Date().getTime();
        System.out.println("It takes " + (endTime - startTime)+ " milliseconds.");

    }

public static void search(Directory directory)throws Exception
{
    AnalyzingInfixSuggester suggester = new AnalyzingInfixSuggester(Version.LUCENE_4_10_0, directory, new StandardAnalyzer());

    boolean load = suggester.load(new FileInputStream("C:\\Ford\\BrandCategoryData\\SuggestorBuildfile.sug"));
    //lookup(suggester, "hamm", "US");
}

    private static void index_v2(Directory directory) throws Exception {
        ArrayList<String> skipList =new ArrayList<String>();
        ArrayList<TypeAheadCorpus> typeAheadCorpuses = new ArrayList<TypeAheadCorpus>();
        //AnalyzingInfixSuggester suggester = new AnalyzingInfixSuggester(Version.LUCENE_4_10_0, directory, new StandardAnalyzer());

        AnalyzingSuggester suggester = new AnalyzingSuggester(new StandardAnalyzer(),new StandardAnalyzer(),0|0,256,-1,true);

        FileReader fr =null;

        try {
            //	fReader = new FileReader("c://Ford//ggg4.txt");
            fr = new FileReader("c://Ford//BrandCategoryData//skip_terms.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader bfr = new BufferedReader(fr);
        String l;

        while ((l = bfr.readLine()) != null)
        {
            skipList.add(l.toLowerCase().trim());
        }

        FileReader fReader = null;
        try {
            //	fReader = new FileReader("c://Ford//ggg4.txt");
            fReader = new FileReader("c://Ford//BrandCategoryData//Category_Nav4a.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader bufReader2 = new BufferedReader(fReader);
        String line;
        ArrayList<Float> weightList=new ArrayList<Float>();

        while ((line = bufReader2.readLine()) != null)
        {
            String[] lineSplit= line.split("\\t");
            //  String[] lineSplit2= lineSplit[1].split("~");
            //   weightList.add(Float.parseFloat(lineSplit2[1]));
            weightList.add(Float.parseFloat(lineSplit[2]));
        }


        Collections.sort(weightList);
        float min=weightList.get(0);
        float max=weightList.get(weightList.size() - 1);
        float maxMinusMin=max-min;


        try {
            //fReader = new FileReader("c://Ford//ggg4.txt");
            fReader = new FileReader("c://Ford//BrandCategoryData//Category_Nav4a.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader bufReader = new BufferedReader(fReader);

        while ((line = bufReader.readLine()) != null)
        {
            String[] lineSplit= line.split("\\t");
            // String[] lineSplit2= lineSplit[1].split("~");
            String category="";

            if(!skipList.contains(lineSplit[0].toLowerCase().trim()))
            {

                if(lineSplit.length>3)
                {
                    category=lineSplit[3];
                }
                //System.out.println("lineSplit2[0]="+lineSplit2[0]);
               // Document doc=new Document();

                float xval=Float.parseFloat(lineSplit[2]);
                //multiplying the below normalized value by 2 to get values in the range 1 to 2 (2-1=1) normalization between any arbitrary point a,b is a+((x-xmin)(b-a)/xmax-xmin)
                //float normalizedFieldWeight=1+(((xval-min)*(1))/maxMinusMin);
                float normalizedFieldWeight=(xval-min)/maxMinusMin; //normalize between 0-1

                TypeAheadCorpus  t=new TypeAheadCorpus(lineSplit[0],category,lineSplit[1],normalizedFieldWeight);
                typeAheadCorpuses.add(t);
            }
        }
        suggester.build(new TypeAheadCorpusIterator(typeAheadCorpuses.iterator()));
        lookup(suggester, "hamm", "US");
        //suggester.refresh();
        lookup(suggester, "drill", "US");

        File path=new File("C://Ford//BrandCategoryData//storedIndex.suggester");
    OutputStream os=new FileOutputStream(path);
        suggester.store(os);
        os.close();

        InputStream is=new FileInputStream(path);
        suggester.load(is);
        is.close();



   /* String input;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
        System.out.println("Enter search Query : ");
        input = br.readLine();
            long startTime = new Date().getTime();
            lookup(suggester, input, "US");

            long endTime = new Date().getTime();
            System.out.println("It takes " + (endTime - startTime)+ " milliseconds.");
    }*/

        //boolean load = suggester.load(new FileInputStream("C:\\Ford\\BrandCategoryData\\SuggestorBuildfile.sug"));
        //lookup(suggester, "hamm", "US");
    }

    private static void lookup(AnalyzingSuggester suggester, String name,
                               String region) {
        try {
            List<Lookup.LookupResult> results;
            HashSet<BytesRef> contexts = new HashSet<BytesRef>();
            contexts.add(new BytesRef(region.getBytes("UTF8")));
            // Do the actual lookup.  We ask for the top 10 results.
           // results = suggester.lookup(name, contexts, 10, true, false);
         //   results = suggester.lookup(name,contexts,true,10);
            results = suggester.lookup(name,null,false,10);
            System.out.println("-- \"" + name + "\" (" + region + "):");
            for (Lookup.LookupResult result : results) {
                System.out.println(result.key);
                TypeAheadCorpus p = getTypeAhead(result);
                if (p != null) {
                    System.out.println("  # Term: " + p.getTerm());
                    System.out.println("  # Weight: " + p.getWeight());
                }
            }
        } catch (IOException e) {
            System.err.println("Error");
        }
    }

    // Deserialize a Product from a LookupResult payload.
    private static TypeAheadCorpus getTypeAhead(Lookup.LookupResult result)
    {
        try {
            BytesRef payload = result.payload;
            if (payload != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(payload.bytes);
                ObjectInputStream in = new ObjectInputStream(bis);
                TypeAheadCorpus p = (TypeAheadCorpus) in.readObject();
                return p;
            } else {
                return null;
            }
        } catch (IOException|ClassNotFoundException e) {
            throw new Error("Could not decode payload :(");
        }
    }
    private static void index(Directory directory) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_0, new StandardAnalyzer());

        IndexWriter writer = new IndexWriter(directory, config);
        ArrayList<String> skipList =new ArrayList<String>();

        FileReader fr =null;

        try {
            //	fReader = new FileReader("c://Ford//ggg4.txt");
            fr = new FileReader("c://Ford//BrandCategoryData//skip_terms.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader bfr = new BufferedReader(fr);
        String l;

        while ((l = bfr.readLine()) != null)
        {
            skipList.add(l.toLowerCase().trim());
        }

        FileReader fReader = null;
        try {
            //	fReader = new FileReader("c://Ford//ggg4.txt");
            fReader = new FileReader("c://Ford//BrandCategoryData//Category_Nav4a.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BufferedReader bufReader2 = new BufferedReader(fReader);
        String line;
        ArrayList<Float> weightList=new ArrayList<Float>();

        while ((line = bufReader2.readLine()) != null)
        {
            String[] lineSplit= line.split("\\t");
          //  String[] lineSplit2= lineSplit[1].split("~");
         //   weightList.add(Float.parseFloat(lineSplit2[1]));
            weightList.add(Float.parseFloat(lineSplit[2]));
        }


        Collections.sort(weightList);
        float min=weightList.get(0);
        float max=weightList.get(weightList.size() - 1);
        float maxMinusMin=max-min;


        try {
            //fReader = new FileReader("c://Ford//ggg4.txt");
            fReader = new FileReader("c://Ford//BrandCategoryData//Category_Nav4a.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedReader bufReader = new BufferedReader(fReader);

        while ((line = bufReader.readLine()) != null)
        {
            String[] lineSplit= line.split("\\t");
           // String[] lineSplit2= lineSplit[1].split("~");
            String category="";

            if(!skipList.contains(lineSplit[0].toLowerCase().trim()))
            {
           /*     if(lineSplit2.length>2)
                {
                    category=lineSplit2[2];
                }*/

                if(lineSplit.length>3)
                {
                    category=lineSplit[3];
                }
                //System.out.println("lineSplit2[0]="+lineSplit2[0]);
                Document doc=new Document();

                FieldType titleFieldType = new FieldType();
                titleFieldType.setIndexOptions(org.apache.lucene.index.FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                titleFieldType.setIndexed(true);
                titleFieldType.setStored(true);
                titleFieldType.setTokenized(false);


                Field f=new Field("term", lineSplit[0], titleFieldType);

                float xval=Float.parseFloat(lineSplit[2]);
                //multiplying the below normalized value by 2 to get values in the range 1 to 2 (2-1=1) normalization between any arbitrary point a,b is a+((x-xmin)(b-a)/xmax-xmin)
                //float normalizedFieldWeight=1+(((xval-min)*(1))/maxMinusMin);
                float normalizedFieldWeight=(xval-min)/maxMinusMin; //normalize between 0-1
                f.setBoost(normalizedFieldWeight);

                doc.add(f);
                doc.add(new TextField("phrase", lineSplit[0], Field.Store.NO));
                //doc.add(new TextField("term", lineSplit[0],Store.YES));
                //doc.add(f);
                //doc.add(new FloatField("weight", Float.parseFloat(lineSplit[1]), Store.NO));
                doc.add(new StringField("cat", category, Field.Store.YES));
                doc.add(new TextField("payload", lineSplit[1], Field.Store.YES));
doc.add(new IntField("weight",33,Field.Store.NO));

                writer.addDocument(doc);
            }
        }
        writer.close();
    }

    public static void searchSuggestions() throws IOException {
        Directory directory = FSDirectory.open(new File("c://Ford//indexdata//LuceneTestindex8//"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Dictionary dictionary = new LuceneDictionary(indexReader, "term");

       // AnalyzingInfixSuggester analyzingSuggester = new AnalyzingInfixSuggester(Version.LUCENE_4_10_0,directory,new StandardAnalyzer());
        AnalyzingSuggester analyzingSuggester = new AnalyzingSuggester(new StandardAnalyzer());
        analyzingSuggester.build(dictionary);

        List<Lookup.LookupResult> lookupResultList = analyzingSuggester.lookup("air cond", false, 20);

        for (Lookup.LookupResult lookupResult : lookupResultList) {
            System.out.println(lookupResult.key + ": " + lookupResult.value);

        }
    }
}
